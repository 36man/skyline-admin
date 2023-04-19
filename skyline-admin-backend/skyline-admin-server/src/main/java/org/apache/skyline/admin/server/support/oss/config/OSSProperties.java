package org.apache.skyline.admin.server.support.oss.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author jojocodex
 * @version @Id: OssProperties.java, v 0.1 2022年12月22日 09:42 jojocodex Exp $
 */
@Data
@ConfigurationProperties(prefix = "admin.oss")
@Validated
public class OSSProperties {
    private String endpoint = "xxx";

    private String bucketName;

    private String local;

    private String accessKey;

    private String secretKey;

    private String group = "public";

    private String region;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
     * 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;

    private StoreType storeType;



}