package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 百度接口，上传地理位置控制层
 */
@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;

    /**
     * 上传地理位置
     * @param param
     * @return
     */
    @RequestMapping(value = "/location",method = RequestMethod.POST)
    public ResponseEntity upLocation(@RequestBody Map<String,Object> param){
        System.out.println("请求进来了");
        baiduService.upLocation(param);
        return ResponseEntity.ok(null);
    }
}
