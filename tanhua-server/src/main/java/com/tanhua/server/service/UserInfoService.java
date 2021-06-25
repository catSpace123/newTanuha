package com.tanhua.server.service;

import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.db.Visitor;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.vo.CountsVo;
import com.tanhua.domain.vo.FriendVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mong.*;
import com.tanhua.server.utils.GetAgeUtil;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserInfoService {

   /* @Autowired
    private UserService userService;*/

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private FriendApi friendApi;

    @Reference
    private UserLikeApi userLikeApi;

    @Reference
    private VisitorsApi visitorsApi;

    @Reference
    private TodayBestApi todayBestApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    /**
     * 条件查询用户信息
     * @param userID
     * @param huanxinID
     * @return
     */
    public UserInfoVo findUserInFo(Long userID, Long huanxinID) {
        //调用service里面的方法取出redis的token判断用户是否登录过期
        User user = UserHolder.getUser();

        //定义一个全局id
        Long currentId ;

        //判断前端是否传入了参数  如果传入的任意一个不为空就用传入的参数作为id去查询用户  如果都为空就用token里面的id
        if(!StringUtils.isEmpty(userID)){
               currentId = userID;
        }else if(!StringUtils.isEmpty(huanxinID)){
            currentId = huanxinID;
        }else{
            currentId = user.getId();
        }
        //创建这个对象用于给前端返回数据
        UserInfoVo userInfoVo = new UserInfoVo();

        //调用服务提供者方法查询数据库
        UserInfo userInfo = userInfoApi.findByUserId(currentId);
        //把查询出来的对象拷贝到返回的对象中去
        BeanUtils.copyProperties(userInfo,userInfoVo);
            //因为前端要的是年龄是String 所以要不int转成string
        if(userInfo.getAge() != null){
            userInfoVo.setAge(String.valueOf(userInfo.getAge()));
        }
        return userInfoVo;
    }

    /**
     * 跟新用户信息
     * @param userInfoVo
     * @param //token
     */
    public void updateUser(UserInfoVo userInfoVo) {
        //判断用户是否登录过期 当返回user表示用户还为失效
        User user = UserHolder.getUser();

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);
        //通过生日计算年龄
       if(!StringUtils.isEmpty(userInfoVo.getBirthday())){
           userInfo.setAge(GetAgeUtil.getAge(userInfoVo.getBirthday()));
       }
       //更新用户
        userInfoApi.updateAvatar(userInfo);
    }



    /**
     * 查询喜欢，互相喜欢，粉丝的数量
     */
    public CountsVo queryCounts() {

        //创建返回对象
        CountsVo countsVo = new CountsVo();
        //获取当前用户id
        Long currentUserID = UserHolder.getUserId();

        //a 根据当前用户id查询关注数量
        long FollowCount =   friendApi.queryFollowCount(currentUserID);

        //b 根据当前用户id 查询 喜欢数量
        long  LikeCount =  userLikeApi.queryLikeCount(currentUserID);

        //根据当前用户查询查询粉丝数量
        long  fansCount =  userLikeApi.queryFansCount(currentUserID);


        countsVo.setEachLoveCount(FollowCount);  //互相喜欢
        countsVo.setLoveCount(LikeCount);        //我的喜欢
        countsVo.setFanCount(fansCount);         //粉丝数量
        return countsVo;
    }

    /**
     * 互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     * 1 互相关注
     * 2 我关注
     * 3 粉丝
     * 4 谁看过我
     */
    public PageResult<FriendVo> QueryFriendsList(Integer type, Integer page, Integer pagesize,String nickname) {
        //创建返回对象
        PageResult<FriendVo> voPageResult = new PageResult<>();
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        PageResult PageResult = null;
        switch (type){
            case 1:   //互相关注  nickname  没有用到
                PageResult = friendApi.queryContacts(page,pagesize,nickname,currentUserId);
                break;
            case 2:  //查询我的关注
                PageResult = userLikeApi.queryFollowLike(page,pagesize,currentUserId);
                break;
            case 3:  //查询粉丝列表
                PageResult = userLikeApi.queryFaes(page,pagesize,currentUserId);
                break;
            case 4:   //查询谁看过我
                PageResult = visitorsApi.findVisitorsList(page,pagesize,currentUserId);
                break;
            default:
                break;
        }
            //如果为空就返回
        if(PageResult == null || CollectionUtils.isEmpty(PageResult.getItems())){
            return null;
        }
        //返回的集合
        List<FriendVo> list = new ArrayList<>();
        //判断当前的对象类型
        for (Object resultItem : PageResult.getItems()) {
            FriendVo friendVo= null;
            if(resultItem instanceof Friend){
                //强转            互相关注
              Friend friend = (Friend) resultItem;
              //调用方法查询用户信息，以及缘分值
                friendVo = queryList(currentUserId, friend.getFriendId());
            }else if(resultItem instanceof UserLike){
                UserLike userLike = (UserLike) resultItem;
                if(type == 2){
                    //调用方法查询用户信息，以及缘分值          查询我的关注
                    friendVo = queryList(currentUserId, userLike.getLikeUserId());
                }else{                      //查询粉丝列表
                    friendVo = queryList(currentUserId, userLike.getUserId());
                }
            }else if(resultItem instanceof Visitor){
                Visitor visitor = (Visitor)resultItem;
                //调用方法查询用户信息，以及缘分值      //查询谁看过我
                 friendVo = queryList(currentUserId, visitor.getVisitorUserId());
            }
            list.add(friendVo);
        }
        BeanUtils.copyProperties(PageResult,voPageResult);
        voPageResult.setItems(list);
        return voPageResult;
    }

    /**
     * 用来查询分装  互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     * @param currentUserId
     * @param friendId
     * @return
     */
    public FriendVo queryList(long currentUserId,long friendId){
             FriendVo friendVo = new FriendVo();
             //根据查询到的好友id查询user info表
            UserInfo userInfo = userInfoApi.findByUserId(friendId);
            Integer score = 66;
            //在查询recommend  缘分值
             RecommendUser recommendUser = todayBestApi.queryPersonalInfo(friendId, currentUserId);
            if(recommendUser != null ){
                score=recommendUser.getScore().intValue();
            }
            BeanUtils.copyProperties(userInfo,friendVo);
            friendVo.setMatchRate(score);
            return friendVo;

    }


    /**
     * 粉丝喜欢   粉丝列表的喜欢按钮
     * @param fansUserId  要关注的用户id
     * @return
     */
    public void saveFansUser(Long fansUserId) {
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //a 先根据粉丝用户id，删除user-like 表的粉丝记录
        userLikeApi.delete(currentUserId,fansUserId);
        //b 向tanhua-user保存俩条记录

        friendApi.saveFansUser(currentUserId,fansUserId);

        //调用环信添加好友关系
        huanXinTemplate.makeFriends(currentUserId,fansUserId);
    }
}
