package com.whatakitty.tools.blog.cert.infa.qiniu;

import com.qiniu.cdn.CdnManager;
import com.qiniu.util.Auth;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.CdnCertManager;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.domain.DomainManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 七牛配置
 *
 * @author WhatAKitty
 * @date 2019/03/17
 * @description
 **/
@Configuration
@ConditionalOnProperty(name = "qiniu.enable", havingValue = "true")
@EnableConfigurationProperties(QiniuProperties.class)
public class QiniuConfiguration {

    @Bean
    public Auth auth(QiniuProperties qiniuProperties) {
        return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
    }

    @Bean
    public CdnManager cdnManager(Auth auth) {
        return new CdnManager(auth);
    }

    @Bean
    public CdnCertManager cdnCertManager(Auth auth, QiniuProperties qiniuProperties) {
        return new CdnCertManager(auth, qiniuProperties.getServer());
    }

    @Bean
    public DomainManager domainManager(Auth auth, QiniuProperties qiniuProperties) {
        return new DomainManager(auth, qiniuProperties.getServer());
    }

}
