package com.whatakitty.tools.blog.cert.infa.ssh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SSH配置
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@Configuration
public class SSHConfiguration {

    @Bean
    public SSHClientFactory sshClientFactory() {
        return new SSHClientFactory();
    }

}
