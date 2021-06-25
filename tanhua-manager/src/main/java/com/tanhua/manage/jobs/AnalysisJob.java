package com.tanhua.manage.jobs;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 统计分析任务类
 */
@Component
public class AnalysisJob {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private SummaryService summaryService;

    /**
     * 定时任务统计方法
     */

    @Scheduled(cron = "0 0 * * * ?") //没5秒统计写入一次
    public void analysisJob(){
        //先查询日志表 得到 （每日新增记录数  ，活跃用户数，次日留存用户数 登录次数）


        String dateStr = DateUtil.formatDate(new Date());//转化为字符串


        DateTime date = DateUtil.parse(dateStr);

        String toDateStr = DateUtil.offsetDay(date, -1).toDateStr();  //昨天

        //每日新增记录数(注册)
        Integer numRegistered = logMapper.queryCount("0102", dateStr);

        //登录用户数
        Integer numLogin = logMapper.queryCount("0101", dateStr);

        //今日活跃用户数
        Integer numActive = logMapper.numActive(dateStr);

        //次日留存用户
        Integer numRetention1d = logMapper.numRetention1d("0102",dateStr,toDateStr);

        //先查询统计表是否有记录
        QueryWrapper<AnalysisByDay> query = new QueryWrapper<>();
            query.eq("record_date",date);
        AnalysisByDay analysisByDay = summaryService.getOne(query);
        Date date1 = new Date();
        //等于null表示当天没有记录新增一条记录
        if(analysisByDay == null){
            System.out.println("是新用户啦");
            analysisByDay = new AnalysisByDay();   //创建对象

            analysisByDay.setRecordDate(date);
            analysisByDay.setNumRegistered(numRegistered);
            analysisByDay.setNumActive(numActive);
            analysisByDay.setNumLogin(numLogin);
            analysisByDay.setNumRetention1d(numRetention1d);

            summaryService.save(analysisByDay);
        }else{
            System.out.println("来更新啦");
            //如果有记录就更新当前记录
            analysisByDay.setNumRegistered(numRegistered);
            analysisByDay.setNumActive(numActive);
            analysisByDay.setNumLogin(numLogin);
            analysisByDay.setNumRetention1d(numRetention1d);

            summaryService.updateById(analysisByDay);
        }

    }

    public static void main(String[] args) {
        String dateStr = DateUtil.date().toDateStr();
        DateTime dateTime = DateUtil.parse(dateStr);

        System.out.println(dateTime);
    }
}
