package com.qingcheng;
import	java.util.Map;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SmsMessageConsumer implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Value ("${smsCode}")
    private String smsCode;

    @Value ("${param}")
    private String param;

    public void onMessage(Message message) {

        String jsonString = new String (message.getBody ());
        Map<String, String> map = JSON.parseObject (jsonString, Map.class);

        String phone =  map.get ("phone");//�ֻ���
        String code = map.get("code");//��֤��
        System.out.println ("�ֻ�����"+phone+":"+"��֤��"+code);
        //���ð����ƶ��ŷ���
        smsUtil.sendSms (phone, smsCode, param.replace ("[value]", code));

    }
}
