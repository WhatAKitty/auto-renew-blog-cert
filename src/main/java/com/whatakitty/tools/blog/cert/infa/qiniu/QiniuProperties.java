package com.whatakitty.tools.blog.cert.infa.qiniu;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 七牛配置
 *
 * @author WhatAKitty
 * @date 2019/03/17
 * @description
 **/
@ConfigurationProperties(prefix = "qiniu")
@Data
public class QiniuProperties {

    /**
     * 访问Key
     */
    private String accessKey;
    /**
     * 访问密钥
     */
    private String secretKey;
    /**
     * 服务器地址
     */
    private String server;

}
