package com.tanhua.server.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.exception.TanhuaException;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo2.Voice;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.VoiceVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.VoiceApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 桃花传音业务层
 */
@Service
public class VoiceService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private VoiceApi voiceApi;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private UserInfoApi userInfoApi;

    /**
     * 语音上传
     *
     * @param soundFile
     */
    public void SaveVoice(MultipartFile soundFile) {
        //调用fastDfs 存储语音
        //调用fastdfs获取存储语音的位置
        try {

            //获取当前用户的id
            Long currendUserId = UserHolder.getUserId();
            //到redis获取发语音的次数
            String count = (String) redisTemplate.opsForValue().get("SaveVoice" + currendUserId);
            Integer newCount;
            if(StringUtils.isEmpty(count)){
                 newCount = 0;
            }else{
                newCount = Integer.valueOf(count);
            }


            if(newCount < 3){
            String originalFilename = soundFile.getOriginalFilename();
            String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            StorePath path = fastFileStorageClient.uploadFile(soundFile.getInputStream(), soundFile.getSize(), substring, null);
            String filePath = fdfsWebServer.getWebServerUrl() + path.getFullPath();
            System.out.println(filePath + "音频输入的位置");

            //调用服务提供者保存语音的url
            //先根据用户id查询userinfo表查询发布语音的用户id
            UserInfo userInfo = userInfoApi.findByUserId(currendUserId);
            //获取性别
            String gender = userInfo.getGender();
            voiceApi.SaveVoice(filePath, currendUserId, gender);
                //调用方法获取过期时间的毫秒值
                long time = getTime();
                redisTemplate.opsForValue().set("SaveVoice" + currendUserId,String.valueOf(newCount + 1),time,TimeUnit.MILLISECONDS);

               // redisTemplate.opsForValue().increment("jd").ex

            }else{
                throw new TanhuaException(ErrorResult.error("10008","今日次数已用完"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取语音
     *
     * @return
     */
    public VoiceVo QueryVoice() {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();

        String dateStr = DateUtil.formatDate(new Date());
        DateTime date = DateUtil.parse(dateStr);
        //先从redis查询该用户今日获取语音的次数
        Object count = redisTemplate.opsForValue().get("voice" + currentUserId + dateStr);

        if (count == null) {
            count = 0;
        }
        //先根据用户id查询userinfo表查询发布语音的用户id
        UserInfo userInfo = userInfoApi.findByUserId(currentUserId);
        //获取性别
        String gender = userInfo.getGender();
        //调用服务提供者获取语音
        Voice voice = voiceApi.QueryVoice(currentUserId, gender);

        if (voice == null) {
           return null;
        }


        VoiceVo voiceVo = new VoiceVo();

        long userId = voice.getUserId();
        //如果不为空就调用userinfo 查询语音的用户基本信息
        UserInfo userInfo1 = userInfoApi.findByUserId(userId);

        //拷贝对象
        BeanUtils.copyProperties(userInfo1, voiceVo);

        //把类型不一致的变量赋值
        voiceVo.setId(userInfo1.getId().intValue());
        voiceVo.setSoundUrl(voice.getVoiceUrl());
        voiceVo.setRemainingTimes(Math.toIntExact(3 - Integer.valueOf(String.valueOf(count))));


        //调用方法
        long time = getTime();
        count = (Integer) count + 1;
        //存入redis  并设置过期时间
        redisTemplate.opsForValue().set("voice" + currentUserId + dateStr, count, time, TimeUnit.MILLISECONDS);

        return voiceVo;
    }




    public long getTime(){
        String dateStr = DateUtil.formatDate(new Date());
        DateTime date = DateUtil.parse(dateStr);
        //获取第二天的时间 目的是为了设置该值的过期时间
        DateTime dateTime = DateUtil.offsetDay(date, 1);//第二天的凌晨
        long time = dateTime.getTime();  //第二天的凌晨毫秒值
        long currentTime = DateUtil.date().getTime();  //当前时间毫秒值

        return time -currentTime;
    }







    public static void main(String[] args) {
        String dateStr = DateUtil.formatDate(new Date());
        //System.out.println(dateStr);
        DateTime date = DateUtil.parse(dateStr);

        DateTime dateTime = DateUtil.offsetDay(date, 1);
        System.out.println(dateTime+"第二天");
        long time = dateTime.getTime();
        System.out.println(time);
        Date date1 = DateUtil.date();
        long time1 = date1.getTime();
        System.out.println( date1 +"");

      //  DateTime dateTime1 = DateUtil.offsetMillisecond(dateTime,);
        System.out.println(time-time1);

        long time2 = DateUtil.date().getTime();
        System.out.println(time2+"ndjnd");

    }
}
