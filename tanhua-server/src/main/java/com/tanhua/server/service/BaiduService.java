package com.tanhua.server.service;

import com.tanhua.dubbo.api.mong.BaiduApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 百度接口，上传地理位置业务层
 */
@Service
public class BaiduService {

    @Reference
    private BaiduApi baiduApi;

    /**
     * 上传地理位置
     * @param param
     * @return
     */
    public void upLocation(Map<String, Object> param) {

        //获取当前用户id
        Long currentUserID = UserHolder.getUserId();
        Double latitude = (Double) param.get("latitude");  //纬度
        Double longitude = (Double) param.get("longitude"); //经度
        String addrStr = (String) param.get("addrStr");     //位置描述


        //调用服务提供者存储
        baiduApi.upLocation(latitude,longitude,addrStr,currentUserID);
    }
}
