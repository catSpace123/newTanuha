package com.tanhua.server.service;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.commons.exception.TanhuaException;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.utils.GetAgeUtil;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 用户消费层service
 */
@Service
@Slf4j
public class UserService {

    @Reference
    private UserApi userApi;

    @Autowired   //注入redis模板
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsTemplate smsTemplate;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")  //注入存入redis的key需要用的前缀
    private String redisValidateCodeKeyPrefix;

    @Autowired
    private com.tanhua.server.utils.JwtUtils jwtUtils;

    @Value("${tanhua.tokenKey}")
    private String tokenKey;

    @Reference
    private UserInfoApi userInfoApi;

    //注入人脸识别组件
    @Autowired
    private FaceTemplate faceTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    //图片上传组件
    @Autowired
    private OssTemplate ossTemplate;


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 用来发送短信验证码
     * @param phone
     * @return
     */
    public void sendCode(String phone) {

        //从redis获取验证码，如果为空则说明已过期，需要从新生成验证码
        Object redisCode =  redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);
        log.debug("用户redis中的redis的验证码{}",redisCode);

        //如果不为空说明验证码还没有过期
        if (!StringUtils.isEmpty(redisCode)){
            throw new TanhuaException(ErrorResult.duplicate()); //表明验证还没有过期
        }

        //当代码走到这里的时候就说明验证码已过期或是第一次申请验证码
        //生成验证码 //调用random工具类生成6位数的验证码

        String code = RandomStringUtils.randomNumeric(6);
        //调用模板发送短信
        code = "123456";
        Map<String, String> stringMap = smsTemplate.sendValidateCode(phone, code);
        //通过这个模板组件得到发送验证码的结果 如果 结果不为空则表示验证码发送失败，则抛出异常提示用户
        if(stringMap != null){
            throw new TanhuaException(ErrorResult.fail()); //表明验证码发送失败
        }

        //如果验证码发送成功将生成的验证码存入redis   并设置过期时间5分钟  （目的时为了登录的时候，用户会输入验证码 跟 存入redis的验证码对比）
        redisTemplate.opsForValue().set(redisValidateCodeKeyPrefix + phone,code,5, TimeUnit.MINUTES);
        log.debug("存入redis的验证码{}",code);


    }

    /**
     * 校验验证码自动登录
     * @param params
     * @return
     */
    public Map loginVerificationCode(Map<String, String> params) {
        Map<String,Object> map = new HashMap<>();  //用于返回数据的封装


        Boolean isNew = false;  //根据接口文档返回数据  ，如果是flase 就表示不是新用户
        String phone = params.get("phone");
        String verificationCode = params.get("verificationCode");
        log.debug("用户输入的code={}",verificationCode);

            //到redis中取出验证码
            String code = (String) redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);
            log.debug("第二次redis获取到的code +{}",code);
            //如果等于null表示验证码已经失效
            if(StringUtils.isEmpty(code)){
                throw new TanhuaException(ErrorResult.loginError());
            }

                //判断获取到验证码和redis中的验证码是否相等
            if(!(code.equals(verificationCode))){
                //进入后就代表不行等  抛出异常，验证码不相等
                throw new TanhuaException(ErrorResult.validateCodeError());
            }

            //不失消就通过电话号码查询用户
            User user = userApi.findByMobile(phone);
            String  type = "0101";   //用户写入log表的操作类型   默认登录
            //如果等于空就代表是新用户，需要注册
            if(user == null){
                 user = new User();
                user.setMobile(phone);
                //自动生成加密密码（电话号码的后六位 加密）
                user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length()-6)));

                Long id = userApi.save(user);
                log.debug("添加后获取到的id={}",id);
                user.setId(id);
                isNew = true;   //改变这个变量的值为true表示是新用户

                //用来给环信注册用户   将用户id保存到环信
                huanXinTemplate.register(id);
                log.debug("上传环语成功");
                //新用户该默认的操作类型为注册
                type = "0102";

            }

            //调用工具类将生成的token 和判断是否是新用户的值存入map中  相当于以前的session
            String token = jwtUtils.createJWT(phone, user.getId());
        System.out.println(token+"=====生成的tokn");
            //在将token 和用户存入到redis中，当一个用户频繁登录的时间就不用去数据库查询，之间在redis查询即可。
            String jsonString = JSON.toJSONString(user);
            //存入redis 并设置过期时间，当用户俩天登录便清除redis中的值
            redisTemplate.opsForValue().set(tokenKey+token,jsonString,2,TimeUnit.DAYS);

            map.put("token",token);
            map.put("isNew",isNew);

            //登录验证成功后删除验证码
            redisTemplate.delete(redisValidateCodeKeyPrefix + phone);


        /**
         * 操作类型,
         * 0101为登录，0102为注册，0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢，0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
         */
        //获取当前时间
        String date = DateUtil.formatDate(new Date());

        Map<String,String> logMap = new HashMap<>();
            logMap.put("user_id",user.getId().toString());
            logMap.put("type",type);
            logMap.put("log_time",date);
            //向rocketmq发送消息，用户统计

        rocketMQTemplate.convertAndSend("tanhua_log",logMap);
        log.debug("发送消息成功啦");


        return map;
    }


    /**
     * 用来保存完善用户信息
     * @param userInfoVo  接收前端传过来的数据
     * @param //token    //这个参数已经在拦截器统一处理过了，不需要在处理啦
     */
    public void loginReginfoAdd(UserInfoVo userInfoVo) {
            //调用方法从redis获取在注册页面存入的 user
        User user = UserHolder.getUser();
        //3调用服务提供者保存用户信息
        UserInfo userInfo = new UserInfo();
        //4把前端接收的对象赋值给后台的对象
        BeanUtils.copyProperties(userInfoVo, userInfo);
        //5因为用户表和用户信息表用到的id是同一个 ，从token中获取id赋值给userInfo对象
        userInfo.setId(user.getId());
        //根据生日计算年龄
        if(!StringUtils.isEmpty(userInfoVo.getBirthday())){
            int age = GetAgeUtil.getAge(userInfoVo.getBirthday());
            userInfo.setAge(age);
        }
        //6调用服务提供者存数据
        userInfoApi.loginReginfoAdd(userInfo);

    }

    /**
     * 用来在redis中获取user对象
     * @param token
     * @return
     */
    public User getRedisByUser(String token){
        //1从redis中取出token判断用户登录信息是否过期
        String userStr = (String) redisTemplate.opsForValue().get(tokenKey + token);
        //当为空的时候表示登录已过期，
        if (StringUtils.isEmpty(userStr)) {
            return null;
        }
        //添加用户登录过期时间（续期）以用户最后一次操作为准作为最后一次登录的时间判断用户过期时间
        redisTemplate.expire(tokenKey + token,1,TimeUnit.DAYS);
        //2把拿到的userstr转化成对象
        return JSON.parseObject(userStr, User.class);
    }


    /**
     * 用来上传图片人脸识别
     * @param headPhoto
     * @param //token
     */
    public void uplondHead(MultipartFile headPhoto) {
        try {
            //1判断用户是否是登录状态
            User user = UserHolder.getUser();
            //2调用人脸识别组件人脸识别
            if(!faceTemplate.detect(headPhoto.getBytes())){
                //如果不是人像就抛出异常
                throw new TanhuaException(ErrorResult.faceError());
            }
            //3调用图片上传组件上传图片 获取到图片在阿里云的地址
            String upload = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

            //将图片地址存入到数据库(更具用户id更新用户图片头像)

            UserInfo userInfo = new UserInfo();
            userInfo.setAvatar(upload);
            userInfo.setId(user.getId());

            //调用服务提供者存储用户图片
            userInfoApi.updateAvatar(userInfo);

        } catch (IOException e) {
            e.printStackTrace();
            throw new TanhuaException(ErrorResult.error());
        }
    }

    /**
     * 更新用户头像
     * @param headPhoto
     */
    public void updateHead(MultipartFile headPhoto) {
        //1先根据用户id查询出用的信息和头像地址
        UserInfo userInfo = userInfoApi.findByUserId(UserHolder.getUserId());
        //获取旧头像的名称
        String avatar = userInfo.getAvatar();
        //2调用人脸识别识别和图片存储到oss和数据库。（就是之前登录的时候人脸识别和存储的方法）
        uplondHead(headPhoto);


        if(!StringUtils.isEmpty(avatar)){
        //3修改成功后调用方法删除阿里云里面的图片
        ossTemplate.deleteFile(avatar);

            System.out.println("阿里云删除图片成功啦");
        }
    }

    /**
     * 更新手机号码——校验验证码
     * @param verificationCode
     * @return
     */
    public Map<String,Boolean> checkVerificationCode(String verificationCode) {
        //创建map集合用来封装返回的数据
        Map<String,Boolean> map = new HashMap<>();
        String phone = UserHolder.getUser().getMobile();
        //先从redis中查询验证码是否存在
        String code = (String) redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);
        if(StringUtils.isEmpty(code)){
            //如果为空表示验证码已过期
            throw new TanhuaException(ErrorResult.loginError());
        }
        Boolean flag = false;
        //判断验证码是否相等
        if(!code.equals(verificationCode)){
            System.out.println("验证码不相等");
            map.put("verification",flag);
           return map;
        }
        System.out.println("验证码相等啦");
        flag = true;
        map.put("verification",flag);
        return map;
    }

    /**
     * 更新手机号码——接收到电话号码修改
     * @param phone
     */
    public void updatePhoneUserById(String phone,String token) {
        //判断电话号码不能为空
        if(StringUtils.isEmpty(phone)){
            throw new TanhuaException(ErrorResult.error());
        }
        User user = new User();
        user.setId(UserHolder.getUserId());
        user.setMobile(phone);
        userApi.updatePhoneUserById(user);
        //保存成功后在redis中删除token  让用户从新登录
        redisTemplate.delete(tokenKey+token);
    }



}
