package com.whatakitty.tools.blog.cert.infa.qiniu.core.domain;

import com.whatakitty.tools.blog.cert.infa.qiniu.core.domain.model.Domain;
import java.util.List;

/**
 * 域名操作结果
 *
 * @author WhatAKitty
 * @date 2019/03/19
 * @description
 **/
public class DomainResult {

    public static class ListResult {
        public String marker;
        public List<Domain> domains;
    }

}
