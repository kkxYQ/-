package com.qingcheng.service.order;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {

    /**
     * 生成微信支付二维码
     * @param orderId 订单id
     * @param money 支付金额(分)
     * @param notifyUrl 生成二维码的回调地址
     * @return
     */
    public Map createNative(String orderId,Integer money,String notifyUrl);

    /**
     * 微信支付回调得到订单号修改订单状态
     * @param xml
     */
    public void notifyLogic(String xml);



}
