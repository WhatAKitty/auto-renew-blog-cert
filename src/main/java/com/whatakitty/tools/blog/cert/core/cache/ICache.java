package com.whatakitty.tools.blog.cert.core.cache;

/**
 * 缓存接口
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
public interface ICache {

    /**
     * 缓存初始化
     */
    void init() throws Exception;

    /**
     * 缓存刷新
     */
    void refresh() throws Exception;

}
