package com.tanhua.dubbo.api;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;

/**
 * 用户信息保存接口
 */
public interface UserInfoApi {
    /**
     * 保存用户信息
     * @param userInfo
     */
    void loginReginfoAdd(UserInfo userInfo);

    /**
     * 更新用户头像保存头像信息
     * @param userInfo
     */
    void updateAvatar(UserInfo userInfo);


    /**
     * 根据用户id查询用户
     * @param currentId
     * @return
     */
    UserInfo findByUserId(Long currentId);


    /**
     * 用户信息翻页查询
     * @param page
     * @param pagesize
     * @return
     */
    PageResult<UserInfo> queryUserInfoList(long page, long pagesize);



}
