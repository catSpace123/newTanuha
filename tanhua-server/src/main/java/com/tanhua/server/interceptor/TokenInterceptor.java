package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一处理token的拦截器
 * 自定义一个拦截器类，实现处理拦截器的接口
 * 实现方法，具体实现方法看要在请求前，还是请求后，还是页面渲染后拦截执行
 * 这里就需要在请求拦截之间获取token，判断用户登录状态
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;  //因为userservice里面有中redis中获取token的方法
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入到拦截器啦。。。。。。");
        //1从请求头中获取token
        String token = request.getHeader("Authorization");
        System.out.println(token+"========");
        //2调用方法获取redis中是否有token
        User user = userService.getRedisByUser(token);
        //3如果为空就抛出异常返回401  （没有权限）
            if(StringUtils.isEmpty(user)){
                response.setStatus(401);
                return false;
            }

            //4不为空的就把user存入到ThreadLocale   在一个线程里面随时都可以获取数据
        UserHolder.setUser(user);
        return true;
    }
}
