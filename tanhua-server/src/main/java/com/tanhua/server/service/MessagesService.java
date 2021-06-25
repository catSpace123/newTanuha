package com.tanhua.server.service;

import com.tanhua.commons.exception.TanhuaException;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.AnnouncementsVo;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.dubbo.api.MessagesApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.CommentApi;
import com.tanhua.dubbo.api.mong.FriendApi;
import com.tanhua.dubbo.api.mong.SmallVideosApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessagesService {

    @Reference
    private MessagesApi messagesApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private SmallVideosApi smallVideosApi;

    @Reference
    private FriendApi friendApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private CommentApi commentApi;
    /**
     * 分页查询公告
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<AnnouncementsVo> findAnnouncements(Integer page, Integer pagesize) {
        //创建返回的对象
        PageResult<AnnouncementsVo> voPageResult = new PageResult<>();
        List<AnnouncementsVo> list = new ArrayList<>();
        //根据分页条件查公告
        PageResult<Announcement> pageResult =  messagesApi.findAnnouncements(page,pagesize);
        if(pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())){
            throw  new TanhuaException(ErrorResult.error());
        }

        List<Announcement> items = pageResult.getItems();
        //遍历数据
        for (Announcement announcements : items) {
            AnnouncementsVo announcementsVo = new AnnouncementsVo();
            //封装数据
            BeanUtils.copyProperties(announcements,announcementsVo);
            announcementsVo.setId(announcements.getId().toString());
            //调用工具类把日期类型转化为字符串
            String createDate = RelativeDateFormat.format(announcements.getCreated());
            announcementsVo.setCreateDate(createDate);
            list.add(announcementsVo);
        }
        BeanUtils.copyProperties(pageResult,voPageResult);
        voPageResult.setItems(list);
        return voPageResult;
    }

    /**
     * 添加好友关系
     * @param friendUserId  好友id
     */
    public void contacts(Long friendUserId) {
        //当前好友id
        Long currentUserID = UserHolder.getUserId();


        //调用服务提供者添加好像关系
        smallVideosApi.saveUserFocus(friendUserId,currentUserID);

        //调用环信保存好友关系
        huanXinTemplate.makeFriends(currentUserID,friendUserId);
    }

    /**
     * 查询联系人列表
     * @param page
     * @param pagesize
     * @param keyword  查询条件
     * @return
     */
    public PageResult<ContactVo> queryContacts(Integer page, Integer pagesize, String keyword) {
        //创建返回对象
        PageResult<ContactVo> pageResult = new PageResult<>();
        List<ContactVo> list = new ArrayList<>();

        //获取当前用户
        Long currentUserID = UserHolder.getUserId();

        //根据当前用户查friend 表
        PageResult<Friend> friendPageResult =  friendApi.queryContacts(page,pagesize,keyword,currentUserID);
        //如果为空表示没用好友，返回空
        if(friendPageResult == null || CollectionUtils.isEmpty(friendPageResult.getItems())){

            return pageResult;
        }

        for (Friend friend : friendPageResult.getItems()) {
            //查询userinfo 封装数据
            ContactVo contactVo = new ContactVo();
            UserInfo userInfo = userInfoApi.findByUserId(friend.getFriendId());
            BeanUtils.copyProperties(userInfo,contactVo);
            long b = 1L;
            contactVo.setId(b);  //用户编号，自定义
            contactVo.setUserId(userInfo.getId().toString());
            list.add(contactVo);
        }
        BeanUtils.copyProperties(friendPageResult,pageResult);
        pageResult.setItems(list);

        return pageResult;
    }


    /**
     * 分页查询点赞列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MessageVo> querylikes(Integer page, Integer pagesize) {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();

        //  根据当前用户id和点赞类型查询 查询评论表
        Comment comment1 = new Comment();
        comment1.setCommentUserId(currentUserId);  //当前用户id
        comment1.setCommentType(1);  //对点赞查询

        PageResult<MessageVo> pageResult = queryLikeOrLove(page, pagesize, comment1);

        return pageResult;
    }

    /**
     * 查询喜欢列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MessageVo> queryloves(Integer page, Integer pagesize) {

        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();

        //  根据当前用户id和点赞类型查询 查询评论表
        Comment comment1 = new Comment();
        comment1.setCommentUserId(currentUserId);  //当前用户id
        comment1.setCommentType(3);  //对喜欢查询

        PageResult<MessageVo> pageResult = queryLikeOrLove(page, pagesize, comment1);

        return pageResult;
    }

    /**
     * 提取查询喜欢和点赞的的方法
     * @param page
     * @param pagesize
     * @param comment1
     * @return
     */
    public PageResult<MessageVo> queryLikeOrLove(Integer page, Integer pagesize,Comment comment1){
        //创建返回对象
        PageResult<MessageVo> pageResult = new PageResult<>();
        List<MessageVo> list = new ArrayList<>();


        PageResult<Comment> commentPageResult = commentApi.findLikeOrLove(page,pagesize,comment1);

        if(commentPageResult ==null || CollectionUtils.isEmpty(commentPageResult.getItems())){
            return pageResult;
        }

        List<Comment> comments = commentPageResult.getItems();

        for (Comment comment : comments) {
            MessageVo messageVo = new MessageVo();
            UserInfo userInfo = userInfoApi.findByUserId(comment.getUserId());

            BeanUtils.copyProperties(userInfo,messageVo);
            long b = 1L;
            messageVo.setId(String.valueOf(b));
            messageVo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd HH:mm"));
            list.add(messageVo);
            b++;
        }

        BeanUtils.copyProperties(commentPageResult,pageResult);
        pageResult.setItems(list);
        return pageResult;
    }

    /**
     * 评论列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MessageVo> querycomments(Integer page, Integer pagesize) {

        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();

        //  根据当前用户id和点赞类型查询 查询评论表
        Comment comment1 = new Comment();
        comment1.setCommentUserId(currentUserId);  //当前用户id
        comment1.setCommentType(2);  //对评论查询

        PageResult<MessageVo> pageResult = queryLikeOrLove(page, pagesize, comment1);

        return pageResult;
    }
}
