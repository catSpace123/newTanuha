package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.NearUserVo;
import com.tanhua.domain.vo.RecommendUserQuery;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.TodayBestService;
import org.apache.dubbo.remoting.exchange.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 今日佳人控制层
 */
@RestController
@RequestMapping("/tanhua")
public class RecommendController {

    @Autowired
    private TodayBestService todayBestService;


    /**
     * 今日佳人推荐查询控制层 查询佳人
     * @return
     */
    @RequestMapping(value = "/todayBest",method = RequestMethod.GET)
    public ResponseEntity todayBest(){


       TodayBestVo todayBestVo =  todayBestService.todayBestAndUserId();
        return ResponseEntity.ok(todayBestVo);
    }

    /**
     * 首页推荐查询，推荐用户 今日推荐朋友
     */

    @RequestMapping(value = "/recommendation",method = RequestMethod.GET)
    public ResponseEntity todayRecommendByUser(RecommendUserQuery recommendUserQuery){

        PageResult<TodayBestVo> pageResult =  todayBestService.todayRecommendByUser(recommendUserQuery);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询推荐动态的个人信息
     * @param recommendId    推荐用户id
     * @return
     */
    @RequestMapping(value = "/{id}/personalInfo",method = RequestMethod.GET)
    public ResponseEntity personalInfo(@PathVariable("id") Long recommendId){


        TodayBestVo todayBestVo = todayBestService.queryPersonalInfo(recommendId);

        return ResponseEntity.ok(todayBestVo);
    }


    /**
     * 查询陌生人问题
     * @param userId  陌生人的id
     * @return
     */
    @RequestMapping(value = "/strangerQuestions",method = RequestMethod.GET)
    public ResponseEntity strangerQuestions(@RequestParam  Long userId){
        String question = todayBestService.queryQuestion(userId);

        return ResponseEntity.ok(question);
    }

    /**
     * 回复陌生人问题
     * @param param
     * @return
     */
    @RequestMapping(value = "/strangerQuestions",method = RequestMethod.POST)
    public ResponseEntity replyQuestions(@RequestBody Map param){

       Integer replyUserId = (Integer) param.get("userId");  //要回复的用户id
        String reply = (String) param.get("reply");          //要回的信息
        todayBestService.replyQuestions(replyUserId,reply);
        return ResponseEntity.ok(null);
    }


    /**
     * 搜附近
     * @param gender   性别
     * @param distance  距离
     * @return
     */
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public ResponseEntity search(@RequestParam String gender,@RequestParam(defaultValue = "500") long  distance){

        List<NearUserVo> list = todayBestService.search(gender,distance);
        return ResponseEntity.ok(list);
    }


    /**
     * 探花喜欢不喜欢
     * @return
     */
    @RequestMapping(value = "/{id}/love",method = RequestMethod.GET)
    public ResponseEntity saveLove(@PathVariable("id") long LoveId){

        System.out.println(LoveId+"要喜欢的用户id");
        //调用业务层添加喜欢
        todayBestService.saveLove(LoveId);

     return  ResponseEntity.ok(null);

    }


}
