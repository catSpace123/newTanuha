package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.db.FollowUser;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.api.mong.SmallVideosApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 小视频服务提供者实现类
 */
@Service
public class SmallVideosApiImpl implements SmallVideosApi {


    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 小视频上传
     * @param video
     */
    @Override
    public void upload(Video video) {
        //向视频表添加记录
        video.setId(ObjectId.get());
        //当前时间
        long currentTimeMillis = System.currentTimeMillis();
        video.setCreated(currentTimeMillis);
        mongoTemplate.insert(video);
    }




    /**
     * 小视频列表分页查询
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult<Video> queryVideosList(Integer page, Integer pagesize) {

        //创建返回对象
        PageResult<Video> videoPageResult = new PageResult<>();
        //a 分页查询小视频
        Query query = new Query();
        query.limit(pagesize).skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("created")))  ;  //通过时间降序排列
        //查询总计数
        long count = mongoTemplate.count(query, Video.class);
        List<Video> videoList = mongoTemplate.find(query, Video.class);

        long pages = count / pagesize + (count % pagesize >0 ? 1 :0);   //总页数
        videoPageResult.setPage(page.longValue());
        videoPageResult.setPagesize(pagesize.longValue());
        videoPageResult.setPages(pages);
        videoPageResult.setCounts(count);
        videoPageResult.setItems(videoList);

        return videoPageResult;
    }


    /**
     * 视频用户关注
     * @param friendId  要关注的用户id
     */
    @Override
    public void saveUserFocus(Long friendId,Long currentUserId) {
        //先调用服务提供者查询follow_user表，判断当前要关注的用户是否关注了当前用户
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(friendId)
                .and("followUserId").is(currentUserId));

        //是否存在  （）
        boolean exists = mongoTemplate.exists(query, FollowUser.class);

        //如果等于空就表示该用户没关注我，然后向这个表添加一条我对该好友的单项关注
        if(!exists){
            insertFollowUser(currentUserId, friendId);
        }else{
            //如果不等于null表示该好友关注了我，所以要删除此条记录，
            mongoTemplate.remove(query,FollowUser.class);

            //然后向tanhua_users表添加俩条记录好友记录
            Friend friend = new Friend();
            friend.setId(ObjectId.get());
            friend.setUserId(currentUserId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.insert(friend);
            //第二条记录
            friend.setId(ObjectId.get());
            friend.setUserId(friendId);
            friend.setFriendId(currentUserId);
            mongoTemplate.insert(friend);
        }

    }



    /**
     * 视频取消关注
     * @param friendID
     * @param currentUserId
     */
    @Override
    public void deleteUserFocus(Long friendID, Long currentUserId) {
        //先调用服务提供者查询follow_user表，判断当前要关注的用户是否关注了当前用户

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId)
                .and("followUserId").is(friendID));
        boolean exists = mongoTemplate.exists(query, FollowUser.class);


        //如果为空就表示当前好友和取消关注的好友是共同好友关系 那么就去tanhua_users表中删除俩条记录然后向FollowUser添加一条单项好友关系
        if(exists){
            Query query0 = new Query();
            query0.addCriteria(Criteria.where("userId").is(currentUserId).and("friendId").is(friendID));
            mongoTemplate.remove(query0,Friend.class);

            Query query1 = new Query();
            query1.addCriteria(Criteria.where("userId").is(friendID).and("friendId").is(currentUserId));
            mongoTemplate.remove(query1,Friend.class);

            //在添加一条记录（调用方法）
            insertFollowUser(friendID, currentUserId);
        }else{
            //如何存在记录就表示单项关注（我只关注了他） 然后删除这条记录
            mongoTemplate.remove(query,FollowUser.class);
        }
    }


    /**
     * 向FollowUser中添加记录
     * @param currentUserId
     * @param friendId
     */
    public void insertFollowUser(Long currentUserId, Long friendId) {
        FollowUser followUser1 = new FollowUser();
        followUser1.setUserId(currentUserId);
        followUser1.setFollowUserId(friendId);
        followUser1.setCreated(System.currentTimeMillis());
        mongoTemplate.insert(followUser1);
    }

}
