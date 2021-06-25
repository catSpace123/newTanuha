package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Video;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 保存用户信息实现类
 */
@Service
public class UserInfoApiImpl implements UserInfoApi{

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存用户基本信息
     * @param userInfo
     */
    @Override
    public void loginReginfoAdd(UserInfo userInfo) {

        userInfoMapper.insert(userInfo);

    }

    /**
     * 更新用户头像
     * @param userInfo
     */
    @Override
    public void updateAvatar(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据id查询用户
     * @param currentId
     * @return
     */
    @Override
    public UserInfo findByUserId(Long currentId) {

        return userInfoMapper.selectById(currentId);

    }


    /**
     * 用户信息翻页查询
     * @param page1
     * @param pagesize
     * @return
     */
    @Override
    public PageResult<UserInfo> queryUserInfoList(long page1, long pagesize) {
        PageResult<UserInfo> pageResult = new PageResult<>();


        //创建分页查询条件
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        //分页查询对象
        Page<UserInfo> page =new Page<>();
        page.setCurrent(page1).setSize(pagesize);
        IPage<UserInfo> userInfoIPage = userInfoMapper.selectPage(page, query);

        //设置返回值
        pageResult.setCounts(userInfoIPage.getTotal());
        pageResult.setPage(page1);
        pageResult.setPagesize(pagesize);
        pageResult.setPages(userInfoIPage.getPages());
        pageResult.setItems(userInfoIPage.getRecords());
        return pageResult;
    }


}
