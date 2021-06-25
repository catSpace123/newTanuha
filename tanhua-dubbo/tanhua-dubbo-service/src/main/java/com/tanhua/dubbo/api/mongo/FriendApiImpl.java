package com.tanhua.dubbo.api.mongo;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.dubbo.api.mong.FriendApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

/**
 * 查询好友列表接口实现类
 */
@Service
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 查询好友列表
     * @param page
     * @param pagesize
     * @param keyword
     * @param currentUserID
     * @return
     */
    @Override
    public PageResult<Friend> queryContacts(Integer page, Integer pagesize, String keyword, Long currentUserID) {
        PageResult<Friend> pageResult = new PageResult<>();

        //分页查询朋友表
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserID))
                .limit(pagesize).skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        long count = mongoTemplate.count(query, Friend.class); //总记录数
        List<Friend> friendList = mongoTemplate.find(query, Friend.class);
        long pages = count / pagesize + (count % pagesize >0 ? 1 : 0);
        pageResult.setCounts(count);
        pageResult.setPages(pages);
        pageResult.setPage(page.longValue());
        pageResult.setPagesize(pagesize.longValue());
        pageResult.setItems(friendList);
        return pageResult;
    }


    //根据当前用户id查询关注数量
    @Override
    public long queryFollowCount(Long currentUserID) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserID));
        long count =  mongoTemplate.count(query, Friend.class);
        return count ;
    }


    //b 向tanhua-user保存俩条记录
    @Override
    public void saveFansUser(Long currentUserId, Long fansUserId) {

        //先查询是否存在好友关系
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId).and("friendId").is(fansUserId));
        boolean exists = mongoTemplate.exists(query, Friend.class);

        //不存在则保存第一条数据  我的好友
        if(!exists){
            Friend friend = new Friend();
            friend.setId(ObjectId.get());
            friend.setCreated(System.currentTimeMillis());
            friend.setUserId(currentUserId);
            friend.setFriendId(fansUserId);
            mongoTemplate.insert(friend);
        }



        //先查是询否存在好友关系
        Query query1 = new Query();
        query.addCriteria(Criteria.where("friendId").is(currentUserId).and("userId").is(fansUserId));
        boolean exists1 = mongoTemplate.exists(query1, Friend.class);
        //第二条对方的好友  不存在则保存第二条数据
        if(!exists1){
            Friend friend1 = new Friend();
            friend1.setId(ObjectId.get());
            friend1.setUserId(fansUserId);
            friend1.setFriendId(currentUserId);
            friend1.setCreated(System.currentTimeMillis());
            mongoTemplate.insert(friend1);
        }

    }
}
