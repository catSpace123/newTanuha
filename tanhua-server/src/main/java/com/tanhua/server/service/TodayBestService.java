package com.tanhua.server.service;


import com.alibaba.fastjson.JSON;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.NearUserVo;
import com.tanhua.domain.vo.RecommendUserQuery;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.domain.vo.UserLocationVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserQuestionApi;
import com.tanhua.dubbo.api.mong.TodayBestApi;
import com.tanhua.server.interceptor.UserHolder;

import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 今日佳人消费者业务层
 */
@Service
public class TodayBestService {

    @Reference
    private TodayBestApi todayBestApi;


   @Reference
    private UserInfoApi userInfoApi;

   @Reference
   private UserQuestionApi userQuestionApi;

   @Autowired
   private HuanXinTemplate huanXinTemplate;
    /**
     * 今日佳人推荐查询佳人
     * @return
     */
    public TodayBestVo todayBestAndUserId() {
        Long currentUserId = UserHolder.getUserId(); //当前用户id
        //用来封装返回的数据对象
        TodayBestVo todayBestVo = new TodayBestVo();

        //1 先根据用户id查询mongdb查出跟当前用户最匹配的用户

        RecommendUser recommendUser = todayBestApi.findTodayBestAndUserId(currentUserId);
        if(StringUtils.isEmpty(recommendUser)){
                //用来给用户分装默认的佳人用户
            return todayBestVo;
        }
        getCommendUserInfo(todayBestVo, recommendUser);
        return todayBestVo;
    }

    /**
     * 用来在userinfo表查询用户基本信息
     * @param todayBestVo
     * @param recommendUser
     */
    private void getCommendUserInfo(TodayBestVo todayBestVo, RecommendUser recommendUser) {
        //如果不为空就拿着在mongodb的查到的佳人id去数据库查询对应的用户信息
        Long userId = recommendUser.getUserId();  //佳人用户id
        if(userId >0 ){
        UserInfo userInfo = userInfoApi.findByUserId(userId);

        //把userinfo的对象拷贝到todayBestVo中
        BeanUtils.copyProperties(userInfo,todayBestVo);
        //需要把把doubbo类型转化为long
        todayBestVo.setFateValue(recommendUser.getScore().longValue());
        todayBestVo.setTags(userInfo.getTags().split(","));
        }
    }

    /**
     * 首页推荐查询，推荐用户 今日推荐朋友
     * @param recommendUserQuery
     * @return
     */
    public PageResult<TodayBestVo> todayRecommendByUser(RecommendUserQuery recommendUserQuery) {
        //当前用户id
        Long currentUserId = UserHolder.getUserId();
        //创建用来返回的list集合
        PageResult<TodayBestVo> pageResultVo = new PageResult<>();

        //a 根据用户id和分页条件调用服务提供者在mongodb查询推荐用户
      PageResult<RecommendUser> userPageResult =  todayBestApi.findTodayRecommendByUser(recommendUserQuery,currentUserId);

      //b 如果没有推荐用户就给默认用户
      if(userPageResult == null || CollectionUtils.isEmpty(userPageResult.getItems())){
          //造默认数据
          userPageResult = new PageResult<>(10l,10l,1l,1l,null);
          List<RecommendUser> userList = defaultRecommend();
          userPageResult.setItems(userList);
      }
        List<TodayBestVo> list = new ArrayList<>();  //用来封装的数据
        //c 推荐用户不为空就那到推荐的用户id去userinfo表查询用户的基本信息

        List<RecommendUser> userList = userPageResult.getItems();

        for (RecommendUser recommendUser : userList) {
            TodayBestVo todayBestVo = new TodayBestVo();
            getCommendUserInfo(todayBestVo,recommendUser);
            list.add(todayBestVo);
        }
        //拷贝对象
        BeanUtils.copyProperties(userPageResult,pageResultVo);
        pageResultVo.setItems(list);
        return pageResultVo;
    }

    //构造默认数据
    private List<RecommendUser> defaultRecommend() {
        String ids = "1,2,3,4,5,6,7,8,9,10";
        List<RecommendUser> records = new ArrayList<>();
        for (String id : ids.split(",")) {
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(Long.valueOf(id));
            recommendUser.setScore(RandomUtils.nextDouble(70, 98));
            records.add(recommendUser);
        }
        return records;
    }


    /**
     * 查询推荐动态的个人信息
     * @param recommendId    推荐用户id
     * @return
     */
    public TodayBestVo queryPersonalInfo(Long recommendId) {
        //创建返回的对象
        TodayBestVo todayBestVo = new TodayBestVo();
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //根据当前用户id和推荐id查询该用户
        RecommendUser recommendUser =  todayBestApi.queryPersonalInfo(recommendId,currentUserId);
        if(recommendUser == null){

        }
        long b =1L;
        getCommendUserInfo(todayBestVo,recommendUser);
        todayBestVo.setId(b);
        b++;
        return todayBestVo;
    }


    /**
     * 查询陌生人问题
     * @param userId  陌生人的id
     * @return
     */
    public String queryQuestion(Long userId) {

        Question question = userQuestionApi.findQuestion(userId);
        String  txt = null;
        //如果该用户的问题为空就给默认值
        if(question == null || StringUtils.isEmpty(question.getTxt())){
            return txt = "约吗";
        }
        return question.getTxt();
    }

    /**
     * 回复陌生人问题
     * @param replyUserId   要回复的用户id
     *      reply   要回的信息
     * @return
     */
    public void replyQuestions(Integer replyUserId, String reply) {
        System.out.println(replyUserId+"是。。。");
        //获取当前用户id
        Long currentUserId = UserHolder.getUserId();
        //根据用户id查user info表 ，查询用户头像跟签名
        UserInfo userInfo = userInfoApi.findByUserId(currentUserId);

        //根据回复的id查询该用户的陌生人问题
        Question question = userQuestionApi.findQuestion(replyUserId.longValue());
        String  txt = "";
        //如果该用户的问题为空就给默认值
        if(question == null || StringUtils.isEmpty(question.getTxt())){
            txt = "约吗";
        }else{
            txt = question.getTxt();
        }

        //封装发消息的模板
        Map<String,Object> map = new HashMap<>();
        map.put("userId",currentUserId); //要回复的用户id
        map.put("nickname",userInfo.getNickname());//要回复的用户签名
        map.put("strangerQuestion",txt);      //被回复的陌生人问题
        map.put("reply",reply);            //要回复的信息

        String string = JSON.toJSONString(map);
        System.out.println(string);
        //被回复的用户id replyUserId
        huanXinTemplate.sendMsg(replyUserId.toString(),string);

    }


    /**
     * 搜附近
     * @param gender   性别
     * @param distance  距离
     * @return
     */
    public List<NearUserVo> search(String gender, long distance) {

        Long currentUserId = UserHolder.getUserId();

        List<NearUserVo> list = new ArrayList<>(); //返回数据
        //a 根据距离查询附近的好友
        List<UserLocationVo> list1 = todayBestApi.search(distance,currentUserId);

        if(CollectionUtils.isEmpty(list1)){
            return null;
        }
        for (UserLocationVo userLocationVo : list1) {

            NearUserVo nearUserVo = new NearUserVo();

            //搜到的附近人的id
            Long locationVoUserId = userLocationVo.getUserId();

            UserInfo userInfo = userInfoApi.findByUserId(locationVoUserId);

            //先排除自己  如果id等于自己就跳过
           if(userInfo.getId().toString().equals(currentUserId.toString())) {
               continue;
           }
            //排除性别不相同的
            if(!userInfo.getGender().equals(gender)){
                 continue;
            }
            BeanUtils.copyProperties(userInfo,nearUserVo);
            nearUserVo.setUserId(userInfo.getId());
            list.add(nearUserVo);
        }
        return list;
    }


    /**
     * 探花喜欢不喜欢
     * @return
     */
    public void saveLove(long loveId) {

        //获取当前用户id
        Long currentUserID = UserHolder.getUserId();

        //调用服务提供者保存喜欢的好友
        todayBestApi.saveLove(currentUserID,loveId);

    }
}
