package com.tanhua.manage.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.exception.BusinessException;
import com.tanhua.manage.mapper.AdminMapper;
import com.tanhua.manage.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    private static final String CACHE_KEY_CAP_PREFIX = "MANAGE_CAP_";
    public static final String CACHE_KEY_TOKEN_PREFIX="MANAGE_TOKEN_";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 保存生成的验证码
     * @param uuid
     * @param code
     */
    public void saveCode(String uuid, String code) {
        String key = CACHE_KEY_CAP_PREFIX + uuid;
        // 缓存验证码，5分钟后失效
        redisTemplate.opsForValue().set(key,code, Duration.ofMinutes(5));
    }

    /**
     * 获取登陆用户信息
     * @return
     */
    public Admin getByToken(String authorization) {
        String token = authorization.replaceFirst("Bearer ","");
        String tokenKey = CACHE_KEY_TOKEN_PREFIX + token;
        String adminString = (String) redisTemplate.opsForValue().get(tokenKey);
        Admin admin = null;
        if(StringUtils.isNotEmpty(adminString)) {
            admin = JSON.parseObject(adminString, Admin.class);
            // 延长有效期 30分钟
            redisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        }
        return admin;
    }

    /**
     * 登录验证
     * @param param
     * @return
     */
    public String login(Map<String, String> param) {
        //获取参数
        String username = param.get("username");
        String password = param.get("password");
        String verificationCode = param.get("verificationCode");
        String uuid = param.get("uuid");

        //判断验证码是否正确或失效
        //redis获取验证码
        if(StringUtils.isEmpty(verificationCode) || StringUtils.isEmpty(uuid)){
            throw new BusinessException("验证码为空");
        }

        String code = (String) redisTemplate.opsForValue().get(CACHE_KEY_CAP_PREFIX + uuid);
        if( StringUtils.isEmpty(code) || !code.equalsIgnoreCase(verificationCode)){
            throw new BusinessException("验证码校验失败");
        }

        //当验证码验证通过就删除验证码
        redisTemplate.delete(CACHE_KEY_CAP_PREFIX + uuid);
        //判断用户名密码是否为空
        if(StringUtils.isEmpty(username)|| StringUtils.isEmpty(password)){
            throw new BusinessException("用户名或密码不能为空");
        }
        //判断用户名是否正确   //查询数据库
        Admin admin = query().eq("username", username).one();
        if(admin == null){
            throw new BusinessException("用户名不正确");
        }
        //判断密码是否正确
        if(!admin.getPassword().equals(SecureUtil.md5(password))){
            throw new BusinessException("密码不正确");
        }
        //当用户名密码都正确的时候生成token
        String token = jwtUtils.createJWT(username, admin.getId());

        String adminStr = JSON.toJSONString(admin);
        //存入redis
        redisTemplate.opsForValue().set(CACHE_KEY_TOKEN_PREFIX+token,adminStr);


        return token;
    }

    /**
     * 退出
      * @param token
     */
    public void logout(String token) {

        //从redis删除token
        redisTemplate.delete("CACHE_KEY_TOKEN_PREFIX"+token);
    }
}
