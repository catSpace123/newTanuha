package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.Visitor;

import java.util.List;

/**
 * 访客控制层接口
 */
public interface VisitorsApi {

    //a 如果时间为空就根据当前用户id查询访客表的前五条记录，
    public List<Visitor> findVisitors(Long currentUserId) ;
    //b如果不为空就根据当前用户上一次的登录时间跟id查询这段时间内的访客记录
    public List<Visitor> findVisitors(Long currentUserId,String loginTime) ;


    //测试数据保存方法
    public  void save (Visitor visitor);

    //查询谁看过我
    PageResult findVisitorsList(Integer page, Integer pagesize, Long currentUserId);
}
