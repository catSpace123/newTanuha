package com.tanhua.server.controller;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小视频功能控制层
 */
@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService smallVideosService;

    /**
     * 小视频上传
     * @param videoThumbnail  封面图片信息
     * @param videoFile        小视频
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity smallVideos(MultipartFile videoThumbnail,MultipartFile videoFile){

        smallVideosService.upload(videoThumbnail,videoFile);
        return ResponseEntity.ok(null);
    }


    /**
     * 小视频列表分页查询
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity queryVideosList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pagesize){
             if(page == 0){
              page = 1 ;
            }
        PageResult<VideoVo> pageResult = smallVideosService.queryVideosList(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 视频用户关注
     * @return  uid 要关注的用户id
     */
    @RequestMapping("/{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") Long uid){

        smallVideosService.userFocus(uid);

        return ResponseEntity.ok(null);
    }

    /**
     * 视频用户取消关注
     * @return  uid 要关注的用户id
     */
    @RequestMapping("/{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") Long uid){

        smallVideosService.userUnFocus(uid);

        return ResponseEntity.ok(null);
    }
}
