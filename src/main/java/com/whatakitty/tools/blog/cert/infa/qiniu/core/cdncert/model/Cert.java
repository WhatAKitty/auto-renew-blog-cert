package com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.model;

/**
 * 证书结构
 * 参考链接：<a href="https://developer.qiniu.com/fusion/api/4249/product-features#9">证书配置</a>
 */
public class Cert {
    public String certid;
    public String name;
    public String common_name;
    public String pri;
    public String ca;
}