package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Video;

/**
 * 小视频服务提供者接口
 */
public interface  SmallVideosApi {

    /**
     * 小视频上传
     * @param video
     */
    public void upload(Video video);



    /**
     * 小视频列表分页查询
     * @param page
     * @param pagesize
     * @return
     */
    PageResult<Video> queryVideosList(Integer page, Integer pagesize);


    /**
     * 视频用户关注（关注通用）
     * @return  friend 要关注的用户id
     */
    void saveUserFocus(Long friend,Long currentUserId);


    /**
     * 视频用户取消关注
     * @return  friendID 要关注的用户id
     */
    void deleteUserFocus(Long friendID, Long currentUserId);
}
