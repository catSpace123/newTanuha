package com.tanhua.dubbo.api;

import com.tanhua.domain.db.PageResult;

/**
 * 黑名单列表
 */
public interface BlackListApi {



    /**
     * 黑名单列表分页查询
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult findBlackPage(Integer page, Integer pagesize, Long userId);


    /**
     *
     * @param uid  要删除的黑名单用户id
     * @param userId 根据用户id删除对应的黑名单用户
     */
     void deleteByUserId(Long uid, Long userId) ;


}
