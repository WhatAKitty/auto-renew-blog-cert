package com.whatakitty.tools.blog.cert.core;

import com.whatakitty.tools.blog.cert.core.properties.BlogInfo;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 博客自动配置类
 *
 * @author WhatAKitty
 * @date 2019/03/21
 * @description
 **/
@Configuration
@EnableConfigurationProperties(BlogInfo.class)
public class BlogAutoConfiguration {

}
