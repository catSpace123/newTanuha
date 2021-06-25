package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.dubbo.mapper.SettingsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 通用设置服务提供者业务类
 */
@Service
public class SettingsApiImpl implements SettingsApi{

    @Autowired
    private SettingsMapper settingsApiMapper;

    /**
     * 根据用户id查询通知设置
     * @param userId
     * @return
     */
    @Override
    public Settings findSettings(Long userId) {

        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
        return settingsApiMapper.selectOne(queryWrapper);
    }

    /**
     * 添加通知设置方法
     * @param settings
     */
    @Override
    public void saveSettings(Settings settings) {

        settingsApiMapper.insert(settings);
    }


    /**
     * 修改通知设置方法
     * @param settings
     */
    @Override
    public void updateSettings(Settings settings) {

        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",settings.getUserId());
        settingsApiMapper.update(settings,queryWrapper);
    }








}
