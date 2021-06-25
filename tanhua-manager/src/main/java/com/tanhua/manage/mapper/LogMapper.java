package com.tanhua.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.manage.domain.Log;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 日志接口
 */
public interface LogMapper extends BaseMapper<Log> {


        //查询新注册用户数  和登录用户数
    @Select("SELECT count(DISTINCT user_id) FROM `tb_log`  where type = #{type} and log_time = #{logTime}")
    Integer queryCount(@Param("type") String type,@Param("logTime") String logTime);

    //活跃用户数
    @Select("SELECT count(DISTINCT user_id) FROM `tb_log`  where log_time = #{logTime}")
    Integer numActive(@Param("logTime") String logTime);

    //查询次日留存用户数
    @Select("SELECT count(DISTINCT user_id) FROM `tb_log`  where log_time = #{logTime}\n" +
            " and user_id in (SELECT user_id FROM `tb_log`  where type = #{type}  and log_time = #{yesterday})")
    Integer numRetention1d(@Param("type") String type,@Param("logTime") String logTime,@Param("yesterday") String yesterday);
}
