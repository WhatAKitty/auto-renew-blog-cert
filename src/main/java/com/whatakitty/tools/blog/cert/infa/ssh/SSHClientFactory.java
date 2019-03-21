package com.whatakitty.tools.blog.cert.infa.ssh;

/**
 * SSH客户端创建工厂
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
public final class SSHClientFactory {

    private static final int DEFAULT_PORT = 22;

    public SSHClient createClient(String host, String username, String password) {
        return new SSHClient(host, DEFAULT_PORT, username, password);
    }

    public SSHClient createClient(String host, Integer port, String username, String password) {
        return new SSHClient(host, port, username, password);
    }

}
