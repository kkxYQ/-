package com.github.wxpay.sdk;

import java.io.InputStream;
//΢��������
public class Config extends WXPayConfig {
    @Override
    public String getAppID() {
        return "wx8397f8696b538317";
    }//�����˻�id

    @Override
    public String getMchID() {
        return "1473426802";
    }//�̻���

    @Override
    public String getKey() {
        return "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    }//��Կ

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
