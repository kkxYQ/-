package com.github.wxpay.sdk;

import java.io.InputStream;
//微信配置类
public class Config extends WXPayConfig {
    @Override
    public String getAppID() {
        return "wx8397f8696b538317";
    }//公众账户id

    @Override
    public String getMchID() {
        return "1473426802";
    }//商户号

    @Override
    public String getKey() {
        return "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    }//密钥

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain(){

            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig wxPayConfig) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }
}
