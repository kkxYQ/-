package com.qingcheng.service.pay;

import java.util.Map;

/**
 * ΢��֧���ӿ�
 */
public interface WeixinPayService {
    /**
     * ����΢��֧����ά��
     * @param orderId ������
     * @param money ���(��)
     * @param notifyUrl �ص���ַ
     * @return
     */
    public Map createNative(String orderId,Integer money,String notifyUrl);
}
