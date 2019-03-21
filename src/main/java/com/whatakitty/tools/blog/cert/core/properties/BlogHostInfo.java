package com.whatakitty.tools.blog.cert.core.properties;

import lombok.Data;

/**
 * 博客主机信息
 *
 * @author WhatAKitty
 * @date 2019/03/21
 * @description
 **/
@Data
public class BlogHostInfo {

    private String ip;
    private SSH ssh;

    @Data
    public static class SSH {

        private String port;
        private String username;
        private String password;

    }

}
