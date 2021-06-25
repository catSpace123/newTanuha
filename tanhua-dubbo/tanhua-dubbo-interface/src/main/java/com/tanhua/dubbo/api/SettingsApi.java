package com.tanhua.dubbo.api;

import com.tanhua.domain.db.PageResult;
import com.tanhua.domain.db.Settings;

/**
 * 通知设置服务提供者接口
 */
public interface SettingsApi {
    /**
     * 根据用户id查询通知设置
     * @param userId
     * @return
     */
     Settings findSettings(Long userId) ;


    /**
     * 添加通知设置方法
     * @param settings
     */
    void saveSettings(Settings settings);

    /**
     * 修改通知设置方法
     * @param settings
     */
    void updateSettings(Settings settings);


}
