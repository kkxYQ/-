package com.qingcheng.service.order;
import java.util.List;
import	java.util.Map;


/**
 * ���ﳵ����
 */
public interface CartService {
    /**
     * ��redis����ȡ���ﳵ
     * */
    public List<Map< String,Object>> findCartList(String username);

    /**
     * �����Ʒ�����ﳵ
     * @param username
     * @param skuId
     * @param num
     */
    public void addItem(String username,String skuId,Integer num);
}
