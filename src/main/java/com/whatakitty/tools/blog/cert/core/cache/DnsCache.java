package com.whatakitty.tools.blog.cert.core.cache;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.exceptions.ClientException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * DNS缓存
 *
 * @author WhatAKitty
 * @date 2019/03/20
 * @description
 **/
@RequiredArgsConstructor
@Component
public class DnsCache implements ICache {

    private final IAcsClient iAcsClient;

    private Map<String, List<DescribeDomainRecordsResponse.Record>> cache;

    @PostConstruct
    @Override
    public void init() throws Exception {
        cache = new ConcurrentHashMap<>(16);
    }

    @Override
    public void refresh() throws Exception {
        cache.clear();
    }

    /**
     * 获取域名的解析列表
     *
     * @param domainName 域名
     * @return 解析列表
     */
    public List<DescribeDomainRecordsResponse.Record> getRecords(String domainName) {
        return cache.computeIfAbsent(domainName, this::_getRecords);
    }

    /**
     * 获取具体的某个解析
     *
     * @param domainName 域名
     * @param rr         前缀
     * @return 解析信息
     */
    public Optional<DescribeDomainRecordsResponse.Record> getRecord(String domainName, String rr) {
        return getRecords(domainName).parallelStream().filter(item -> rr.equals(item.getRR())).findFirst();
    }

    private List<DescribeDomainRecordsResponse.Record> _getRecords(String domainName) {
        final DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setDomainName(domainName);
        final DescribeDomainRecordsResponse describeDomainRecordsResponse;
        try {
            describeDomainRecordsResponse = iAcsClient.getAcsResponse(describeDomainRecordsRequest);
        } catch (ClientException e) {
            throw new RuntimeException("get records failed");
        }
        return describeDomainRecordsResponse.getDomainRecords();
    }

}
