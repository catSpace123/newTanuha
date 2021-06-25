package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 服务提供者 实现类
 */
@Service
public class UserApiImpl implements UserApi{

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加方法
     * @param user
     * @return 返回添加后的id
     */
    @Override
    public Long save(User user) {
        //Date date = new Date();
       // user.setCreated(date);
       // user.setUpdated(date);
         userMapper.insert(user);


         //返回用户id 当添加成功后会自动把添加后的id存入到对象的id里面
        return user.getId() ;
    }

    /**
     * 根据用户名查询用户
     * @param mobile
     * @return
     */
    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mobile",mobile);
            
        return   userMapper.selectOne(queryWrapper);

    }

    /**
     * 根据用户id更新手机号码
     * @param user
     */
    @Override
    public void updatePhoneUserById(User user) {
        userMapper.updateById(user);
    }


}
