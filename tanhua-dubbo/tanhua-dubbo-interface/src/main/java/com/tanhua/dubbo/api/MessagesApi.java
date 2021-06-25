package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Announcement;

import com.tanhua.domain.db.PageResult;

/**
 * 消息服务提供者接口
 */
public interface MessagesApi {


    /**
     * 分页查询公告
     * @param page
     * @param pagesize
     * @return
     */
    PageResult<Announcement> findAnnouncements(Integer page, Integer pagesize);
}
