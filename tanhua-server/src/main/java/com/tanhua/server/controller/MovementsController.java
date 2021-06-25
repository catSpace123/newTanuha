package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.VisitorVo;
import com.tanhua.server.service.MovementsPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 圈子 （动态发布查询  ，控制层）
 */

@RestController
@RequestMapping("/movements")
public class MovementsController {

    @Autowired
    private MovementsPublishService movementsPublishService;





    /**
     * 发布动态，（圈子）
     * @param imageContent   发布的图片
     * @param publishVo  其他信息实体对象
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity movementsPublish(MultipartFile [] imageContent , PublishVo publishVo){

        movementsPublishService.saveMovementsPublish(imageContent,publishVo);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询好友动态
     * @param page  当前页码
     * @param pagesize  每页显示条数
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findFriendMovements(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize){


      PageResult<MomentVo>  pageResult = movementsPublishService.findFriendMovements(page,pagesize);

      return ResponseEntity.ok(pageResult);
    }


    /**
     * 查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(value = "/recommend",method =RequestMethod.GET)
    public ResponseEntity findRecommendMovements(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize){

        //调用server
      PageResult<MomentVo> pageResult  =  movementsPublishService.findRecommendMovements(page,pagesize);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询当前用户发布的动态（我的动态）
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    @RequestMapping(value = "/all",method =RequestMethod.GET)
    public ResponseEntity findCurrentUserMovements(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pagesize,@RequestParam Long userId){

        //调用server
        PageResult<MomentVo> pageResult  =  movementsPublishService.findCurrentUserMovements(page,pagesize,userId);

        return ResponseEntity.ok(pageResult);
    }


    /**
     * 动态点赞
     * @param publishId  动态id
     * @return 返回点赞数量
     */

    @RequestMapping(value = "/{id}/like",method = RequestMethod.GET)
    public ResponseEntity saveFabulous(@PathVariable("id") String publishId){
        System.out.println(publishId+"要添加的点赞id");
    long  count = movementsPublishService.saveFabulous(publishId);
        return ResponseEntity.ok(count);
    }


    /**
     * 取消点赞
     * @param publishId  动态id
     * @return  返回点赞数量
     */
    @RequestMapping(value = "/{id}/dislike",method = RequestMethod.GET)
    public ResponseEntity deleteFabulous(@PathVariable("id") String publishId){
        long  count = movementsPublishService.deleteFabulous(publishId);
        return ResponseEntity.ok(count);
    }

    /**
     * 动态喜欢
     * @param publishId 动态id
     * @return 返回动态数量
     */
    @RequestMapping(value = "/{id}/love",method = RequestMethod.GET)
    public ResponseEntity saveLove(@PathVariable("id") String publishId){
        long  count =  movementsPublishService.saveLove(publishId);
        return ResponseEntity.ok(count);
    }


    /**
     * 取消喜欢动态
     * @param publishId 动态id
     * @return 返回动态数量
     */
    @RequestMapping(value = "/{id}/unlove",method = RequestMethod.GET)
    public ResponseEntity unLove(@PathVariable("id") String publishId){
        long  count =  movementsPublishService.unLove(publishId);
        return ResponseEntity.ok(count);
    }

    /**
     * 查询单条动态
     * @param publishId  动态id
     * @return  返回动态信息以及用户信息
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public ResponseEntity findMovementsById(@PathVariable("id") String publishId){

        MomentVo momentVo = movementsPublishService.findMovementsById(publishId);
        return ResponseEntity.ok(momentVo);
    }


    /**
     * 查询访客
     * @return
     */
    @RequestMapping(value = "/visitors",method = RequestMethod.GET)
    public ResponseEntity findVisitors(){

        List<VisitorVo> list = movementsPublishService.findVisitors();

        return ResponseEntity.ok(list);
    }
}
