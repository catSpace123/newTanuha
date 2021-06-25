package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo2.Questiongrade;
import com.tanhua.domain.mongo2.Questions;
import com.tanhua.domain.mongo2.Report;
import com.tanhua.domain.mongo2.UserReport;
import com.tanhua.dubbo.api.mong.TestSoulApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
/**
 * 测灵魂服务提供者接口实现类
 */
@Service
public class TestSoulApiImpl implements TestSoulApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 测灵魂试题查询
     * @return
     */
    @Override
    public List<Questions> testSoul(String level) {

        Query query = new Query();

        query.addCriteria(Criteria.where("level").is(level));
        List<Questions> questionsList = mongoTemplate.find(query,Questions.class);

        return questionsList;
    }


    /**
     * 查询试题等级
     * @param level  等级
     * @return
     */
    @Override
    public Questiongrade questiongrade(String level) {
        //查询等级
        Query query = new Query();
        query.addCriteria(Criteria.where("level").is(level));
        Questiongrade questiongrade = mongoTemplate.findOne(query, Questiongrade.class);
        return questiongrade;
    }



    /**
     * 根据分数添加测试报告返回报告id
     * @param score
     * @return
     */
    @Override
    public String saveTestSoul(Double score,Long currentUserID) {
        //先根据分数查询对应的测试报告并返回报告id
        if(score < 59.0){
            score = 60.0;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("score").lte(score)).with(Sort.by(Sort.Order.desc("score")));//.orOperator(Criteria.where("score").gte(score))
        Report report = mongoTemplate.findOne(query, Report.class);

        if(report == null){
            return null;
        }

        String id = report.getId().toHexString();
        //根据报告id查询是否有记录
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("reportId").is(id).and("userID").is(currentUserID));
        UserReport templateOne = mongoTemplate.findOne(query1, UserReport.class);
        if(templateOne == null){
            //如果为空就添加 //拿到报告id  在存入个人报告表
            UserReport userReport = new UserReport();
            ObjectId objectId = ObjectId.get();
            userReport.setId(objectId);
            userReport.setUserID(currentUserID);
            userReport.setReportId(id);  //详细报告id
            userReport.setDateTime(new Date().getTime()); //时间
            mongoTemplate.insert(userReport);
            return objectId.toHexString();
        }

        //如果存在就修改当前记录
        Update update = new Update();
        update.set("reportId",id);
        mongoTemplate.updateFirst(query1,update,UserReport.class);

        return templateOne.getId().toHexString();
    }



    /**
     * 查看报告
     * @param reportId
     * @return
     */
    @Override
    public Report queryReport(String reportId) {
        //先根据报告id查询userReport  表
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(reportId));
        UserReport one = mongoTemplate.findOne(query, UserReport.class);

        //在根据详细报告id  查询 Report 表
        if(one == null){
            return null;
        }

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is(one.getReportId()));
        Report report = mongoTemplate.findOne(query1, Report.class);
        return report;
    }


    //更具当前用户查询报告id
    @Override
    public UserReport queryUserReport(Long userId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(userId)).with(Sort.by(Sort.Order.desc("dateTime")));
        UserReport userReport = mongoTemplate.findOne(query, UserReport.class);
        return userReport;
    }

    @Override
    public long queryCount(Long userId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userID").is(userId)).with(Sort.by(Sort.Order.desc("dateTime")));
        long count = mongoTemplate.count(query, UserReport.class);
        return count;
    }


}
