package com.tanhua.dubbo.api.mong;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Comment;

/**
 * 评论服务提供者接口
 */
public interface CommentApi {

    /**
     * 动态点赞
     * @param comment
     * @return  返回点赞记录数
     */
     long saveFabulous(Comment comment);
    /**
     * 删除
     * @param comment
     * @return  返回点赞记录数
     */
     long deleteFabulous(Comment comment) ;

    /**
     * 动态喜欢
     * @param comment
     * @return 返回动态数量
     */
    long saveLove(Comment comment);

    /**
     * 取消动态喜欢
     * @param comment
     * @return 返回动态数量
     */
    long deleteLove(Comment comment);


    /**
     * 查询评论列表
     * @param page 当前页码
     * @param pagesize 每页记录数
     * @param movementId  动态id
     * @return
     */
    PageResult<Comment> findComments(Integer page, Integer pagesize, String movementId);

    /**
     * 发表评论
     * @param comment1
     */
    void saveCommentList(Comment comment1);



    /**
     * 评论点赞
     * @param
     * @return  返回点赞数量
     */
    long saveCommentLike(Comment comment);



    /**
     * 取消评论点赞
     * @param
     * @return  返回点赞数量
     */
    long onCommentLike(Comment comment);


    /**
     * 分页查询点赞列表
     * @param page
     * @param pagesize
     * @return
     */
    PageResult<Comment> findLikeOrLove(Integer page, Integer pagesize, Comment comment1);
}
