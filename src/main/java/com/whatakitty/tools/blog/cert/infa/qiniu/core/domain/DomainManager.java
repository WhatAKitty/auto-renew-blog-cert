package com.whatakitty.tools.blog.cert.infa.qiniu.core.domain;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 域名操作
 *
 * @author WhatAKitty
 * @date 2019/03/19
 * @description
 **/
public class DomainManager {

    private final Auth auth;
    private String server;
    private Client client;

    /**
     * CdnManager 使用七牛标准的管理鉴权方式
     *
     * @param auth - Auth 对象
     */
    public DomainManager(Auth auth) {
        this(auth, "http://fusion.qiniuapi.com");
    }

    public DomainManager(Auth auth, String server) {
        this.auth = auth;
        this.server = server;
        this.client = new Client();
    }

    public DomainManager(Auth auth, String server, Client client) {
        this.auth = auth;
        this.server = server;
        this.client = client;
    }

    public void changeCert(String domainName, String certID, boolean forceHttps) throws QiniuException, UnsupportedEncodingException {
        // check params
        if (StringUtils.isNullOrEmpty(domainName)) {
            throw new QiniuException(new Exception("domainName should not be null"));
        }
        if (StringUtils.isNullOrEmpty(certID)) {
            throw new QiniuException(new Exception("certID should not be null"));
        }

        // 构造post请求body
        Gson gson = new Gson();
        Map<String, Object> domainCofig = new HashMap<>(4);
        domainCofig.put("certid", certID);
        domainCofig.put("forceHttps", forceHttps);
        String paraR = gson.toJson(domainCofig);
        byte[] bodyByte = paraR.getBytes();

        // 获取签名
        String url = "http://api.qiniu.com/domain/static.xuqiang.me/httpsconf";

        String accessToken = (String) auth.authorization(url, bodyByte, "application/json")
            .get("Authorization");

        Client client = new Client();
        StringMap headers = new StringMap();
        headers.put("Authorization", accessToken);
        try {
            final Request.Builder builder = new Request.Builder();
            builder.method("PUT", RequestBody.create(MediaType.parse(Client.JsonMime), bodyByte));
            builder.url(url);
            Response resp = client.send(builder, headers);
        } catch (Exception e) {
            throw new QiniuException(new Exception("failed to execute change cert"));
        }
    }

    @SuppressWarnings("Duplicates")
    public DomainResult.ListResult list(String marker, Integer limit) throws QiniuException {
        //check params
        if (limit <= 0) {
            throw new QiniuException(new Exception("limit count should be greater than 0"));
        }

        StringBuilder urlBuilder = new StringBuilder(server);
        urlBuilder.append("/domain?");
        if (!StringUtils.isNullOrEmpty(marker)) {
            urlBuilder.append("marker=").append(marker).append("&");
        }
        urlBuilder.append("limit=").append(limit);
        String url = urlBuilder.toString();
        StringMap headers = auth.authorizationV2(url, "GET", null, null);
        Response response = client.get(url, headers);
        return response.jsonToObject(DomainResult.ListResult.class);
    }

}
