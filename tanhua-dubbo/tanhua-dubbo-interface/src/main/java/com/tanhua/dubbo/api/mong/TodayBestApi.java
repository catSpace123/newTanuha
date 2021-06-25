package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.RecommendUserQuery;
import com.tanhua.domain.vo.UserLocationVo;

import java.util.List;

/**
 * 今日佳人服务提供者接口
 */
public interface TodayBestApi {
    //根据当前用户id查询佳人用户信息
     RecommendUser findTodayBestAndUserId(Long currentUserId);

    /**
     * 根据条件查询推荐用户列表（首页推荐）
     * @param recommendUserQuery  查询条件
     * @param currentUserId  //当前用户id
     * @return
     */
    PageResult<RecommendUser> findTodayRecommendByUser(RecommendUserQuery recommendUserQuery, Long currentUserId);


    /**
     * 查询推荐动态的个人信息
     * @param recommendId    推荐用户id
     * @return
     */
    RecommendUser queryPersonalInfo(Long recommendId, Long currentUserId);

    /**
     * 搜附近
     * @param distance  距离
     * @return
     */
    List<UserLocationVo> search(long distance,long currentUserId);


    /**
     * 喜欢好友
     * @param currentUserID  当前用户id
     * @param loveId  要喜欢的用户id
     */
    void saveLove(Long currentUserID, long loveId);
}
