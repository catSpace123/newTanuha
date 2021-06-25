package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Friend;

/**
 * 查询好友列表接口
 */
public interface FriendApi {

    /**
     * 查询好友列表
     * @param page
     * @param pagesize
     * @param keyword
     * @param currentUserID
     * @return
     */
    PageResult<Friend> queryContacts(Integer page, Integer pagesize, String keyword, Long currentUserID);

    //根据当前用户id查询关注数量
    long queryFollowCount(Long currentUserID);


    //b 向tanhua-user保存俩条记录
    void saveFansUser(Long currentUserId, Long fansUserId);

}
