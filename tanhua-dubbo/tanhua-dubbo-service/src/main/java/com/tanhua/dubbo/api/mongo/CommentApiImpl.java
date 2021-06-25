package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.mong.CommentApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


@Service
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 动态点赞
     * @param comment
     * @return  返回点赞记录数
     */
    @Override
    public long saveFabulous(Comment comment) {
        //a 给评论表添加记录
        //获取当前时间
        saveComment(comment);


        //b 修改发布表中的点赞记录
        updateCount(comment,1);
        //c 查询发布表中点赞记录
        Integer likeCount = getQueryCount(comment);

        return likeCount;
    }




    /**
     * 动态取消
     * @param comment
     * @return  返回点赞记录数
     */
    @Override
    public long deleteFabulous(Comment comment) {
        //a 删除发布表的点赞记录
        removeComment(comment);
        //b 修改发布表中的点赞记录
        updateCount(comment,-1);
        //c 查询发布表中点赞记录
        Integer likeCount = getQueryCount(comment);
        return likeCount;
    }

    /**
     * 动态喜欢
     * @param comment
     * @return 返回动态数量
     */
    @Override
    public long saveLove(Comment comment) {
        //a  向发布表中添加喜欢的记录
        saveComment(comment);

        //b 更新发布表中的喜欢记录
        updateCount(comment,1);

        //查询发布表中的喜欢记录数
        Integer count = getQueryCount(comment);
        return count;
    }

    /**
     * 取消动态喜欢
     * @param comment
     * @return 返回动态数量
     */
    @Override
    public long deleteLove(Comment comment) {
        //a 删除评论表中的喜欢记录
        removeComment(comment);
        //修改发布表中的喜欢记录shul
        updateCount(comment,-1);

        //查出返回发布表中的喜欢记录
        return getQueryCount(comment);
    }

    /**
     * 查询评论列表
     * @param page 当前页码
     * @param pagesize 每页记录数
     * @param movementId  动态id
     * @return
     */
    @Override
    public PageResult<Comment> findComments(Integer page, Integer pagesize, String movementId) {
        PageResult<Comment>  pageResult = new PageResult<>();  //返回对象
        //根据动态id  评论类型，，内容类型  时间降序查询评论
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId")
                .is(new ObjectId(movementId))
                .and("commentType").is(2)   //对评论的
                .and("pubType").is(1))      // 对动态
                .limit(pagesize).skip((page-1)*pagesize)
                .with(Sort.by(Sort.Order.desc("created"))); //对对时间降序排序
        //查询总记录数
        long count = mongoTemplate.count(query, Comment.class);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        long pages = count / pagesize + (count % pagesize > 0 ? 1 : 0);  //计算页码

        pageResult.setPages(pages);
        pageResult.setCounts(count);
        pageResult.setPage(page.longValue());
        pageResult.setPagesize(pagesize.longValue());
        pageResult.setItems(comments);
        return pageResult;
    }



    /**
     * 发表评论
     * @param comment1
     */
    @Override
    public void saveCommentList(Comment comment1) {
        //直接调用方法向评论表添加记录即可
        saveComment(comment1);
        //修改发布表中的评论数量
        updateCount(comment1,1);
    }


    /**
     * 评论点赞
     * @param comment
     * @return  返回点赞数量
     */
    @Override
    public long saveCommentLike(Comment comment) {
        //向评论表中添加记录
        saveComment(comment);

        //更新评论表中的点赞数量
        updateCount(comment,1);

        //查询评论表中的点赞数量
        Integer count = getQueryCount(comment);
        return count;
    }


     /**
     * 取消评论点赞
     * @param
     * @return  返回点赞数量
     */
    @Override
    public long onCommentLike(Comment comment) {
        //删除评论表中的点赞记录
        removeComment(comment);

        //修改评论表中的点赞记录
        updateCount(comment,-1);

        //查询表中的点赞记录
        Integer count = getQueryCount(comment);
        return count;
    }


    /**
     * 分页查询点赞喜欢评论列表
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult<Comment> findLikeOrLove(Integer page, Integer pagesize, Comment comment1) {
        PageResult<Comment> commentPageResult = new PageResult<>();
        //查询条件 当前用户id
        Query query = new Query();
        query.addCriteria(Criteria.where("commentUserId").is(comment1.getCommentUserId())
                .and("commentType").is(comment1.getCommentType()))   //类型是点赞
                .limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("created")));
        //查询总记录数
        long count = mongoTemplate.count(query, Comment.class);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        long pages = count / pagesize + (count % pagesize > 0 ? 1 : 0);  //计算页码

        commentPageResult.setPages(pages);
        commentPageResult.setCounts(count);
        commentPageResult.setPage(page.longValue());
        commentPageResult.setPagesize(pagesize.longValue());
        commentPageResult.setItems(comments);
        return commentPageResult;
    }


    /**
     * 向评论表中添加记录（对动态的喜欢，评论的点赞，动态的点赞等等）
     */
    private void saveComment(Comment comment) {
        //a 给评论表添加记录
        //获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        comment.setCreated(currentTimeMillis);

        //针对评论操作
        if(comment.getPubType() == 1){
            //根据发布id查询出发布userid
            Publish publish = mongoTemplate.findById(comment.getPublishId(), Publish.class);
            comment.setCommentUserId(publish.getUserId());
        }

        mongoTemplate.insert(comment);
    }


    /**
     * 删除发布表的点赞(喜欢，评论)记录
     * @param comment
     */
    private void removeComment(Comment comment) {
        //a 删除发布表的点赞(喜欢，评论)记录
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType())
                .and("pubType").is(comment.getPubType())
                .and("userId").is(comment.getUserId()));
        mongoTemplate.remove(query,Comment.class);
    }



    /**
     * 查询发布表中（点赞，喜欢，评论数量）
     * @param comment
     * @return
     */
    public Integer getQueryCount(Comment comment) {
        if(comment.getPubType() == 1) {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(comment.getPublishId()));
            //c 查询点赞数量
            Publish publish = mongoTemplate.findOne(query, Publish.class);
            //如果类型等于1表示返回点赞数量
            if (comment.getCommentType() == 1) {
                Integer likeCount = publish.getLikeCount();
                return likeCount;
            }
            //如果类型等于3表示返回喜欢数量
            if (comment.getCommentType() == 3) {
                return publish.getLoveCount();
            }
        }
        //对评论的操作
        if(comment.getPubType() == 3){
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(comment.getPublishId()));
            //c 查询点赞数量
            Comment comment1 = mongoTemplate.findOne(query, Comment.class);
            Integer count = comment1.getLikeCount();
            return count;
        }
        return 0;
    }


    /**
     * 修改发布表中动态记录，（点赞，喜欢，评论数量）
     * @param comment
     * @param number
     */
    public void updateCount(Comment comment,Integer number){

        if(comment.getPubType() == 1){
        //b 在更新发布表的点赞(喜欢,评论)记录数
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc(comment.getCol(),number);
        //对应动态的操作进入这里
            mongoTemplate.updateFirst(query, update, Publish.class);
        }

        if(comment.getPubType() == 3){
           //对评论操作
             //b 在更新发布表的点赞(喜欢,评论)记录数
             Query query = new Query();
             query.addCriteria(Criteria.where("id").is(comment.getPublishId()));
             Update update = new Update();
             update.inc(comment.getCol(),number);
            mongoTemplate.updateFirst(query,update,Comment.class);
        }
    }


}
