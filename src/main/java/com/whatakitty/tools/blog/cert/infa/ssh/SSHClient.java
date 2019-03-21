package com.whatakitty.tools.blog.cert.infa.ssh;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;

/**
 * SSH客户端
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@Slf4j
@RequiredArgsConstructor
public final class SSHClient {

    @Getter
    private final String host;
    @Getter
    private final Integer port;
    @Getter
    private final String username;
    private final String password;

    private net.schmizz.sshj.SSHClient sshClient;

    public void connect() {
        try {
            getSshClient().connect(host);
        } catch (IOException e) {
            throw new RuntimeException("链接服务器失败", e);
        }

        try {
            getSshClient().authPassword(username, password);
        } catch (UserAuthException e) {
            throw new RuntimeException("登录服务器出错，错误的用户名密码", e);
        } catch (TransportException e) {
            throw new RuntimeException("错误的协议传输", e);
        }
    }

    public void connectWithSession(String command, Callback callback) {
        try (
            Session session = getSshClient().startSession()
        ) {
            exec(session, command, callback);
        } catch (ConnectionException e) {
            throw new RuntimeException("连接失败", e);
        } catch (TransportException e) {
            throw new RuntimeException("错误的协议传输", e);
        }
    }

    public void lifecycle(Map<String, Callback> callbackMap) {
        connect();
        callbackMap.forEach(this::connectWithSession);
        disconnect();
    }

    public void exec(Session session, String command) {
        exec(session, command, result -> {
        });
    }

    public void exec(Session session, String command, Callback callback) {
        try {
            Session.Command exec = session.exec(command);
            String result = IOUtils.readFully(exec.getInputStream()).toString();
            callback.result(result);
            exec.join(10, TimeUnit.MINUTES);
            int status = exec.getExitStatus();
            if (status != 0) {
                log.error("执行命令 {} 错误 {}, 错误码 {}", command, result, status);
            } else {
                log.info("成功执行命令 {}", command);
                log.debug("附带返回信息 {}", result);
            }
        } catch (IOException e) {
            throw new RuntimeException("输出结果失败", e);
        }
    }

    public void disconnect() {
        try {
            getSshClient().disconnect();
        } catch (IOException e) {
            throw new RuntimeException("关闭ssh客户端失败", e);
        }
    }

    @FunctionalInterface
    public interface Callback {
        void result(String result);
    }

    private net.schmizz.sshj.SSHClient getSshClient() {
        if (sshClient == null) {
            synchronized (this) {
                sshClient = new net.schmizz.sshj.SSHClient();
                try {
                    sshClient.loadKnownHosts();
                    sshClient.addHostKeyVerifier(new PromiscuousVerifier());
                } catch (IOException e) {
                    throw new RuntimeException("无法读取本地.hosts文件", e);
                }
            }
        }

        return sshClient;
    }

}
