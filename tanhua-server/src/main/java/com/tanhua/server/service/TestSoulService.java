package com.tanhua.server.service;


import com.alibaba.fastjson.JSON;
import com.tanhua.domain.mongo2.Questiongrade;
import com.tanhua.domain.mongo2.Questions;
import com.tanhua.domain.mongo2.Report;
import com.tanhua.domain.mongo2.UserReport;
import com.tanhua.domain.vo.*;
import com.tanhua.dubbo.api.mong.TestSoulApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * 测灵魂消费者业务层
 */
@Service
public class TestSoulService {

    @Reference
    private TestSoulApi testSoulApi;



   /* public List<TestSoulVo> testSoul1() {

        ArrayList<QuestionsVo> questionsVos = new ArrayList<>();

        QuestionsVo questionsVo = new QuestionsVo();

        questionsVo.setId("001");
        questionsVo.setQuestion("今天吃早饭了嘛");

        ArrayList<OptionsVo> optionsVo = new ArrayList<>();
      //  questionsVo.setOptions(optionsVo);

        OptionsVo optionsVo1 = new OptionsVo();
        optionsVo1.setId("111");
        optionsVo1.setOption("不想吃");
        optionsVo.add(optionsVo1);


        OptionsVo optionsVo2 = new OptionsVo();
        optionsVo2.setId("222");
        optionsVo2.setOption("吃饭没意思");
        optionsVo.add(optionsVo2);

        questionsVos.add(questionsVo);

        TestSoulVo testSoulVo = new TestSoulVo();

        testSoulVo.setId("789456123000");
        testSoulVo.setName("初级灵魂题");
        testSoulVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png");
        testSoulVo.setLevel("初级");
        testSoulVo.setStar(2);
      //  testSoulVo.setQuestions(questionsVos);
        testSoulVo.setIsLock(0);
        testSoulVo.setReportId("001245");
        return null;
    }
*/
    /**
     * 测灵魂-问卷列表
     * @return
     */
    public List<TestSoulVo> testSoul() {
        Long userId = UserHolder.getUserId();
        //封装返回对象
        List<TestSoulVo> list = new ArrayList<>();
        TestSoulVo testSoulVo1 = getTestSoulVo("初级");
        UserReport userReport1 =  testSoulApi.queryUserReport(userId);
        if(userReport1 != null){
            testSoulVo1.setIsLock(0);
            testSoulVo1.setReportId(userReport1.getId().toHexString());
        }else{
            testSoulVo1.setIsLock(0);
        }




        TestSoulVo testSoulVo2 = getTestSoulVo("中级");

        //更具当前用户查询报告id
        //在查询报告记录数
        long count1  = testSoulApi.queryCount(userId);
        UserReport userReport =  testSoulApi.queryUserReport(userId);
        if(userReport != null ){
            testSoulVo2.setIsLock(0);
            if(count1 >= 2){
                testSoulVo2.setReportId(userReport.getId().toHexString());
            }
        }else{
            testSoulVo2.setIsLock(1);
        }
        //更具当前用户查询报告id
        UserReport  userReport3 =  testSoulApi.queryUserReport(userId);
        //在查询报告记录数
         long count  = testSoulApi.queryCount(userId);

        TestSoulVo testSoulVo3 = getTestSoulVo("高级");
        if(userReport3 != null && count  >= 2){
            testSoulVo3.setIsLock(0);
            if(count == 3){
                testSoulVo3.setReportId(userReport3.getId().toHexString());
            }

        }else{
            testSoulVo3.setIsLock(1);
        }

        list.add(testSoulVo1);
        list.add(testSoulVo2);
        list.add(testSoulVo3);
        return list;
    }

    /**
     *用来查询试题已经试题等级
     * @param level
     * @return
     */
    public TestSoulVo getTestSoulVo(String level){
        //创建返回对象
        TestSoulVo testSoulVo = new TestSoulVo();


        //调用服务提供者，查询试题等级
        Questiongrade questiongrade = testSoulApi.questiongrade(level);
        if(questiongrade == null){
            return null;
        }
        BeanUtils.copyProperties(questiongrade,testSoulVo);

        //调用服务提供者查询测试题
        List<Questions>  questionsList = testSoulApi.testSoul(level);
        if(CollectionUtils.isEmpty(questionsList)){
            return null;
        }


        List<QuestionsVo> questionsVos = new ArrayList<>();
        //遍历查询出来的试题经行封装
        for (Questions questions : questionsList) {
            QuestionsVo questionsVo = new QuestionsVo();

            BeanUtils.copyProperties(questions,questionsVo);
            questionsVo.setId(questions.getId().toHexString());
            questionsVos.add(questionsVo);
        }

        testSoulVo.setId(questiongrade.getId().toHexString());
        testSoulVo.setQuestions(questionsVos);
        return testSoulVo;
    }

    /**
     * 查看报告
     * @param reportId
     * @return
     */
    public ReportVo queryReport(String reportId) {
        ReportVo reportVo = new ReportVo();
       //根据报告id查询报告表
        Report report = testSoulApi.queryReport(reportId);

        if(report == null){
            return null;
        }

        BeanUtils.copyProperties(report,reportVo);


        return reportVo;
    }


    /**
     * 测灵魂-提交问卷
     */

    public String saveTestSoul(List<Answers>  answers) {

        //获取前用户id
        Long currentUserID = UserHolder.getUserId();
        if(CollectionUtils.isEmpty(answers)){
            return null;
        }
        //定义一个分数变量  默认为0
        Double score = 0.0 ;

        String jsonString = JSON.toJSONString(answers);

       List list = JSON.parseObject(jsonString, List.class);

        //遍历选项，算出分数
        for (Object list1 : list) {
         //   Answers answers1 = (Answers )list1;
            String string = JSON.toJSONString(list1);
           Answers answers1 = JSON.parseObject(string, Answers.class);

            switch (answers1.getOptionId()){
                case "A":
                    score += 7.5;
                    break;
                case "B":
                    score += 6.5;
                    break;
                case "C":
                    score += 9.0;
                    break;
                case "D":
                    score += 8.0;
                    break;
                case "E":
                    score += 9.5;
                    break;
                default:
                    score = 0.0;
                    break;
            }
        }

        //根据分数查询报告
     String  reportID = testSoulApi.saveTestSoul(score, currentUserID);

        return reportID;
    }
}
