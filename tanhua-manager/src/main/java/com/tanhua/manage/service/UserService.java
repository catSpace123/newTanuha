package com.tanhua.manage.service;


import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.CommentApi;
import com.tanhua.dubbo.api.mong.MovementsPublishApi;
import com.tanhua.manage.vo.MomentManagerVo;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

        @Reference
        private UserInfoApi userInfoApi;

        @Reference
        private MovementsPublishApi movementsPublishApi;

        @Reference
        private CommentApi commentApi;

    /**
     * 用户信息翻页查询
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<UserInfoVo> queryUserInfoList(long page, long pagesize) {

        //创建返回对象
        PageResult<UserInfoVo> pageResult =new PageResult<>();

        List<UserInfoVo> list = new ArrayList<>();

        //调用服务提供者查询用户信息分页
       PageResult<UserInfo> userInfoPageResult = userInfoApi.queryUserInfoList(page, pagesize);

        List<UserInfo> infoList = userInfoPageResult.getItems();
        if(userInfoPageResult == null || CollectionUtils.isEmpty(infoList)){

            return pageResult;
        }

        for (UserInfo userInfo : infoList) {
            UserInfoVo userInfoVo = new UserInfoVo();
            BeanUtils.copyProperties(userInfo, userInfoVo);
            list.add(userInfoVo);
        }
        BeanUtils.copyProperties(userInfoPageResult,pageResult);

        return  pageResult;
    }




    //查询用户基本信息
    public UserInfoVo queryUserInfo(long userId) {

        //创建返回对象
        UserInfoVo userInfoVo = new UserInfoVo();


        //根据用户id查询
        UserInfo userInfo = userInfoApi.findByUserId(userId);

        BeanUtils.copyProperties(userInfo,userInfoVo);

        //年龄数据类型不相同要设置
       userInfoVo.setAge(userInfo.getAge().toString());
        userInfoVo.setState(userInfo.getState().toString());
       userInfoVo.setCreated(userInfo.getCreated().getTime());
        return userInfoVo;
    }


    /**
     * 查询动态分页列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MomentManagerVo> queryMomentList(Integer page,Integer pagesize,long uid) {

        //创建返回对象
        PageResult<MomentManagerVo> pageResult = new PageResult<>();
        List<MomentManagerVo> list = new ArrayList<>();

        //调用方法查询发布表
        PageResult<Publish>  publishPageResult =  movementsPublishApi.findCurrentUserMovements(page,pagesize,uid);
        List<Publish> publishList = publishPageResult.getItems();
        if(publishPageResult == null || CollectionUtils.isEmpty(publishList)){
            return pageResult;
        }

        //遍历集合查询动态的详细信息
        for (Publish publish : publishList) {

            MomentManagerVo momentManagerVo = new MomentManagerVo();
            UserInfo userInfo = userInfoApi.findByUserId(publish.getUserId());

            if(userInfo == null){
                continue;
            }

            //封装数据
            BeanUtils.copyProperties(userInfo,momentManagerVo);
            BeanUtils.copyProperties(publish,momentManagerVo);
            String id = String.valueOf(publish.getId());

            momentManagerVo.setId(id);



            momentManagerVo.setCreateDate(publish.getCreated());
            momentManagerVo.setImageContent(publish.getMedias().toArray(new String[] {}));

            list.add(momentManagerVo);
        }

        BeanUtils.copyProperties(publishPageResult,pageResult);
        pageResult.setItems(list);

        return pageResult;
    }




    /**
     * 查询用户动态详情
     * @param publishId
     * @return
     */
    public MomentManagerVo queryMomentById(String publishId) {
        MomentManagerVo momentManagerVo =new  MomentManagerVo(); //返回对象

        //根据发布动态查询发布表，查询用户发布的单挑动态
        Publish publish = movementsPublishApi.findMovementsById(new ObjectId(publishId));

        if(publish == null){
            return momentManagerVo;
        }


        //在根据发布表用户id查询用户的基本信息
        UserInfo userInfo = userInfoApi.findByUserId(publish.getUserId());


        //拷贝封装对象
        BeanUtils.copyProperties(userInfo,momentManagerVo);

        BeanUtils.copyProperties(publish,momentManagerVo);

        //把不同类型的数据封装处理
        momentManagerVo.setId(publishId);
        momentManagerVo.setUserId(publish.getUserId().intValue());
        momentManagerVo.setImageContent(publish.getMedias().toArray(new String [] {}));

        return momentManagerVo;
    }

    /**
     * 查询评论详情
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    public PageResult<CommentVo> queryCommentById(Integer page, Integer pagesize, String messageID) {
        PageResult<CommentVo> pageResult = new PageResult<>(); //返回对象

        List<CommentVo> list = new ArrayList<>();
        //查询动态的评论列表
        PageResult<Comment> comments = commentApi.findComments(page, pagesize, messageID);
        List<Comment> commentList = comments.getItems();
        if(comments == null || CollectionUtils.isEmpty(commentList)){
            return pageResult;
        }

        //如果不为空就按照评论表的用户id查询评论人的头像和昵称

        for (Comment comment : commentList) {
            CommentVo vo = new CommentVo();  //创建返回对象
            UserInfo userInfo = userInfoApi.findByUserId(comment.getUserId());

            BeanUtils.copyProperties(userInfo,vo);
            BeanUtils.copyProperties(comment,vo);
            vo.setCreateDate(comment.getCreated());
            vo.setId(messageID);
            list.add(vo);
        }


        BeanUtils.copyProperties(comments,pageResult);
        pageResult.setItems(list);
        return pageResult;
    }
}
