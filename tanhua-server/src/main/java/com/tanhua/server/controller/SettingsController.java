package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用设置控制层
 */
@RestController
@RequestMapping("/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;



    /**
     * 通用设置，查询陌生人问题，通知设置，电话号码
     */
    @RequestMapping(value = "/settings",method = RequestMethod.GET)
    public ResponseEntity findSettings(){

        //调用业务查询
        SettingsVo settingsVo = settingsService.findSettings();

        return  ResponseEntity.ok(settingsVo);
    }


    /**
     * 更新或添加陌生人问题
     */
    @RequestMapping(value = "/questions",method = RequestMethod.POST)
    public ResponseEntity updateOrSaveQuestions(@RequestBody Map<String,String> params){

        //获取要更新到问题
        String questions = params.get("content");
        //调用业务
        settingsService.updateOrSaveQuestions(questions);
        return ResponseEntity.ok(null);
    }


    /**
     * 更新或添加通知设置记录
     */
    @RequestMapping(value = "/notifications/setting",method = RequestMethod.POST)
    public ResponseEntity updateOrSaveSettings(@RequestBody Map<String,Boolean> params){
        //取出相应的数据
        Boolean likeNotification = params.get("likeNotification"); //是否允许推送喜欢通知
        Boolean pinglunNotification = params.get("pinglunNotification");//是否允许推送评论
        Boolean gonggaoNotification = params.get("gonggaoNotification");//推送公告通知
        System.out.println(likeNotification+"=="+pinglunNotification+"=="+gonggaoNotification);

        //调用业务
        settingsService.updateOrSaveSettings(likeNotification,pinglunNotification,gonggaoNotification);

        return ResponseEntity.ok(null);
    }


    /**
     * 黑名单分页查询  （如果都不传，就给默认值）
     * @param page  当前页码
     * @param pagesize 每页显示条数
     * @return
     */
    @RequestMapping(value = "/blacklist",method = RequestMethod.GET)
    public ResponseEntity findBlackListPage(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize){
            //调用业务分页查询
        PageResult  pageResult  =  settingsService.findBlackListPage(page,pagesize);
       return ResponseEntity.ok(pageResult);
    }

    /**
     * 删除黑名单用户
     * @param uid
     * @return
     */
    @RequestMapping(value = "/blacklist/{uid}",method = RequestMethod.DELETE)
    public ResponseEntity deleteBlackListByUserId(@PathVariable("uid") Long uid){
        System.out.println(uid+"=======uid");
        settingsService.deleteBlackListByUserId(uid);
        return ResponseEntity.ok(null);
    }

}
