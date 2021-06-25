package com.tanhua.server.controller;

import com.tanhua.commons.vo.HuanXinUser;
import com.tanhua.server.interceptor.UserHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



/**
 * 环信登录获取用户名token的控制层
 */
@RestController
@RequestMapping("/huanxin/user")
public class HuanxinController {

    /**
     * 返回给前端用户名密码，前端会拿着到环信云判断用户的登录状态，然后再能发送消息，打招呼
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity huanxinUser(){
        HuanXinUser huanXinUser = new HuanXinUser(UserHolder.getUserId().toString(),"123456","哇哈哈");
        return ResponseEntity.ok(huanXinUser);
    }
}
