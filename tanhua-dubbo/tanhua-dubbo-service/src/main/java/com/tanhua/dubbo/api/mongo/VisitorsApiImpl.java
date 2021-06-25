package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.Visitor;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.api.mong.VisitorsApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
/**
 * 访客控制层接口实现类
 */
@Service
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


     //a 如果时间为空就根据当前用户id查询访客表的前五条记录
    @Override
    public List<Visitor> findVisitors(Long currentUserId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId)).limit(5);
        List<Visitor> visitorList = mongoTemplate.find(query, Visitor.class);
        return visitorList;
    }
    //b如果不为空就根据当前用户上一次的登录时间跟id查询这段时间内的访客记录
    @Override
    public List<Visitor> findVisitors(Long currentUserId, String loginTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId)
            .and("date").gt(loginTime)).limit(5);  //查询出大于这个时间段访问的用户
        List<Visitor> visitorList = mongoTemplate.find(query, Visitor.class);
        return visitorList;
    }


    //测试数据保存方法
    @Override
    public void save(Visitor visitor) {
        mongoTemplate.save(visitor);
    }

    //查询谁看过我
    @Override
    public PageResult findVisitorsList(Integer page, Integer pagesize, Long currentUserId) {
        //创建返回对象
        PageResult PageResult = new PageResult<>();
        //a 查询谁看过我
        Query query = new Query();
        query.limit(pagesize).skip((page-1)*pagesize).addCriteria(Criteria.where("userId").is(currentUserId)
                );
        //查询总计数
        long count = mongoTemplate.count(query, Visitor.class);
        List<Visitor> visitorList = mongoTemplate.find(query, Visitor.class);

        long pages = count / pagesize + (count % pagesize >0 ? 1 :0);   //总页数
        PageResult.setPage(page.longValue());
        PageResult.setPagesize(pagesize.longValue());
        PageResult.setPages(pages);
        PageResult.setCounts(count);
        PageResult.setItems(visitorList);

        return PageResult;
    }
}
