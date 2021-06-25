package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.MovementsPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论控制层
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;


    /**
     * 查询评论列表
     * @param page 当前页码
     * @param pagesize 每页记录数
     * @param movementId  动态id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pagesize,
                                       @RequestParam String movementId){

        PageResult<CommentVo> pageResult = commentService.findComments(page,pagesize,movementId);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 发表评论
     * @param param
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity saveComment(@RequestBody Map<String,String> param){
        String movementId = param.get("movementId");  //动态编号
        String comment = param.get("comment");         //评论内容

        //调用自己的业务
        commentService.saveComment(movementId,comment);
        return ResponseEntity.ok(null);
    }


    /**
     * 评论点赞
     * @param commentId   评论id
     * @return  返回点赞数量
     */
    @RequestMapping(value = "/{id}/like",method = RequestMethod.GET)
    public ResponseEntity saveCommentLike(@PathVariable("id") String commentId){

        long count = commentService.saveCommentLike(commentId);
        return ResponseEntity.ok(count);
    }



    /**
     * 取消评论点赞
     * @param commentId   评论id
     * @return  返回点赞数量
     */
    @RequestMapping(value = "/{id}/dislike",method = RequestMethod.GET)
    public ResponseEntity onCommentLike(@PathVariable("id") String commentId){

        long count = commentService.onCommentLike(commentId);
        return ResponseEntity.ok(count);
    }
}
