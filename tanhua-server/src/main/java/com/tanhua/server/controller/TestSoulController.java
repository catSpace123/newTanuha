package com.tanhua.server.controller;

import com.tanhua.domain.vo.Answers;
import com.tanhua.domain.vo.ReportVo;
import com.tanhua.domain.vo.TestSoulVo;
import com.tanhua.server.service.TestSoulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
测灵魂控制层
 */
@RestController
@RequestMapping("/testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;


    /**
     * 测灵魂-问卷列表
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity testSoul(){

        List<TestSoulVo> list = testSoulService.testSoul();

        return ResponseEntity.ok(list);
    }

    /**
     * 测灵魂-提交问卷（学生实战）
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity saveTestSoul(@RequestBody Map<String,Object>  param){
        //用list直接接收，报一个错，不能序列化，
        ArrayList<Answers> answers = (ArrayList<Answers>) param.get("answers");
        String reportId = testSoulService.saveTestSoul(answers);
        return ResponseEntity.ok(reportId);
    }

    /**
     * 测灵魂查看报告
     * @param reportId  //报告id
     * @return
     */
    @RequestMapping(value = "/report/{id}",method = RequestMethod.GET)
    public ResponseEntity queryReport(@PathVariable("id") String reportId){

       ReportVo reportVo = testSoulService.queryReport(reportId);
        return ResponseEntity.ok(reportVo);
    }
}
