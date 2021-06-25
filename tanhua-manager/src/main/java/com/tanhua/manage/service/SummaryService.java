package com.tanhua.manage.service;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.errorprone.annotations.Var;
import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.mapper.AnalysisByDayMapper;
import com.tanhua.manage.utils.ComputeUtil;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import com.tanhua.manage.vo.AnalysisUsersVo;
import com.tanhua.manage.vo.DataPointVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 概要统计业务层
 */
@Service
public class SummaryService extends ServiceImpl<AnalysisByDayMapper,AnalysisByDay> {

    /**
     * 概要统计信息
     */
    public AnalysisSummaryVo summary() {

        //创建当前日期
        DateTime dateTime = new DateTime();
        //创建返回对象
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
        analysisSummaryVo.setCumulativeUsers(queryCumulativeUsers()); //累计用户数

        analysisSummaryVo.setActivePassMonth(queryCount(dateTime,-30,"num_active"));//过去30天活跃数

        analysisSummaryVo.setActivePassWeek(queryCount(dateTime,-7,"num_active")); //过去7天活跃用户

        Integer numRegistered = queryCount(dateTime, 0, "num_registered");   //今日新增用户数量
        Integer toNumRegistered = queryCount(dateTime, -1, "num_registered");  //今天昨天新增用户数量

        analysisSummaryVo.setNewUsersToday(numRegistered); //今日新增用户数量
        /**
         * toNumRegistered-numRegistered   相减就得到昨天的数量
         */
        analysisSummaryVo.setNewUsersTodayRate(ComputeUtil.computeRate(numRegistered.longValue(),toNumRegistered.longValue() - numRegistered)); //今日新增用户涨跌率


        //今日登录次数
        Integer numLogin = queryCount(dateTime, 0, "num_login");
        //昨日登录次数
        Integer toNumLogin = queryCount(dateTime, -1, "num_login");
        analysisSummaryVo.setLoginTimesToday(numLogin); //今日登录次数

        analysisSummaryVo.setLoginTimesTodayRate(ComputeUtil.computeRate(numLogin.longValue(),toNumLogin.longValue() - numLogin));//今日登录次数涨跌率


        Integer numActive = queryCount(dateTime, 0, "num_active");  //今日活跃用户
        Integer toNumActive = queryCount(dateTime, -1, "num_active"); //今天昨天活跃用户
        analysisSummaryVo.setActiveUsersToday(numActive);  //今日活跃用户

        analysisSummaryVo.setActiveUsersTodayRate(ComputeUtil.computeRate(numActive.longValue(),(toNumActive.longValue() - numActive)));//今日活跃用户涨跌率

        return analysisSummaryVo;
    }

    /**
     * 查询总的注册用户
     * @return
     */
    public Integer queryCumulativeUsers(){

        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper();
        queryWrapper.select("sum(num_registered) as numRegistered");
        AnalysisByDay analysis = getOne(queryWrapper);
        return analysis.getNumRegistered();
    }

    /**
     * 抽取公共方法
     * @param dateTime  当前时间   用的是hutool  工具类
     * @param number    查询的变量 （要查询多少天之前的（之后的）以正负数来决定    传入当前日期和天数   就会得到加天数（减天数）后的日期）
     * @param column     要查询的字段
     * @return
     */
    public Integer queryCount(DateTime dateTime,Integer number,String column){
        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper();
        //DateUtil.offsetDay(dateTime,number)   用的是hutool  工具类
        queryWrapper.select("sum("+column+") as numRegistered")
                .le("record_date",dateTime.toDateStr())
                .ge("record_date",DateUtil.offsetDay(dateTime,number).toDateStr());

       //获取记录
        AnalysisByDay analysisByDay = getOne(queryWrapper);

        return analysisByDay.getNumRegistered();
    }





    /**
     * 新增、活跃用户、次日留存率
     * @param sd  开始时间
     * @param ed   结束时间
     * @param type  类型  101 新增 102 活跃用户 103 次日留存率
     * @return
     */
    public AnalysisUsersVo getUsers(long sd, long ed, Integer type) {

        //创建返回对象
        AnalysisUsersVo analysisUsersVo = new AnalysisUsersVo();


       // DataPointVo dataPointVo = new DataPointVo();

        //转化时间
        DateTime startDate = new DateTime(sd);  //开始时间
        DateTime endDate = new DateTime(ed);    //结束时间
        //判断类型
        String column = null;
        switch (type) {  //101 新增 102 活跃用户 103 次日留存率
            case 101:
                column = "num_registered";
                break;
            case 102:
                column = "num_active";
                break;
            case 103:
                column = "num_retention1d";
                break;

            default:
                column = "num_registered";
                break;
        }
        //本年的  数量和日期
        List<DataPointVo> dataPointVoList = queryUsers(startDate, endDate, column);
        analysisUsersVo.setThisYear(dataPointVoList);
        //去年的
        List<DataPointVo> dataPointVoList1 = queryUsers(DateUtil.offset(startDate, DateField.YEAR,-1), DateUtil.offset(endDate,DateField.YEAR,-1), column);

        analysisUsersVo.setLastYear(dataPointVoList1);

        return analysisUsersVo;
    }

    /**
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param column    字段类型
     * @return
     */
    public List<DataPointVo> queryUsers(DateTime startDate,DateTime endDate,String column){

        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper();

        queryWrapper.select("record_date as recordDate,"+column+" as numRegistered")
                .ge("record_date",startDate.toDateStr())
                .le("record_date",endDate.toDateStr());
        List<AnalysisByDay> list = list(queryWrapper);
        //返回数据
        List<DataPointVo> dataPointVoList = new ArrayList<>();

        //判断对象是否为空
        if(CollectionUtils.isEmpty(list)){
            return null;
        }

        //不为空就封装数据
        for (AnalysisByDay analysisByDay : list) {
            DataPointVo dataPointVo = new DataPointVo(); //返回对象
            //把日期类型转化为字符串
            dataPointVo.setTitle(DateUtil.formatDate(analysisByDay.getRecordDate()));
            dataPointVo.setAmount(analysisByDay.getNumRegistered().longValue());
            dataPointVoList.add(dataPointVo);
        }
        return dataPointVoList;

    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        DateTime startDate = new DateTime(new Date());

        DateTime offset = DateUtil.offset(startDate, DateField.YEAR, -1);
        System.out.println(offset);

        System.out.println(startDate.toDateStr());

        Date date = new Date();
        System.out.println(date+"====");
        System.out.println(date.toString()+"--------");
    }
}
