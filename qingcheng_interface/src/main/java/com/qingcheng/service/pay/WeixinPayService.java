package com.qingcheng.service.pay;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {
    /**
     * 生成微信支付二维码
     * @param orderId 订单号
     * @param money 金额(分)
     * @param notifyUrl 回调地址
     * @return
     */
    public Map createNative(String orderId,Integer money,String notifyUrl);
}
