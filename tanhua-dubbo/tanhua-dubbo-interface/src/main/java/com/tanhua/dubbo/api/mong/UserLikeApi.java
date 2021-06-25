package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;

/**
 * 喜欢关注控制层接口
 */
public interface UserLikeApi {
    //b 根据当前用户id 查询 喜欢数量
    long queryLikeCount(Long currentUserID);
    //根据当前用户查询查询粉丝数量
    long queryFansCount(Long currentUserID);

    //查询我的关注
    PageResult queryFollowLike(Integer page, Integer pagesize, Long currentUserId);
    //查询粉丝列表
    PageResult queryFaes(Integer page, Integer pagesize, Long currentUserId);

    //a 现根据粉丝用户id，删除user-like 表的粉丝记录
    void delete(Long currentUserId, Long fansUserId);
}
