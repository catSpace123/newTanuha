package com.tanhua.dubbo.api.mongo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.domain.vo.RecommendUserQuery;
import com.tanhua.domain.vo.UserLocationVo;
import com.tanhua.dubbo.api.mong.TodayBestApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者佳人信息查出实现类
 */
@Service
public class TodayBestApiImpl implements TodayBestApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //根据当前用户id查询佳人用户信息
    @Override
    public RecommendUser findTodayBestAndUserId(Long currentUserId) {
        //根据当前用户id查询对应的佳人id
        Query query = new Query(); //查询条件
        query.addCriteria(Criteria.where("toUserId").is(currentUserId))
                //按照成绩（缘分值降序） limit查询最大的一条数据  ，
             .with(Sort.by(Sort.Order.desc("score")))
            // .with(Sort.by(Sort.Direction.DESC,"score"))   这俩种写法都可以
             .limit(1);
        return  mongoTemplate.findOne(query,RecommendUser.class);
    }

    /**
     * 根据条件查询推荐用户列表（首页推荐）
     * @param recommendUserQuery  查询条件
     * @param currentUserId  //当前用户id
     * @return
     */
    @Override
    public PageResult<RecommendUser> findTodayRecommendByUser(RecommendUserQuery recommendUserQuery, Long currentUserId) {
            //用来返回的对象
        PageResult<RecommendUser> pageResult = new PageResult<>();


        Integer page = recommendUserQuery.getPage(); //当前页码
        Integer pagesize = recommendUserQuery.getPagesize();//每页记录数
        //a 分页查询推荐表
            //根据当前用户查询与之对应的推荐用户   分页查询  ，然后根据缘分值降序排列
        Query query = new Query();
        query.addCriteria(Criteria.where("toUserId").is(currentUserId))
                .limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("score")));

        //查询总记录数
        long counts = mongoTemplate.count(query, RecommendUser.class);
        List<RecommendUser> items = mongoTemplate.find(query, RecommendUser.class);

        long pages = counts / pagesize + (counts % pagesize >0 ? 1 : 0);

        pageResult.setPage(page.longValue());  //当强页码
        pageResult.setPagesize(pagesize.longValue()); //每页显示条数
        pageResult.setPages(pages);  //总共多少页
        pageResult.setCounts(counts); //总记录数
        pageResult.setItems(items);
        return pageResult;
    }


    /**
     * 查询推荐动态的个人信息
     * @param recommendId    推荐用户id
     * @return
     */
    @Override
    public RecommendUser queryPersonalInfo(Long recommendId, Long currentUserId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(recommendId).and("toUserId").is(currentUserId)).with(Sort.by(Sort.Order.desc("score")));
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }


    /**
     * 搜附近
     * @param distance  距离
     * @return
     */
    @Override
    public List<UserLocationVo> search(long distance ,long currentUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId));
        //a 先查询当前用户的距离
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);

        //b 获取当前用户位置
        GeoJsonPoint geoJsonPoint = userLocation.getLocation();
        //c 以当前用户为圆心搜附近的好友
        Distance distance1 = new Distance(distance/1000, Metrics.KILOMETERS);//传入米  ，转化为km

        Query query1 = new Query();
        //geoJsonPoint（圆心（我当前的位置） distance1（）半径（前端传入的距离））
        Circle circle = new Circle(geoJsonPoint,distance1);  //创建一个圆的对象  传入当前用户的位置，和要搜索的位置距离。
        query1.addCriteria(Criteria.where("location").withinSphere(circle));
        List<UserLocation> locationList = mongoTemplate.find(query1, UserLocation.class);


        //应为GeoJsonPoint 不能序列化 所有不能直接返回locationList  要转化为locationVo  返回
        return  UserLocationVo.formatToList(locationList);

    }


    /**
     * 喜欢好友
     * @param currentUserID  当前用户id
     * @param loveId  要喜欢的用户id
     */
    @Override
    public void saveLove(Long currentUserID, long loveId) {
        //先查询userlike表是否有单项喜欢记录
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(loveId).and("likeUserId").is(currentUserID));
        UserLike userLike = mongoTemplate.findOne(query, UserLike.class);

        if(userLike == null){
            //向userlike表添加一条喜欢记录
          userLike = new UserLike();
          userLike.setUserId(currentUserID);
          userLike.setLikeUserId(loveId);
          userLike.setCreated(DateUtil.date().getTime());
            mongoTemplate.insert(userLike);
            return;
        }
        //不等于空删除单项喜欢记录
        mongoTemplate.remove(query,UserLike.class);

        //向tanhuausers添加俩条记录(好友记录)
        saveUsers(currentUserID,loveId);

        saveUsers(loveId,currentUserID);


    }

    //用来保存喜欢（好友关系）
    public void saveUsers(Long currentUserID, long loveId){
        Friend friend = new Friend();
        friend.setId(ObjectId.get());
        friend.setUserId(currentUserID);
        friend.setFriendId(loveId);
        friend.setCreated(DateUtil.date().getTime());
        mongoTemplate.save(friend);
    }


}
