package com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert;

import com.qiniu.common.Constants;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.qiniu.util.Json;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.model.Cert;
import java.util.HashMap;
import java.util.Map;

/**
 * 融合CDN证书管理器
 * 该类主要用于证书的上传、下载、列表获取等
 * 参考<a href="https://developer.qiniu.com/fusion/api/4248/certificate">融合CDN-证书相关</a>
 *
 * @author WhatAKitty
 * @date 2019/03/17
 * @description
 **/
public class CdnCertManager {

    private final Auth auth;
    private String server;
    private Client client;

    /**
     * CdnManager 使用七牛标准的管理鉴权方式
     *
     * @param auth - Auth 对象
     */
    public CdnCertManager(Auth auth) {
        this(auth, "http://fusion.qiniuapi.com");
    }

    public CdnCertManager(Auth auth, String server) {
        this.auth = auth;
        this.server = server;
        this.client = new Client();
    }

    public CdnCertManager(Auth auth, String server, Client client) {
        this.auth = auth;
        this.server = server;
        this.client = client;
    }

    /**
     * 获取证书列表
     *
     * @param marker 起始位置
     * @param limit  获取的数量
     * @return 证书列表
     * @throws QiniuException 限制的数量小于等于<code>0</code>的时候，抛出异常
     */
    @SuppressWarnings("Duplicates")
    public CdnCertResult.CertListResult list(String marker, int limit) throws QiniuException {
        //check params
        if (limit <= 0) {
            throw new QiniuException(new Exception("limit count should be greater than 0"));
        }

        StringBuilder urlBuilder = new StringBuilder(server);
        urlBuilder.append("/sslcert?");
        if (!StringUtils.isNullOrEmpty(marker)) {
            urlBuilder.append("marker=").append(marker).append("&");
        }
        urlBuilder.append("limit=").append(limit);
        String url = urlBuilder.toString();
        StringMap headers = auth.authorizationV2(url, "GET", null, null);
        Response response = client.get(url, headers);
        return response.jsonToObject(CdnCertResult.CertListResult.class);
    }

    /**
     * 获取证书信息
     *
     * @param certID 证书ID
     * @return 证书信息
     * @throws QiniuException 传入的证书ID为空的时候抛出异常
     */
    public CdnCertResult.CertResult cert(String certID) throws QiniuException {
        // check params
        if (StringUtils.isNullOrEmpty(certID)) {
            throw new QiniuException(new Exception("certID count should not be null"));
        }

        String url = server + "/sslcert/" + certID;
        StringMap headers = auth.authorizationV2(url, "GET", null, null);
        Response response = client.get(url, headers);
        return response.jsonToObject(CdnCertResult.CertResult.class);
    }

    /**
     * 上传证书
     *
     * @param cert 证书信息
     * @return 上传结果
     * @throws QiniuException 证书信息校验不通过，则会抛出异常
     */
    public CdnCertResult.UploadResult upload(Cert cert) throws QiniuException {
        // check params
        if (StringUtils.isNullOrEmpty(cert.name)) {
            throw new QiniuException(new Exception("name should not be null"));
        }
        if (StringUtils.isNullOrEmpty(cert.common_name)) {
            throw new QiniuException(new Exception("common name should not be null"));
        }
        if (StringUtils.isNullOrEmpty(cert.pri)) {
            throw new QiniuException(new Exception("pri should not be null"));
        }
        if (StringUtils.isNullOrEmpty(cert.ca)) {
            throw new QiniuException(new Exception("ca should not be null"));
        }

        Map<String, String> req = new HashMap<>(8);
        req.put("name", cert.name);
        req.put("common_name", cert.common_name);
        req.put("pri", cert.pri);
        req.put("ca", cert.ca);

        byte[] body = Json.encode(req).getBytes(Constants.UTF_8);
        String url = server + "/sslcert";
        StringMap headers = auth.authorizationV2(url, "POST", body, Client.JsonMime);
        Response response = client.post(url, body, headers, Client.JsonMime);
        return response.jsonToObject(CdnCertResult.UploadResult.class);
    }

}
