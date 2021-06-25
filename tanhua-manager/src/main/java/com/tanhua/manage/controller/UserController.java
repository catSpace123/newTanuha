package com.tanhua.manage.controller;




import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.manage.service.UserService;
import com.tanhua.manage.vo.MomentManagerVo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户信息翻页查询
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/users",method = RequestMethod.GET)
    public ResponseEntity queryUserInfoList(@RequestParam(defaultValue = "1") long page,@RequestParam(defaultValue = "10") long pagesize){

     PageResult<UserInfoVo> pageResult = userService.queryUserInfoList(page,pagesize);

     return ResponseEntity.ok(pageResult);
    }

    //查询用户基本信息
    @RequestMapping(value = "/users/{userID}",method = RequestMethod.GET)
    public ResponseEntity queryUserInfo(@PathVariable("userID") long userId){
        UserInfoVo  userInfoVo = userService.queryUserInfo(userId);

        return ResponseEntity.ok(userInfoVo);
    }


    /**
     * 查询动态分页列表
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/messages",method = RequestMethod.GET)
    public ResponseEntity queryMomentList(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize,@RequestParam long uid){

            PageResult<MomentManagerVo> pageResult =  userService.queryMomentList(page,pagesize,uid);

            return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询用户动态详情
     * @param publishId
     * @return
     */
    @RequestMapping(value = "/messages/{id}",method = RequestMethod.GET)
    public ResponseEntity queryMomentById(@PathVariable("id") String publishId){

        MomentManagerVo momentManagerVo = userService.queryMomentById(publishId);

        return ResponseEntity.ok(momentManagerVo);
    }


    /**
     * 查询评论详情
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    @RequestMapping(value = "/messages/comments",method = RequestMethod.GET)
    public ResponseEntity queryCommentById(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize,@RequestParam String messageID){


     PageResult<CommentVo> commentVoPageResult = userService.queryCommentById(page,pagesize,messageID);

     return ResponseEntity.ok(commentVoPageResult);
    }

}
