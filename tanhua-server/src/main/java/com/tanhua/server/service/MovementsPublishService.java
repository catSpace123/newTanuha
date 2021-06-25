package com.tanhua.server.service;

import com.google.errorprone.annotations.Var;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.db.Visitor;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.VisitorVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.CommentApi;
import com.tanhua.dubbo.api.mong.MovementsPublishApi;
import com.tanhua.dubbo.api.mong.TodayBestApi;
import com.tanhua.dubbo.api.mong.VisitorsApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 圈子 （动态发布查询  ，业务层）
 */
@Service
public class MovementsPublishService {

    @Autowired
    private OssTemplate ossTemplate;

    @Reference
    private MovementsPublishApi movementsPublishApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private CommentApi commentApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Reference
    private VisitorsApi visitorsApi;
    @Reference
    private TodayBestApi todayBestApi;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发布动态，（圈子）
     * @param imageContent   发布的图片
     * @param publishVo  其他信息实体对象
     * @return
     */
    public void saveMovementsPublish(MultipartFile[] imageContent, PublishVo publishVo) {
        try {
            //获取当前用户id
            Long currentUserId = UserHolder.getUserId();
            // a 判断当前用户是否上传了图片
            if(imageContent != null && imageContent.length>0){
                //用来装上传后的图片名称
                List<String> medias = new ArrayList<>();
                //不为空表示用户上传了图片
                for (MultipartFile multipartFile : imageContent) {
                    //b 调用阿里云组件 将图片上传到oss中
                    //获取到文件的名字
                    String filename = multipartFile.getOriginalFilename();
                    String imgName = ossTemplate.upload(filename, multipartFile.getInputStream());
                    medias.add(imgName);
                }
                //处理完后将数据分装，传给服务提供者
                publishVo.setUserId(currentUserId);
                publishVo.setMedias(medias);


                //调用服务提供者
                String publishId = movementsPublishApi.saveMovementsPublish(publishVo);

                //保存到发布表之后拿到发布id  写消息到mq  用来在后台异步审核
                rocketMQTemplate.convertAndSend("tanhua_publishId",publishId);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询好友动态
     * @param page  当前页码
     * @param pagesize  每页显示条数
     * @return
     */
    public PageResult<MomentVo> findFriendMovements(Integer page, Integer pagesize) {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //a 根据当前用户id查询去自己的时间线表查询好友发布的动态id 然后再去查询发布表的发布动态信息


        //定义用来返回的对象
        PageResult<MomentVo> pageResult = new PageResult<>();
        //用来封装的list集合
        List<MomentVo> list = new ArrayList<>();

        PageResult<Publish> publishPageResult =  movementsPublishApi.findFriendMovements(page,pagesize,currentUserId);

        if(publishPageResult == null || CollectionUtils.isEmpty(publishPageResult.getItems())){
            return null;
        }
        //调用方法查询userinfo表查询用户的基本信息。
        //不为空就遍历
        for (Publish publish : publishPageResult.getItems()) {
            if(publish != null){
                MomentVo momentVo =  new MomentVo();
                UserInfo userInfo = userInfoApi.findByUserId(publish.getUserId());

                //调用方法
                findPublishMomentUserInfo(list, publish, momentVo, userInfo);
            }/*else{
                    System.out.println(publish);
                }*/
        }

        //封装数据
        BeanUtils.copyProperties(publishPageResult,pageResult);
        pageResult.setItems(list);
        return pageResult;
    }


    /**
     * 查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MomentVo> findRecommendMovements(Integer page, Integer pagesize) {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //创建返回的page对象
        PageResult<MomentVo> pageResult = new PageResult<>();
        List<MomentVo> list = new ArrayList<>();

        //根据当前用户id查询推荐的动态
        PageResult<Publish> publishPageResult =  movementsPublishApi.findRecommendMovenents(page,pagesize,currentUserId);

        if(publishPageResult == null || CollectionUtils.isEmpty(publishPageResult.getItems())){
            return null;
        }

        //调用方法用来根据发布的动态用户id查询用户的基本信息
        //调用方法查询userinfo表查询用户的基本信息。
        //不为空就遍历
        for (Publish publish : publishPageResult.getItems()) {
            if(publish != null){
                MomentVo momentVo =  new MomentVo();
                UserInfo userInfo = userInfoApi.findByUserId(publish.getUserId());
                //调用方法 用来查询用户信息表
                findPublishMomentUserInfo(list, publish, momentVo, userInfo);

            }/*else{
                    System.out.println(publish);
                }*/
        }
        //封装数据
        BeanUtils.copyProperties(publishPageResult,pageResult);

        pageResult.setItems(list);
        return pageResult;
    }


    /**
     * 查询当前用户发布的动态
     * @param page
     * @param pagesize
     * @param userId  当前用户的id
     * @return
     */
    public PageResult<MomentVo> findCurrentUserMovements(Integer page, Integer pagesize, Long userId) {
        //创建返回的对象
        PageResult<MomentVo> pageResult = new PageResult<>();
        List<MomentVo> list = new ArrayList<>();
        //调用服务提供者查询用户的动态
       PageResult<Publish> publishPageResult = movementsPublishApi.findCurrentUserMovements(page,pagesize,userId);

       if(publishPageResult == null || CollectionUtils.isEmpty(publishPageResult.getItems())){
           return null;
       }
       //根据当前的用户id查询基本信息
        UserInfo userInfo = userInfoApi.findByUserId(userId);

        List<Publish> publishes = publishPageResult.getItems();
        if(publishes != null){
            for (Publish publish : publishes) {
                MomentVo momentVo = new MomentVo();
                //调用方法封装数据
                findPublishMomentUserInfo(list,publish,momentVo,userInfo);
            }
        }

        BeanUtils.copyProperties(publishPageResult,pageResult);
        pageResult.setItems(list);
        return pageResult;
    }




    /**
     * 查询userinfo表，，根据查询出来的发布动态的用户id，去sql查询用户基本信息
     * @param list   查询出来分装的数据
     * @param publish  发布的动态信息
     * @param momentVo  要返回的数据对象
     * @param userInfo  用户的基本信息
     */
    private void findPublishMomentUserInfo(List<MomentVo> list, Publish publish, MomentVo momentVo, UserInfo userInfo) {
        Long currentUserId = UserHolder.getUserId();

        if(userInfo != null || !StringUtils.isEmpty(userInfo.getTags())){
            BeanUtils.copyProperties(userInfo,momentVo);

            momentVo.setUserId(userInfo.getId());

            momentVo.setTags(userInfo.getTags().split(","));
        }
        BeanUtils.copyProperties(publish,momentVo);
        momentVo.setId(publish.getId().toHexString());
        //把集合转化为数组
        momentVo.setImageContent(publish.getMedias().toArray(new String [] {}));
        momentVo.setDistance("1米");
        //调用工具类对毫秒值日期处理
        String createDate = RelativeDateFormat.format(new Date(publish.getCreated()));
        momentVo.setCreateDate(createDate);

        //判断从redis中取出喜欢数据看是否是当前用户点过赞的，点过的返回1 没点返回0
        if( StringUtils.isEmpty(redisTemplate.opsForValue().get("like_count"+currentUserId+publish.getId()))){
            momentVo.setHasLiked(0);
        }else{
            momentVo.setHasLiked(1);
        }

        //判断从redis中取出喜欢数据看是否是当前用户喜欢的，喜欢的返回1 不喜欢返回0
        if(StringUtils.isEmpty(redisTemplate.opsForValue().get("love_count_"+currentUserId+publish.getId()))){
            momentVo.setHasLoved(0);
        }else{
            momentVo.setHasLoved(1);
        }

        list.add(momentVo);
    }


    /**
     * 动态点赞
     * @param publishId  动态id
     * @return 返回点赞数量
     */

    public long saveFabulous(String publishId) {
        //获取当前用户id
        Long currentId = UserHolder.getUserId();
        //封装要添加的数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);      //（1）是喜欢
        comment.setPubType(1);          //（1）对动态的操作
        comment.setUserId(currentId);
        long count =  commentApi.saveFabulous(comment);
        //向redis存入点赞成功后的记录，那边查询动态的时候就能判断当前用户是否对这个动态点赞
        redisTemplate.opsForValue().set("like_count"+currentId+publishId,currentId.toString());
        return count;
    }




    /**
     * 取消点赞
     * @param publishId  动态id
     * @return  返回点赞数量
     */
    public long deleteFabulous(String publishId) {
        //获取当前用户id
        Long currentId = UserHolder.getUserId();
        //封装要添加的数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));
        comment.setCommentType(1);      //（1）是喜欢
        comment.setPubType(1);          //（1）对动态的操作
        comment.setUserId(currentId);
        long count =  commentApi.deleteFabulous(comment);

        //删除redis中的对该动态的记录
        redisTemplate.delete("like_count"+currentId+publishId);

        return count;
    }

    /**
     * 动态喜欢
     * @param publishId 动态id
     * @return 返回动态数量
     */
    public long saveLove(String publishId) {
        Long currentUserId = UserHolder.getUserId();
        //封装动态喜欢的数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));  //动态id
        comment.setCommentType(3);  //评论类型 （3）是喜欢
        comment.setPubType(1);  //评论内容类型（1） 对动态的操作
        comment.setUserId(currentUserId);  //当前用户的id
       long count =commentApi.saveLove(comment);

       //向redis存入喜欢后的值，
        redisTemplate.opsForValue().set("love_count_"+currentUserId+publishId,currentUserId.toString());
        return count;
    }


    /**
     * 取消喜欢动态
     * @param publishId 动态id
     * @return 返回动态数量
     */
    public long unLove(String publishId) {
        Long currentUserId = UserHolder.getUserId();
        //封装用来条件删除动态喜欢的数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(publishId));  //动态id
        comment.setCommentType(3);  //评论类型 （3）是喜欢
        comment.setPubType(1);  //评论内容类型（1） 对动态的操作
        comment.setUserId(currentUserId);  //当前用户的id
        long count =commentApi.deleteLove(comment);


        //删除redis的喜欢记录
        redisTemplate.delete("love_count_"+currentUserId+publishId);
        return count;
    }



    /**
     * 查询单条动态
     * @param publishId  动态id
     * @return  返回动态信息以及用户信息
     */
    public MomentVo findMovementsById(String publishId) {
        //当前用户id
        Long currentUserId = UserHolder.getUserId();
        //用来返回的对象
        MomentVo momentVo = new MomentVo();
        ObjectId objectId = new ObjectId(publishId);
        //a 调用服务提供者查询单挑动态
        Publish publish = movementsPublishApi.findMovementsById(objectId);
        if(publish != null) {
            //b 根据发布表中userid查询用户信息表
            Long pubUserId = publish.getUserId();

            UserInfo userInfo = userInfoApi.findByUserId(pubUserId);
            BeanUtils.copyProperties(publish, momentVo);
            BeanUtils.copyProperties(userInfo, momentVo);

            momentVo.setId(publishId);
            momentVo.setTags(userInfo.getTags().split(","));
            //把集合类型转化为string数组
            momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            momentVo.setDistance("1米");
            //调用工具类处理日期
            String createDate = RelativeDateFormat.format(new Date(publish.getCreated()));
            momentVo.setCreateDate(createDate);

            //判断从redis中取出喜欢数据看是否是当前用户点过赞的，点过的返回1 没点返回0
            if (StringUtils.isEmpty(redisTemplate.opsForValue().get("like_count" + currentUserId + publish.getId()))) {
                momentVo.setHasLiked(0);
            } else {
                momentVo.setHasLiked(1);
            }

            //判断从redis中取出喜欢数据看是否是当前用户喜欢的，喜欢的返回1 不喜欢返回0
            if (StringUtils.isEmpty(redisTemplate.opsForValue().get("love_count_" + currentUserId + publish.getId()))) {
                momentVo.setHasLoved(0);
            } else {
                momentVo.setHasLoved(1);
            }
        }
        return momentVo;
    }


    /**
     * 查询访客
     * @return
     */
    public List<VisitorVo> findVisitors() {
        //创建返回对象
        List<VisitorVo> list = new ArrayList<>();
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //到redis获取用户的上一次登录时间
        String loginTime = redisTemplate.opsForValue().get("Visitors_" + currentUserId);
        List<Visitor> visitorList = null;
        if(StringUtils.isEmpty(loginTime)){
            //a 如果时间为空就根据当前用户id查询访客表的前五条记录，
             visitorList = visitorsApi.findVisitors(currentUserId);
        }else{
            //b如果不为空就根据当前用户上一次的登录时间跟id查询这段时间内的访客记录
            visitorList =  visitorsApi.findVisitors(currentUserId,loginTime);
        }
        //说明当前用户没有访客记录
        if(CollectionUtils.isEmpty(visitorList)){
            return null;
        }

        for (Visitor visitor : visitorList) {

            VisitorVo visitorVo = new VisitorVo();
            //c 根据查到的用户id在查询查询commend_user 查询缘分值
            Integer score = 66;
            RecommendUser recommendUser =  todayBestApi.queryPersonalInfo(currentUserId,visitor.getVisitorUserId());
            if(recommendUser != null){
                score =  recommendUser.getScore().intValue();
            }

            //d 根据访客id查询访客基本信息
            UserInfo userInfo = userInfoApi.findByUserId(visitor.getVisitorUserId());

            BeanUtils.copyProperties(userInfo,visitorVo);
            visitorVo.setTags(userInfo.getTags().split(","));
            visitorVo.setFateValue(score);
            list.add(visitorVo);
        }
        //存入redis
        redisTemplate.opsForValue().set("Visitors_" + currentUserId,System.currentTimeMillis()+"");

        return list;
    }
}
