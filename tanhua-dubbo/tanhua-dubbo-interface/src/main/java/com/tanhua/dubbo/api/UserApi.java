package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;

/**
 *      服务提供者接口
 */
public interface UserApi {

    /**
     * 添加用户
     * @param user
     * @return
     */
    Long save(User user);

    /**
     * 通过手机号码查询
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);


    /**
     * 修改手机号码
     * @param user
     */
    void updatePhoneUserById(User user);
}
