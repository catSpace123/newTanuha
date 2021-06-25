package com.tanhua.dubbo.api.mongo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tanhua.domain.mongo2.Voice;
import com.tanhua.dubbo.api.mong.VoiceApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.util.CollectionUtils;


import java.util.*;

/**
 * 语音上传服务提供者接口实现类
 */
@Service
public class VoiceApiImpl implements VoiceApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;






    /**
     * 上传语音
     * @param filePath  语音url
     * @param currendUserId  当前用户id
     * @param gender  用户性别
     */
    @Override
    public void SaveVoice(String filePath, Long currendUserId, String gender) {
        Voice voice = new Voice();
        voice.setId(ObjectId.get());
        voice.setUserId(currendUserId);
        voice.setVoiceUrl(filePath);
        voice.setGender(gender);
        voice.setCreateTime(new Date().getTime());
        //封装对象
        //保存信息
        mongoTemplate.insert(voice);
    }


    /**
     * 查询语音
     * @param currentUserId
     * @param gender
     * @return
     */
    @Override
    public Voice QueryVoice(Long currentUserId, String gender) {



        //先从redis获取是否有已经获取过的语音
        //获取set集合中的元素
        Set<String> members = redisTemplate.opsForSet().members("QueryVoice" + currentUserId);


        //当前时间
        String Date= DateUtil.formatDate(new Date());
        long time = DateUtil.parse(Date).getTime();

        //查询表获取语音
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").ne(currentUserId).and("createTime").gt(time).and("gender").ne(gender));
        List<Voice>  voiceList = mongoTemplate.find(query, Voice.class);
        //当redis为空的时候就查询一条记录，直接返回
        if(CollectionUtils.isEmpty(members)){

            //存入集合然后存储redis
            Random random = new Random();

            Voice voice = voiceList.get(random.nextInt(voiceList.size()));

            String idStr = voice.getId().toHexString();

            redisTemplate.opsForSet().add("QueryVoice" + currentUserId,idStr);
            return voice;
        }

        //如果不为空就查询今天的所有语音和redis比较，不相等就添加到redis 然后返回
/*
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").ne(currentUserId).and("createTime").gt(time).and("gender").ne(gender));
        List<Voice> voiceList = mongoTemplate.find(query, Voice.class);*/


       //遍历redis中的id值
        for (String voiceID : members) {
            Random random = new Random();

            Voice voice = voiceList.get(random.nextInt(voiceList.size()));

            String idStr = voice.getId().toHexString();

            //，判断是否相等，不相等的就返回，并存入redis

                if(!voiceID.equals(idStr)) {

                    redisTemplate.opsForSet().add("QueryVoice" + currentUserId,idStr);
                    return voice;
                }
            }

        //如果没有查询到匹配的就返回空
        return null;
    }
}
