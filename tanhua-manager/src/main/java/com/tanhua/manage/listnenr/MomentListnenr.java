package com.tanhua.manage.listnenr;

import com.tanhua.commons.templates.HuaWeiUGCTemplate;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.dubbo.api.mong.MovementsPublishApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 动态消息的监听类
 */
@Component
@RocketMQMessageListener(topic = "tanhua_publishId",consumerGroup = "tanhua111")
public class MomentListnenr implements RocketMQListener<String> {


    @Autowired
    private HuaWeiUGCTemplate huaWeiUGCTemplate;

    @Autowired
    private MovementsPublishApi movementsPublishApi;


    @Override
    public void onMessage(String publishID) {

        if(!StringUtils.isEmpty(publishID)){


        //调用服务提供者查询发布的内容和图片url
        Publish publish = movementsPublishApi.findMovementsById(new ObjectId(publishID));
        //获取文本内容和图片uil
        String textContent = publish.getTextContent();  //文本信息
        List<String> ImgUil = publish.getMedias();      //图片uil

        //调用华为云，审核图片
        boolean textFlag = huaWeiUGCTemplate.textContentCheck(textContent);
        boolean imgFlag = huaWeiUGCTemplate.imageContentCheck(ImgUil.toArray(new String[]{}));
        //如果都审核通过就修改发布表的动态状态
        //定义审核状态默认为人工审核
        Integer state = 2;
        if(textFlag && imgFlag){
           state = 1;  //表示审核通过
            movementsPublishApi.updateState(publishID,state);

            System.out.println("审核通过");
        }else{
            //审核不通过该状态为人工状态 2
            movementsPublishApi.updateState(publishID,state);
            System.out.println("审核不通过");
        }
        }
    }
}
