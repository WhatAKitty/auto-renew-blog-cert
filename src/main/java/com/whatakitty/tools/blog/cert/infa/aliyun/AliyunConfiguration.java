package com.whatakitty.tools.blog.cert.infa.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云配置类
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@Configuration
@EnableConfigurationProperties(AliyunProperties.class)
@ConditionalOnProperty(name = "aliyun.enable", havingValue = "true")
public class AliyunConfiguration {

    @Bean
    public IClientProfile profile(AliyunProperties aliyunProperties) {
        //The client setting is valid for all requests
        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        clientConfig.setReadTimeoutMillis(aliyunProperties.getReadTimeoutMillis());
        clientConfig.setConnectionTimeoutMillis(aliyunProperties.getConnectionTimeoutMillis());

        IClientProfile profile = DefaultProfile.getProfile(
            aliyunProperties.getRegionId(), aliyunProperties.getAccessKey(), aliyunProperties.getSecretKey());
        profile.setHttpClientConfig(clientConfig);
        return profile;
    }

    @Bean
    public IAcsClient iAcsClient(IClientProfile profile) {
        return new DefaultAcsClient(profile);
    }

}
