package com.whatakitty.tools.blog.cert.core.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 博客信息
 *
 * @author WhatAKitty
 * @date 2019/03/21
 * @description
 **/
@Data
@ConfigurationProperties("blog")
public class BlogInfo {

    private String title;
    private String domain;
    private String resRr;
    private BlogHostInfo host;
    private CDNInfo cdn;
    private List<String> batchCmds;

    public String getStaticDomain() {
        return String.format("%s.%s", resRr, domain);
    }

}
