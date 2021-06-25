package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.dubbo.api.mong.UserLikeApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 喜欢关注控制层接口实现类
 */
@Service
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    //b 根据当前用户id 查询 喜欢数量
    @Override
    public long queryLikeCount(Long currentUserID) {

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserID));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }
    //根据当前用户查询查询粉丝数量
    @Override
    public long queryFansCount(Long currentUserID) {
        Query query = new Query();
        query.addCriteria(Criteria.where("likeUserId").is(currentUserID));
        long count = mongoTemplate.count(query, UserLike.class);
        return count;
    }

    //查询我的关注
    @Override
    public PageResult queryFollowLike(Integer page, Integer pagesize, Long currentUserId) {

        //创建返回对象
        PageResult PageResult = new PageResult<>();
        //a 分页查询小视频
        Query query = new Query();
        query.limit(pagesize).skip((page-1)*pagesize).addCriteria(Criteria.where("userId").is(currentUserId));
        //查询总计数
        long count = mongoTemplate.count(query, UserLike.class);
        List<UserLike> userLike = mongoTemplate.find(query, UserLike.class);

        long pages = count / pagesize + (count % pagesize >0 ? 1 :0);   //总页数
        PageResult.setPage(page.longValue());
        PageResult.setPagesize(pagesize.longValue());
        PageResult.setPages(pages);
        PageResult.setCounts(count);
        PageResult.setItems(userLike);

        return PageResult;
    }

    @Override
    public PageResult queryFaes(Integer page, Integer pagesize, Long currentUserId) {
        //创建返回对象
        PageResult PageResult = new PageResult<>();
        //a 分页查询小视频
        Query query = new Query();
        query.limit(pagesize).skip((page-1)*pagesize).addCriteria(Criteria.where("likeUserId").is(currentUserId));
        //查询总计数
        long count = mongoTemplate.count(query, UserLike.class);
        List<UserLike> userLike = mongoTemplate.find(query, UserLike.class);

        long pages = count / pagesize + (count % pagesize >0 ? 1 :0);   //总页数
        PageResult.setPage(page.longValue());
        PageResult.setPagesize(pagesize.longValue());
        PageResult.setPages(pages);
        PageResult.setCounts(count);
        PageResult.setItems(userLike);

        return PageResult;
    }


    //a 现根据粉丝用户id，删除user-like 表的粉丝记录
    @Override
    public void delete(Long currentUserId, Long fansUserId) {


        Query query = new Query();
        query.addCriteria(Criteria.where("likeUserId").is(currentUserId).and("userId").is(fansUserId));
        mongoTemplate.remove(query,UserLike.class);
    }
}
