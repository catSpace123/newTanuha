package com.tanhua.manage.controller;

import com.tanhua.manage.service.SummaryService;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import com.tanhua.manage.vo.AnalysisUsersVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 概要统计信息控制层
 */
@RestController
@RequestMapping("/dashboard")
public class AnalysisController {

    @Autowired
    private SummaryService summaryService;

    /**
     * 概要统计信息
     */
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public ResponseEntity summary() {
        AnalysisSummaryVo analysisSummaryVo = summaryService.summary();

        return ResponseEntity.ok(analysisSummaryVo);
    }

    /**
     * 新增、活跃用户、次日留存率
     *
     * @param sd   开始时间
     * @param ed   结束时间
     * @param type 类型  101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity getUsers(@RequestParam("sd") long sd, @RequestParam("ed") long ed, @RequestParam("type") Integer type) {

        AnalysisUsersVo analysisUsersVo = summaryService.getUsers(sd, ed, type);

        return ResponseEntity.ok(analysisUsersVo);
    }
}

