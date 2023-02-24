package com.whatakitty.tools.blog.cert.core;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.domain.model.v20180129.QueryDomainListResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.ImmutableMap;
import com.qiniu.common.QiniuException;
import com.whatakitty.tools.blog.cert.core.cache.DnsCache;
import com.whatakitty.tools.blog.cert.core.cache.DomainCache;
import com.whatakitty.tools.blog.cert.core.properties.BlogHostInfo;
import com.whatakitty.tools.blog.cert.core.properties.BlogInfo;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.CdnCertManager;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.CdnCertResult;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.model.Cert;
import com.whatakitty.tools.blog.cert.infa.qiniu.core.domain.DomainManager;
import com.whatakitty.tools.blog.cert.infa.ssh.SSHClient;
import com.whatakitty.tools.blog.cert.infa.ssh.SSHClientFactory;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 自动刷新
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public final class AutoRefresh {

    private final DomainCache domainCache;
    private final DnsCache dnsCache;
    private final IAcsClient iAcsClient;
    private final SSHClientFactory sshClientFactory;
    private final CdnCertManager cdnCertManager;
    private final DomainManager domainManager;

    private final BlogInfo blogInfo;

    /**
     * * 更换阿里云域名{}为A类型，指向{}
     * * 登录服务器，{}执行{}命令
     * * 更换阿里云域名回CNAME到{}
     * * 上传证书到七牛云证书管理，并切换{}的证书为新的证书
     */
    public void execute() {
        log.info("开始为 {} 站点续签证书", blogInfo.getTitle());
        // 更换域名解析
        log.info("更换阿里云域名{}为A类型，指向{}", blogInfo.getStaticDomain(), blogInfo.getHost().getIp());
        Optional<QueryDomainListResponse.Domain> optionalDomain = domainCache.getDomain(blogInfo.getDomain());
        final Optional<String> recordId = optionalDomain
            .map(domain -> dnsCache.getRecord(domain.getDomainName(), blogInfo.getResRr()))
            .map(optionalRecord -> {
                if (!optionalRecord.isPresent()) {
                    return null;
                }
                DescribeDomainRecordsResponse.Record record = optionalRecord.get();
                if (blogInfo.getResRr().equals(record.getRR())
                    && "A".equals(record.getType())
                    && blogInfo.getHost().getIp().equals(record.getValue())) {
                    return record.getRecordId();
                }

                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                updateDomainRecordRequest.setRecordId(record.getRecordId());
                updateDomainRecordRequest.setRR(blogInfo.getResRr());
                updateDomainRecordRequest.setType("A");
                updateDomainRecordRequest.setValue(blogInfo.getHost().getIp());
                try {
                    UpdateDomainRecordResponse response = iAcsClient.getAcsResponse(updateDomainRecordRequest);
                    return response.getRecordId();
                } catch (ClientException e) {
                    return null;
                }
            });
        if (!recordId.isPresent()) {
            throw new RuntimeException("更换失败");
        }
        log.info("完成更换");

        // SSH服务器连接
        final BlogHostInfo.SSH ssh = blogInfo.getHost().getSsh();
        log.info("登录服务器，{}执行续签命令", ssh.getUsername());
        final Map<String, String> certs = new HashMap<>(4);
        final SSHClient sshClient = sshClientFactory.createClient(
            blogInfo.getHost().getIp(),
            Integer.valueOf(blogInfo.getHost().getSsh().getPort()),
            ssh.getUsername(),
            ssh.getPassword()
        );
        // @formatter:off
        sshClient.lifecycle(ImmutableMap.of(
            blogInfo.getBatchCmds().get(0), result -> {},
            blogInfo.getBatchCmds().get(1), result -> certs.put("chained", result),
            blogInfo.getBatchCmds().get(2), result -> certs.put("key", result)
        ));
        // @formatter:off
        log.info("证书签名完成");

        // 上传证书
        log.info("上传证书到七牛云");
        Cert cert = new Cert();
        cert.ca = certs.get("chained");
        cert.pri = certs.get("key");
        cert.common_name = "blog_" + System.currentTimeMillis();
        cert.name = cert.common_name;
        final String certId;
        try {
            CdnCertResult.UploadResult uploadResult = cdnCertManager.upload(cert);
            certId = uploadResult.certID;
        } catch (QiniuException e) {
            throw new RuntimeException("上传证书失败", e);
        }
        log.info("上传完毕");

        // 换回阿里云域名
        log.info("更换阿里云域名回CNAME到{}", blogInfo.getCdn().getDomain());
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
        updateDomainRecordRequest.setRecordId(recordId.get());
        updateDomainRecordRequest.setRR(blogInfo.getResRr());
        updateDomainRecordRequest.setType("CNAME");
        updateDomainRecordRequest.setValue(blogInfo.getCdn().getDomain());
        try {
            iAcsClient.getAcsResponse(updateDomainRecordRequest);
        } catch (ClientException e) {
            throw new RuntimeException("回撤域名失败");
        }
        log.info("更换完毕");

        // 更换证书
        log.info("切换{}的证书为新的证书", blogInfo.getStaticDomain());
        try {
            domainManager.changeCert(blogInfo.getStaticDomain(), certId, true);
        } catch (QiniuException | UnsupportedEncodingException e) {
            throw new RuntimeException("更换证书失败", e);
        }
        log.info("切换完毕");

        log.info("{} 站点完成续签", blogInfo.getTitle());

    }

}
