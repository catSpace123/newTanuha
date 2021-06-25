package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo2.*;
import com.tanhua.domain.vo.QuestionsVo;
import com.tanhua.domain.vo.ReportVo;
import com.tanhua.domain.vo.TestSoulVo;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(value = SpringRunner.class)
public class TestDemo {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 语音表
     */
    @Test
    public void voice(){
        Voice voice= new Voice();
        voice.setUserId(100l);
        voice.setVoiceUrl("http//");
        voice.setGender("man");

        voice.setCreateTime(new Date().getTime());
        mongoTemplate.insert(voice);
    }


    /**
     * 测灵魂: 试题表
     */
   /* @Test
    public void testQuestions(){

        TestQuestions testQuestions = new TestQuestions();


        testQuestions.setQuestion("??????????");
        testQuestions.setLevel("高级");


        Options options1 = new Options();
        options1.setId("A");
        options1.setOption("日式沙拉酱");

        Options options2 = new Options();
        options2.setId("B");
        options2.setOption("只加盐和胡椒");

        Options options3 = new Options();
        options3.setId("C");
        options3.setOption("法式沙拉酱");


      *//*  Options options4 = new Options();
        options4.setId("D");
        options4.setOption("绿色的");*//*
       *//* Options options5 = new Options();
        options5.setId("E");
        options5.setOption("你平常不做梦");*//*
        Options [] options = {options1,options2,options3};
        testQuestions.setOptions(options);
        mongoTemplate.insert(testQuestions);
    }*/
    /**
     * 评分依据表
     */
    @Test
    public void scoringBasis(){
        ScoringBasis scoringBasis = new ScoringBasis();

        scoringBasis.setOptionScore(88);
        scoringBasis.setQuestionId(001);
        scoringBasis.setOption(0);
        mongoTemplate.insert(scoringBasis);
    }

    /**
     *  试题等级表
     */
    @Test
    public void questiongrade(){

        Questiongrade questiongrade = new Questiongrade();
        questiongrade.setName("高级灵魂题");
        questiongrade.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_03.png");
        questiongrade.setLevel("高级");
        questiongrade.setStar(5);
        questiongrade.setIsLock(1);
        mongoTemplate.insert(questiongrade);
    }


    /**
     * 评分表
     */
    @Test
    public void score(){

        Score score = new Score();
        score.setUserId(1007L);
        score.setScore(88.0);
        score.setLevelId("60ced34309bd9a3620710a8f");
        score.setCreateTime(new Date().getTime());
        mongoTemplate.insert(score);
    }

    /**
     *好友表（喜欢表）
     */
    @Test
    public void userLike(){

        UserLike userLike = new UserLike();
        userLike.setUserId(1007L);
        userLike.setLikeUserId(1008L);
        userLike.setCreated(new Date().getTime());
        mongoTemplate.insert(userLike);
    }

    /**
     *好友表
     */
    @Test
    public void friend(){
        Friend friend = new Friend();
        friend.setUserId(1008L);
        friend.setFriendId(1007L);
        friend.setCreated(new Date().getTime());
        mongoTemplate.insert(friend);
    }

    /**
     * ceshi
     */
  /*  @Test
    public void test(){

        Query query = new Query();
        query.addCriteria(Criteria.where("level").is("初级"));
        List<TestQuestions> testQuestions = mongoTemplate.find(query, TestQuestions.class);
        for (TestQuestions testQuestion : testQuestions) {
            System.out.println(testQuestion);
        }
    }*/
    @Test
    public void test1(){
        Report reportVo = new Report();

        reportVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png");
        reportVo.setConclusion("双鱼型：直率的言辞绝非豪迈地喊出，而是软绵绵的语气缓缓道来犀利的语，看到这你固然以为双鱼白羊城府阴毒，事实全然让人大跌眼镜，神经质的情感本质确是毫无心机，准确的说是不会用心计，直肠子直的平行，即使逼到黄河，壮烈地说，我要暗算你！可笑的是所谓心计都是空想出的带有戏剧性的整人计划，实施起来毫无力度或根本不可能实施，想过也等于用过了。");
        reportVo.setScore(99.0);
        List<Dimensions> list = new ArrayList<>();
        Dimensions dimensions = new Dimensions();
        dimensions.setKey("理性");
        dimensions.setValue("95%");
        list.add(dimensions);

        Dimensions dimensions2 = new Dimensions();
        dimensions2.setKey("外向");
        dimensions2.setValue("91%");
        list.add(dimensions2);

        Dimensions dimensions3 = new Dimensions();
        dimensions3.setKey("判断");
        dimensions3.setValue("98%");
        list.add(dimensions3);

        Dimensions dimensions4 = new Dimensions();
        dimensions4.setKey("抽象");
        dimensions4.setValue("85%");
        list.add(dimensions4);

        reportVo.setDimensions(list);

        mongoTemplate.insert(reportVo);
    }
}
