package com.tanhua.manage.listnenr;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.tanhua.manage.domain.Log;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.service.LogService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mq消费者监听业务类
 * topic  监听的主题
 * tnahuaManage 消费者的组名
 */
@Component
@RocketMQMessageListener(topic = "tanhua_log",consumerGroup = "tnahuaManage")
public class LogRocketMqListener implements RocketMQListener<String> {

    @Autowired
    private LogService logService;

    @Override
    public void onMessage(String message) {

        //将接收到的消息转化为map
        Map<String,String> map = JSON.parseObject(message, Map.class);

        String userId = map.get("user_id");
        String type = map.get("type");
        String logTime = map.get("log_time");

        //写入日志表
        Log log = new Log();
        log.setUserId(Long.valueOf(userId));
        log.setLogTime(logTime);
        log.setType(type);
        log.setEquipment("锤子u哇咔咔咔咔");
        log.setPlace("天安门啊");
        log.setCreated(new DateTime());
        logService.save(log);
    }


}
