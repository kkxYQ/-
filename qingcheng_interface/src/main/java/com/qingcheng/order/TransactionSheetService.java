package com.qingcheng.order;


import java.util.List;
import java.util.Map;

/**
 * 交易统计
 */
public interface TransactionSheetService {

    /**
     * 定时任务
     */
    public void createData();

    /**
     * 查询交易统计表 折线图
     */
    public List<Map> countTracn(String date1,String date2);
    /**
     * 查询交易统计表 漏斗图
     */
    public List<Map> countFunnel(String date1);

}
