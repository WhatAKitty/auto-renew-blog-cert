package com.whatakitty.tools.blog.cert.infa.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云配置
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@Data
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {

    private String regionId;
    private String accessKey;
    private String secretKey;
    private long readTimeoutMillis;
    private long connectionTimeoutMillis;


}
