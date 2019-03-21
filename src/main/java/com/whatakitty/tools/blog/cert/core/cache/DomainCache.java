package com.whatakitty.tools.blog.cert.core.cache;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.domain.model.v20180129.QueryDomainListRequest;
import com.aliyuncs.domain.model.v20180129.QueryDomainListResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 域名缓存
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@RequiredArgsConstructor
@Component
public final class DomainCache implements ICache {

    private final IAcsClient iAcsClient;

    @Getter
    private List<QueryDomainListResponse.Domain> domains;

    @PostConstruct
    @Override
    public void init() throws ClientException {
        domains = Lists.newArrayList(_getDomains());
    }

    /**
     * 重新刷新
     *
     * @throws ClientException 初始化异常
     */
    @Override
    public void refresh() throws ClientException {
        synchronized (this) {
            init();
        }
    }

    /**
     * 获取域名
     *
     * @param domainName 域名
     * @return 域名信息
     */
    public Optional<QueryDomainListResponse.Domain> getDomain(String domainName) {
        return domains.parallelStream()
            .filter(item -> domainName.equals(item.getDomainName()))
            .findFirst();
    }

    private List<QueryDomainListResponse.Domain> _getDomains() throws ClientException {
        QueryDomainListRequest queryDomainListRequest = new QueryDomainListRequest();
        queryDomainListRequest.setPageNum(1);
        queryDomainListRequest.setPageSize(100);
        QueryDomainListResponse queryDomainListResponse = iAcsClient.getAcsResponse(queryDomainListRequest);
        return queryDomainListResponse.getData();
    }

}
