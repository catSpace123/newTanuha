package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PublishVo;
import org.bson.types.ObjectId;

/**
 * 圈子（动态） 服务提供者接口
 */
public interface MovementsPublishApi {

    /**
     * 发布动态，（圈子）
     * @param publishVo  发布信息实体对象
     * @return
     */
    String saveMovementsPublish(PublishVo publishVo);


    /**
     * 查询好友动态
     * @param page  当前页码
     * @param pagesize  每页显示条数
     * @return
     */
    PageResult<Publish> findFriendMovements(Integer page, Integer pagesize, Long currentUserId);

    /**
     * 查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    PageResult<Publish> findRecommendMovenents(Integer page, Integer pagesize, Long currentUserId);



    /**
     * 查询当前用户发布的动态
     * @param page
     * @param pagesize
     * @param userId  当前用户的id
     * @return
     */
    PageResult<Publish> findCurrentUserMovements(Integer page, Integer pagesize, Long userId);


    /**
     * 查询单条动态
     * @param objectId  动态id
     * @return  返回动态信息以及用户信息
     */
    Publish findMovementsById(ObjectId objectId);


    /**
     * 修改动态的状态
     * @param publishID
     * @param state
     */
    void updateState(String publishID, Integer state);
}
