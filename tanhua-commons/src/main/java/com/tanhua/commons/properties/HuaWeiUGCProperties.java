package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 华为云配置
 */
@Configuration
@ConfigurationProperties(prefix = "tanhua.huawei")
@Data
public class HuaWeiUGCProperties {

    private String username;
    private String password;
    private String project;
    private String domain;
    private String categoriesText;
    private String categoriesImage;
    private String textApiUrl;
    private String imageApiUrl;
}