package com.qingcheng.service.order;

import java.util.Map;

/**
 * ΢��֧���ӿ�
 */
public interface WeixinPayService {

    /**
     * ����΢��֧����ά��
     * @param orderId ����id
     * @param money ֧�����(��)
     * @param notifyUrl ���ɶ�ά��Ļص���ַ
     * @return
     */
    public Map createNative(String orderId,Integer money,String notifyUrl);

    /**
     * ΢��֧���ص��õ��������޸Ķ���״̬
     * @param xml
     */
    public void notifyLogic(String xml);



}
