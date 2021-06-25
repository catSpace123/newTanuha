package com.tanhua.dubbo.api.mongo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.mong.MovementsPublishApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈子（动态） 服务提供者接口实现类
 */
@Service
public class MovementsPublishApiImpl implements MovementsPublishApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 发布动态，（圈子）
     * @param publishVo  发布信息实体对象
     * @return
     */
    @Override
    public String saveMovementsPublish(PublishVo publishVo) {
        //向发布表中添加记录
        //获取当前时间
        long currentTimeMillis = System.currentTimeMillis();  //当前时间
        Long currentUserID = publishVo.getUserId();//当前用户id
        // a 创建与发布表关联的实体对象 ，添加数据
        Publish publish = new Publish();
        //赋值
        BeanUtils.copyProperties(publishVo,publish);
        publish.setId(ObjectId.get());  //发布id
        publish.setPid(66L);  //用于系统推荐的id 可以不设置
        publish.setSeeType(1);  //表示公开，谁都可以看
        publish.setState(0);   //审核状态  0   待审核
        publish.setLocationName(publishVo.getLocation());  //地理位置
        publish.setCreated(currentTimeMillis);  //当前发布的时间

        String publishId = String.valueOf(publish.getId());
        //b向发布表中添加数据
         mongoTemplate.save(publish);

        //c 在给相册表添加数据（主要是用于用户自己查询自己的动态的时候跟快速）
        Album album = new Album();
        album.setId(ObjectId.get());
        album.setPublishId(publish.getId());  //发布的id
        album.setCreated(currentTimeMillis);  //发布的时间
        // d 向相册表添加记录，应为每一个新用户第一次发布动态就会创建属于自己的相册表
        mongoTemplate.insert(album,"quanzi_album_"+currentUserID);

        //e 在根据当前用户id查询好友集合（表）中的好友信息id  Friend 好友表实体
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserID));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        //判断是否为空
        if(!CollectionUtils.isEmpty(friends)){
            for (Friend friend : friends) {
                //f 如果查到好列表不为空，就向好友的时间线表中插入我的发布动态id
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setUserId(currentUserID);  //当前用户id
                timeLine.setPublishId(publish.getId());  //发布的动态id
                timeLine.setCreated(currentTimeMillis);  //发布的时间
                //向好友的时间线表插入我当前用户的发布id
                mongoTemplate.save(timeLine,"quanzi_time_line_"+friend.getFriendId());
            }
        }

        return publishId;

    }



    /**
     * 查询好友动态
     * @param page  当前页码
     * @param pagesize  每页显示条数
     * @return
     */
    @Override
    public PageResult<Publish> findFriendMovements(Integer page, Integer pagesize, Long currentUserId) {
       //用来返回的数据
        PageResult<Publish> publishPageResult = new PageResult<>();
            List<Publish> list = new ArrayList<>();

        //根据当前用户id到mongdb的时间线表查询好友发布的动态id

        Query query = new Query();
        //分页查询条件
        query.limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("created")));
        //查询总记录数
        long count = mongoTemplate.count(query, TimeLine.class, "quanzi_time_line_" + currentUserId);
        List<TimeLine> friendList = mongoTemplate.find(query,TimeLine.class,"quanzi_time_line_"+currentUserId);

        if(CollectionUtils.isEmpty(friendList)){
            return null;
        }
        // 不为空就拿发布id到发布表查询发布的动态信息
        for (TimeLine timeLine : friendList) {
            //根据发布id查询
            Publish publish = mongoTemplate.findById(timeLine.getPublishId(), Publish.class);
            list.add(publish);
        }
        long pages = (long) Math.ceil(count / pagesize);
        //封装数据
        publishPageResult.setItems(list);
        publishPageResult.setCounts(count);
        publishPageResult.setPage(page.longValue());
        publishPageResult.setPagesize(pagesize.longValue());
        publishPageResult.setPages(pages);

        return publishPageResult;
    }


    /**
     * 查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult<Publish> findRecommendMovenents(Integer page, Integer pagesize, Long currentUserId) {
        //创建要返回的对象
        PageResult<Publish> publishPageResult = new PageResult<>();

        List<Publish> list = new ArrayList<>();

        //根据当前用户id去推荐圈子中获取推荐的动态id

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserId))
                .limit(pagesize)
                .skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("score")));
               //实际项目要加当前时间 .addCriteria(Criteria.where("created").is(System.currentTimeMillis()));
        //查询总记录数
        long count = mongoTemplate.count(query, RecommendQuanzi.class);
        List<RecommendQuanzi> recommendQuanzis = mongoTemplate.find(query, RecommendQuanzi.class);

        if(CollectionUtils.isEmpty(recommendQuanzis) || count == 0L){
            return null;
        }
        //如果不为空就去发布表中查询发布的动态
        for (RecommendQuanzi recommendQuanzi : recommendQuanzis) {

            Publish publish = mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
            if(publish != null){
                list.add(publish);
            }
        }
        //计算总页数
        long pages = count / pagesize + (count % pagesize > 0 ? 1 : 0 );
        publishPageResult.setPage(page.longValue());
        publishPageResult.setPagesize(pagesize.longValue());
        publishPageResult.setCounts(count);
        publishPageResult.setPages(pages);
        publishPageResult.setItems(list);

        return publishPageResult;
    }




    /**
     * 查询当前用户发布的动态
     * @param page
     * @param pagesize
     * @param userId  当前用户的id
     * @return
     */
    @Override
    public PageResult<Publish> findCurrentUserMovements(Integer page, Integer pagesize, Long userId) {
        //创建返回的对象
        PageResult<Publish> publishPageResult = new PageResult<>();

        List<Publish> list = new ArrayList<>();
        //到用户相册表查询自己的发布动态ids
        Query query = new Query();

        query.limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("created")));
        //查询总记录条数‘
        long count = mongoTemplate.count(query, Album.class,"quanzi_album_" + userId);
        List<Album> albums = mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);
        if(CollectionUtils.isEmpty(albums)){
            return null;
        }

        for (Album album : albums) {
            //查询到发布的ids后在去发布表查询发布的动态
            Publish publish = mongoTemplate.findById(album.getPublishId(),Publish.class);
            list.add(publish);
        }
        //计算总页数
        long pages = count / pagesize + (count % pagesize > 0 ? 1 : 0);

        publishPageResult.setPage(page.longValue());
        publishPageResult.setCounts(count);
        publishPageResult.setPagesize(pagesize.longValue());
        publishPageResult.setPages(pages);
        publishPageResult.setItems(list);

        return publishPageResult;
    }


    /**
     * 查询单条动态
     * @param objectID  动态id
     * @return  返回动态信息以及用户信息
     */
    @Override
    public Publish findMovementsById(ObjectId objectID) {
        System.out.println(objectID+"========");
        //根据发布id 查询动态信息
        return mongoTemplate.findById(objectID, Publish.class);
    }



    /**
     * 修改动态的状态
     * @param publishID
     * @param state
     */
    @Override
    public void updateState(String publishID, Integer state) {
        Query query = new  Query();
        query.addCriteria(Criteria.where("id").is(new ObjectId(publishID)));

        Update update = new Update();
        update.set("state",state);
        mongoTemplate.updateFirst(query,update,Publish.class);
    }


}
