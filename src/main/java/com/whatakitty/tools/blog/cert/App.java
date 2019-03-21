package com.whatakitty.tools.blog.cert;

import com.whatakitty.tools.blog.cert.core.AutoRefresh;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口
 *
 * @author WhatAKitty
 * @date 2019/03/17
 * @description
 **/
@SpringBootApplication
@RequiredArgsConstructor
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final AutoRefresh autoRefresh;

    @Override
    public void run(String... args) {
        autoRefresh.execute();
    }

}
