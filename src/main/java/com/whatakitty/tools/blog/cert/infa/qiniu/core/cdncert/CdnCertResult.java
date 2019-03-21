package com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert;

import com.whatakitty.tools.blog.cert.infa.qiniu.core.cdncert.model.Cert;
import java.util.List;

/**
 * 定义证书回复
 *
 * @author WhatAKitty
 * @date 2019/03/17
 * @description
 **/
public class CdnCertResult {

    public static class UploadResult {
        public String certID;
    }

    public static class CertResult {
        public Cert cert;
    }

    public static class CertListResult {
        public String marker;
        public List<Cert> certs;
    }

}
