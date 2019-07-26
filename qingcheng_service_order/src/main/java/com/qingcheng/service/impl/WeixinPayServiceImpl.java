
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
            //1.��װ�������
            Map<String,String> map=new HashMap();
            map.put ("appid",config.getAppID());//�����˺�ID
            map.put ("mch_id",config.getMchID ());//�̻���
            map.put ("nonce_str", WXPayUtil.generateNonceStr ());//��������ַ���
            map.put ("body","΢΢һЦ�����");//��Ʒ����
            map.put ("out_trade_no",orderId);//������
            map.put ("total_fee",money+"");//��Ʒ���(��)
            map.put ("spbill_create_ip","127.0.0.1");//�ն�ip
            map.put ("notify_url",notifyUrl);//֧���ɹ��첽�Զ����øõ�ַ����֪ͨ
            map.put ("trade_type","NATIVE");//��������ɨ��֧��

            String xmlParam = WXPayUtil.generateSignedXml (map, config.getKey ());//����һ����ǩ����xml
            System.out.println ("�������"+xmlParam);

            //2.��������
            WXPayRequest wxPayRequest = new WXPayRequest (config);
            //urlSuffix:����url�ĺ�׺,uuID:���Բ��ô�ûʲôʵ������,Date:��ǩ�����ַ���xml,aotoResport:�Ƿ��Զ�����
            String xmlRequest = wxPayRequest.requestWithCert ("/pay/unifiedorder", null, xmlParam, false);//��Ӧ������ַ���
            System.out.println ("��Ӧ�Ľ��"+xmlRequest);

            //3.�������صĽ��
            Map <String, String> mapResult = WXPayUtil.xmlToMap (xmlRequest);
            String code_url = mapResult.get ("code_url");
            System.out.println (code_url);
            //4.��װ���
            Map paramMap=new HashMap ();
            paramMap.put ("code_url",code_url);//֧����ַ
            paramMap.put ("total_fee",money+"");//֧�����
            paramMap.put ("out_trade_no",orderId);//������

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
            //1.��xml���н�����map
            Map <String, String> xmlToMap = WXPayUtil.xmlToMap (xml);
            //2.��֤ǩ��
            boolean sign = WXPayUtil.isSignatureValid (xmlToMap, config.getKey ());
            System.out.println ("��֤ǩ���Ƿ���ȷ��"+sign);
            System.out.println ("�����ţ�"+xmlToMap.get ("out_trade_no"));
            System.out.println ("���ʽ��"+xmlToMap.get ("result_code"));
            //3.�޸Ķ���״̬
            if (sign){
                if("SUCCESS".equals (xmlToMap.get ("result_code"))){
                    orderService.updatePayStatus (xmlToMap.get ("out_trade_no"),xmlToMap.get ("transaction_id"));
                    //���Ͷ����Ÿ�mq
                    rabbitTemplate.convertAndSend ("paynotify","",xmlToMap.get ("out_trade_no"));

                }else{
                    //��¼��־
                }
            }else{
                //��¼��־
            }


        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
