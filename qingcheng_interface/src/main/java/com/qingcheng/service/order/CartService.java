package com.qingcheng.service.order;
import java.util.List;
import	java.util.Map;


/**
 * ���ﳵ����
 */
public interface CartService {
    /**
     * ��redis����ȡĳ�û��Ĺ��ﳵ
     * */
    public List<Map< String,Object>> findCartList(String username);

    /**
     * �����Ʒ�����ﳵ
     * @param username �û���
     * @param skuId ��Ʒ��Id
     * @param num ��Ʒ������
     */
    public void addItem(String username,String skuId,Integer num);

    /**
     * ���ﳵ�����û���ѡ״̬
     * @param username
     * @param skuId
     * @param checked
     */
    public Boolean updateChecked(String username, String skuId,boolean checked);

    /**
     * ɾ��ѡ�е�
     * @param username
     */
    public void deleteCheckedCart(String username);

    /**
     * ���㵱ǰѡ�е��Żݽ��
     * @param username
     * @return
     */
    public int preferential(String username);

    /**
     * �����û�����ȡ���ﳵ
     * @param username
     * @return
     */
    public List<Map<String,Object>> findNewOrderItemList(String username);
}
