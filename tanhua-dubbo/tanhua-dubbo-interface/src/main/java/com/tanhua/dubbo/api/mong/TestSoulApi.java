package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.mongo2.Questiongrade;
import com.tanhua.domain.mongo2.Questions;
import com.tanhua.domain.mongo2.Report;
import com.tanhua.domain.mongo2.UserReport;


import java.util.List;

/**
 * 测灵魂服务提供者接口
 */
public interface TestSoulApi {

    /**
     * 测灵魂试题查询
     * @return
     */
    List<Questions> testSoul(String level);


    /**
     * 查询试题等级
     * @param level  等级
     * @return
     */
    Questiongrade questiongrade(String level);


    /**
     * //根据分数查询添加测试报告
     * @param score
     * @return
     */
    String saveTestSoul(Double score,Long currentUserID);


    /**
     * 查看报告
     * @param reportId
     * @return
     */
    Report queryReport(String reportId);

    //更具当前用户查询报告id
    UserReport queryUserReport(Long userId);

    //在查询报告记录数
    long queryCount(Long userId);
}
