package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.CountsVo;
import com.tanhua.domain.vo.FriendVo;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户信息控制层
 */
@RestController
@RequestMapping("/users")
public class UserInFoController {
    /**
     * 查询用户信息
     * @return
     */
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;
    /**
     * 根据条件查询用户，，条件可以为空格
     * @param userID
     * @param huanxinID
     * @param //token
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findUserInFo(Long userID, Long huanxinID ){
        //调用service查询用户

        UserInfoVo  userInfoVo=  userInfoService.findUserInFo(userID,huanxinID);

        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 更新用户信息
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUser(@RequestBody UserInfoVo userInfoVo){
        //调用业务跟新用户信息
            userInfoService.updateUser(userInfoVo);
        return ResponseEntity.ok(null);

    }


    /**
     * 更新用户头像
     * @param headPhoto
     * @return
     */
    @RequestMapping(value = "/header",method = RequestMethod.POST)
    public ResponseEntity updateHeader(MultipartFile headPhoto){


        userService.updateHead(headPhoto);
        return ResponseEntity.ok(null);
    }


    /**
     * 更新手机号码——发送验证码
     */
    @RequestMapping(value = "/phone/sendVerificationCode",method = RequestMethod.POST)
    public ResponseEntity sendVerificationCode(){
        //从当前线程中获取电话号码
        User user = UserHolder.getUser();
        String mobile = user.getMobile();
        System.out.println("用户电话号码"+mobile);
        //调用发送验证码的方法发送验证码
        userService.sendCode(mobile);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新手机号码——校验验证码
     */
    @RequestMapping(value = "/phone/checkVerificationCode",method = RequestMethod.POST)
    public ResponseEntity checkVerificationCode(@RequestBody Map<String,String> params){
        //调用server经行验证码校验
        System.out.println(params.get("verificationCode")+"用户输入验证码");
        Map<String,Boolean> map = userService.checkVerificationCode(params.get("verificationCode"));

     return ResponseEntity.ok(map);
    }

    /**
     * 更换手机号码——接收新的手机号码
     */
    @RequestMapping(value = "/phone",method = RequestMethod.POST)
    public ResponseEntity updatePhone(@RequestBody  Map<String ,String> params,@RequestHeader("Authorization") String token){
        System.out.println(params.get("phone")+"新的电话号码");
        String phone = params.get("phone");
        //调用业务根据用户id修改电话号码


        userService.updatePhoneUserById(phone,token);
        return ResponseEntity.ok(null);
    }

    @RequestMapping("/counts")


    /**
     * 查询喜欢，互相喜欢，粉丝的数量
     */
    public ResponseEntity queryCounts(){

     CountsVo countsVo= userInfoService.queryCounts();
     return ResponseEntity.ok(countsVo);
    }


    /**
     * 互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     */
    @RequestMapping(value = "/friends/{type}",method = RequestMethod.GET)
    public ResponseEntity QueryFriendsList(@PathVariable("type") Integer type,@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10")
            Integer pagesize,String nickname){
        PageResult<FriendVo> pageResult = userInfoService.QueryFriendsList(type,page,pagesize,nickname);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 粉丝喜欢   粉丝列表的喜欢按钮
     * @param fansUserId  要关注的用户id
     * @return
     */
    @RequestMapping(value = "/fans/{uid}",method = RequestMethod.POST)
    public ResponseEntity saveFans(@PathVariable("uid") Long fansUserId){

        userInfoService.saveFansUser(fansUserId);
        return ResponseEntity.ok(null);
    }
}
