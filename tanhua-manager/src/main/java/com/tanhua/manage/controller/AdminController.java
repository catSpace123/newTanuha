package com.tanhua.manage.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSON;
import com.tanhua.manage.domain.Admin;
import com.tanhua.manage.service.AdminService;
import com.tanhua.manage.vo.AdminVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 后台登陆时 图片验证码 生成
     */
    @GetMapping("/verification")
    public void showValidateCodePic(String uuid,HttpServletRequest req, HttpServletResponse res){
        res.setDateHeader("Expires",0);
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        res.setHeader("Pragma", "no-cache");
        res.setContentType("image/jpeg");
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = lineCaptcha.getCode();
        log.debug("uuid={},code={}",uuid,code);
        adminService.saveCode(uuid,code);
        try {
            lineCaptcha.write(res.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用户登录
     * @param param
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody Map<String,String> param){
      String token1 =  adminService.login(param);
        Map<String,String> token = new HashMap<>();
        token.put("token",token1);
      return ResponseEntity.ok(token);
    }

    /**
     * 获取用户的基本信息
     * @param Authorization
     * @return
     */
    @RequestMapping(value = "/profile",method = RequestMethod.POST)
    public ResponseEntity getUserInfo(@RequestHeader("Authorization") String Authorization){
        //获取token
        String token = Authorization.replace("Bearer ", "");

        //在存redis获取用户信息
        String adminStr = (String) redisTemplate.opsForValue().get("MANAGE_TOKEN_" + token);

        Admin admin = JSON.parseObject(adminStr, Admin.class);
        AdminVo adminVo = new AdminVo();

        BeanUtils.copyProperties(admin,adminVo);
        return ResponseEntity.ok(adminVo);
    }

    /**
     * 退出
     * @return
     */
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ResponseEntity logout(@RequestHeader("Authorization") String Authorization){
        //获取token
        String token = Authorization.replace("Bearer ", "");
        adminService.logout(token);
        return ResponseEntity.ok(null);
    }
}