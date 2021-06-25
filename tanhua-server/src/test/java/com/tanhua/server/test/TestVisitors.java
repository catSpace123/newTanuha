package com.tanhua.server.test;

import com.tanhua.domain.db.Visitor;

import com.tanhua.dubbo.api.mong.VisitorsApi;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
public class TestVisitors {

    @Autowired
    private VisitorsApi visitorsApi;

    /*@Resource
    private SetOperations<String,String> setOperations;*/

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void testSave(){
        for (int i = 0; i < 20; i++) {
            Visitor visitor = new Visitor();
            visitor.setFrom("首页");
            visitor.setUserId(10022l);//用户id
            visitor.setScore(77d);
            visitor.setVisitorUserId(RandomUtils.nextLong(11,50));
            this.visitorsApi.save(visitor);
        }
        System.out.println("ok");
    }



   /* @Test
    public void get(){


        //先从redis获取是否有已经获取过的语音
        RedisOperations<String, String> operations = setOperations.getOperations();
        setOperations.add("QueryVoice","jdsifjidjfijgi");
        //获取set集合中的元素
        Set<String> members = setOperations.members("QueryVoice");
        for (String member : members) {
            System.out.println(member);
        }
    }*/

    @Test
    public void get1(){

       redisTemplate.opsForSet().add("dkfj", "hdfh");

        Set<String> mo = redisTemplate.opsForSet().members("dkfj");
        for (String o : mo) {
            System.out.println(o);
        }
    }





}