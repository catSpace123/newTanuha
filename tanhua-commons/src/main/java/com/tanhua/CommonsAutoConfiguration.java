package com.tanhua;

import com.tanhua.commons.properties.*;
import com.tanhua.commons.templates.*;
import com.tanhua.commons.vo.HuanXinUser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 自定义自动配置类，启动就加载模板创建对象
 */
@Configuration
@EnableConfigurationProperties({SmsProperties.class,
                                OssProperties.class,
                                FaceProperties.class,
                                HuanXinProperties.class,
                                HuaWeiUGCProperties.class})
public class CommonsAutoConfiguration {

    /**
     * 验证码模板对象
     * @param smsProperties
     * @return
     */
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        SmsTemplate smsTemplate = new SmsTemplate(smsProperties);
        smsTemplate.init();
        return smsTemplate;
    }

    /**
     * 阿里云图片上传组件对象
     * @param ossProperties
     * @return
     */
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }

    /**
     * 人脸识别组件
     * @param faceProperties
     * @return
     */
    @Bean
    public FaceTemplate faceTemplate(FaceProperties faceProperties){
        return new FaceTemplate(faceProperties);
    }

    /*
     * 环信即时通信
     * @param huanXinProperties
     * @return
     */
    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties){

        return new HuanXinTemplate(huanXinProperties);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder){

        return templateBuilder.build();
    }


    /**
     * 华为云
     */

    @Bean
    public HuaWeiUGCTemplate huaWeiUGCTemplate(HuaWeiUGCProperties properties) {
        return new HuaWeiUGCTemplate(properties);
    }



}
