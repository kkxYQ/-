package com.qingcheng.order;


import java.util.List;
import java.util.Map;

/**
 * ����ͳ��
 */
public interface TransactionSheetService {

    /**
     * ��ʱ����
     */
    public void createData();

    /**
     * ��ѯ����ͳ�Ʊ� ����ͼ
     */
    public List<Map> countTracn(String date1,String date2);
    /**
     * ��ѯ����ͳ�Ʊ� ©��ͼ
     */
    public List<Map> countFunnel(String date1);

}
