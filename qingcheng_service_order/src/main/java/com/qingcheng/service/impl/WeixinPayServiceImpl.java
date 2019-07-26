
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.Config;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WeixinPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {

    @Autowired
    private Config config;

    @Override
    public Map createNative(String orderId, Integer money, String notifyUrl){
        try {
            //1.封装请求参数
            Map<String,String> map=new HashMap();
            map.put ("appid",config.getAppID());//公众账号ID
            map.put ("mch_id",config.getMchID ());//商户号
            map.put ("nonce_str", WXPayUtil.generateNonceStr ());//生成随机字符串
            map.put ("body","微微一笑很倾城");//商品描述
            map.put ("out_trade_no",orderId);//订单号
            map.put ("total_fee",money+"");//商品金额(分)
            map.put ("spbill_create_ip","127.0.0.1");//终端ip
            map.put ("notify_url",notifyUrl);//支付成功异步自动调用该地址进行通知
            map.put ("trade_type","NATIVE");//交易类型扫码支付

            String xmlParam = WXPayUtil.generateSignedXml (map, config.getKey ());//生成一个带签名的xml
            System.out.println ("请求参数"+xmlParam);

            //2.发送请求
            WXPayRequest wxPayRequest = new WXPayRequest (config);
            //urlSuffix:域名url的后缀,uuID:可以不用传没什么实际意义,Date:带签名的字符串xml,aotoResport:是否自动报告
            String xmlRequest = wxPayRequest.requestWithCert ("/pay/unifiedorder", null, xmlParam, false);//响应结果的字符串
            System.out.println ("响应的结果"+xmlRequest);

            //3.解析返回的结果
            Map <String, String> mapResult = WXPayUtil.xmlToMap (xmlRequest);
            String code_url = mapResult.get ("code_url");
            System.out.println (code_url);
            //4.封装结果
            Map paramMap=new HashMap ();
            paramMap.put ("code_url",code_url);//支付地址
            paramMap.put ("total_fee",money+"");//支付金额
            paramMap.put ("out_trade_no",orderId);//订单号

            return paramMap;
        } catch (Exception e) {
            e.printStackTrace ();
            return new HashMap ();
        }

    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void notifyLogic(String xml) {

        try {
            //1.对xml进行解析成map
            Map <String, String> xmlToMap = WXPayUtil.xmlToMap (xml);
            //2.验证签名
            boolean sign = WXPayUtil.isSignatureValid (xmlToMap, config.getKey ());
            System.out.println ("验证签名是否正确："+sign);
            System.out.println ("订单号："+xmlToMap.get ("out_trade_no"));
            System.out.println ("访问结果"+xmlToMap.get ("result_code"));
            //3.修改订单状态
            if (sign){
                if("SUCCESS".equals (xmlToMap.get ("result_code"))){
                    orderService.updatePayStatus (xmlToMap.get ("out_trade_no"),xmlToMap.get ("transaction_id"));
                    //发送订单号给mq
                    rabbitTemplate.convertAndSend ("paynotify","",xmlToMap.get ("out_trade_no"));

                }else{
                    //记录日志
                }
            }else{
                //记录日志
            }


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
