package com.tanhua.server.service;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.CommentApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表业务层
 */
@Service
public class CommentService {


    @Reference
    private CommentApi commentApi;


    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 查询评论列表
     * @param page 当前页码
     * @param pagesize 每页记录数
     * @param movementId  动态id
     * @return
     */
    public PageResult<CommentVo> findComments(Integer page, Integer pagesize, String movementId) {
        //当前用户id
        Long currentUserId = UserHolder.getUserId();
        //创建返回对象
        PageResult<CommentVo> pageResult = new PageResult<>();
        List<CommentVo> list = new ArrayList<>();

        //a 根据动态id查询关于这条动态的评论列表  评论类型  评论内容类型
        PageResult<Comment>  commentPageResult = commentApi.findComments(page,pagesize,movementId);

        //如果等于空代表该动态没有评论 返回空对象
        if(commentPageResult == null || CollectionUtils.isEmpty(commentPageResult.getItems())){
            BeanUtils.copyProperties(commentPageResult,pageResult);
            return pageResult;
        }
        List<Comment> comments = commentPageResult.getItems();
        for (Comment comment : comments) {
            CommentVo commentVo = new CommentVo();
            //b 根据评论表的用户id查询评论用户的基本信息
            UserInfo userInfo = userInfoApi.findByUserId(comment.getUserId());
            //拷贝赋值对象
            BeanUtils.copyProperties(userInfo,commentVo);
            BeanUtils.copyProperties(comment,commentVo);

            commentVo.setId(comment.getId().toHexString());   //评论id
            commentVo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
            //从redis取出有值就代表当前用户对这个评论点过赞，
            if(StringUtils.isEmpty(redisTemplate.opsForValue().get("comment_like"+currentUserId+comment.getId()))){
                commentVo.setHasLiked(0);  //是否点赞（1是） （0） 否
            }else{
                commentVo.setHasLiked(1);  //是否点赞（1是） （0） 否
            }
            list.add(commentVo);
        }

        //拷贝赋值对象
        BeanUtils.copyProperties(commentPageResult,pageResult);
        pageResult.setItems(list);

        return pageResult;
    }

    /**
     * 发表评论
     * @param movementId  被评论动态的id
     * @param comment   评论内容
     */
    public void saveComment(String movementId, String comment) {
        //当前用户id
        Long currentUserId = UserHolder.getUserId();
        Comment comment1 = new Comment();
        //调用服务提供者
        comment1.setPublishId(new ObjectId(movementId));  //被评论动态的id
        comment1.setCommentType(2);
        comment1.setPubType(1);
        if(StringUtils.isEmpty(comment)){
            comment1.setContent("吆西大大的好");
        }else{
            comment1.setContent(comment);   //评论内容
        }
        comment1.setUserId(currentUserId);
        commentApi.saveCommentList(comment1);
    }

    /**
     * 评论点赞
     * @param commentId   评论id
     * @return  返回点赞数量
     */
    public long saveCommentLike(String commentId) {

        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));   //评论id
        comment.setUserId(currentUserId);  //当前用户id
        comment.setCommentType(1);    //评论的点赞
        comment.setPubType(3);      //对评论操作

      long count = commentApi.saveCommentLike(comment);
        //存入redis
        redisTemplate.opsForValue().set("comment_like"+currentUserId+commentId,currentUserId.toString());

        return count;
    }



    /**
     * 取消评论点赞
     * @param commentId   评论id
     * @return  返回点赞数量
     */
    public long onCommentLike(String commentId) {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));   //评论id
        comment.setUserId(currentUserId);  //当前用户id
        comment.setCommentType(1);    //评论的点赞
        comment.setPubType(3);       //对评论操作

        long count = commentApi.onCommentLike(comment);
        //删除redis
        redisTemplate.delete("comment_like"+currentUserId+commentId);

        return count;
    }
}
