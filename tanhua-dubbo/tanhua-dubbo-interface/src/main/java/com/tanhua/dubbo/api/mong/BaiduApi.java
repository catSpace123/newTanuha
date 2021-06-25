package com.tanhua.dubbo.api.mong;
/**
 * 百度接口，上传地理位置服务提供者接口
 */
public interface BaiduApi {
    /**
     * 保存地理位置
     * @param latitude  //纬度
     * @param longitude//经度
     * @param addrStr//位置描述
     */
    void upLocation(Double latitude, Double longitude, String addrStr,Long currentUserID);
}
