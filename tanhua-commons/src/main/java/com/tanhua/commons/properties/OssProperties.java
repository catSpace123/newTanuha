package com.tanhua.commons.properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * t图片上传需要用到的组件配置类
 */
@Data
@ConfigurationProperties(prefix = "tanhua.oss")
public class OssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String url;//sztanhua.oss-cn-shenzhen.aliyuncs.com
}