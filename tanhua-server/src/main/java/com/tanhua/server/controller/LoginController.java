package com.tanhua.server.controller;

import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import java.util.Map;

/**
 * 用户控制层
 */
@RestController
@RequestMapping("/user")
public class LoginController {


    @Autowired
    private UserService userService;



    /**
     * 接收电话号码，发送验证码。
     */
    @RequestMapping("/login")
    public ResponseEntity sendCode(@RequestBody Map<String,String> params){
        String phone = params.get("phone");

        //调用业务发送短信
        userService.sendCode(phone);
        //发送验证码不需要返回数据，
        return ResponseEntity.ok(null);
    }



    /**
     * 用于校验验证码，用户登录
     * @param params
     * @return
     */
    @RequestMapping(value = "/loginVerification",method = RequestMethod.POST)
    public ResponseEntity loginVerificationCode(@RequestBody Map<String,String> params){
      Map  map= userService.loginVerificationCode(params);

      return ResponseEntity.ok(map);
    }

    /**
     * 完善用户信息
     * @param userInfoVo
     * @param //token
     * @return
     */
    @RequestMapping(value = "/loginReginfo",method = RequestMethod.POST)
    public ResponseEntity loginReginfo(@RequestBody UserInfoVo userInfoVo){
       //调用业务
        userService.loginReginfoAdd(userInfoVo);

        return ResponseEntity.ok(null);
    }

    /**
     * 接收设置头像请求，人脸识别，图片上传
     * @return
     */
    @RequestMapping(value = "/loginReginfo/head",method = RequestMethod.POST)
    public ResponseEntity loginReginfoHead(MultipartFile headPhoto ){

            userService.uplondHead(headPhoto);

        return ResponseEntity.ok(null);
    }

}
