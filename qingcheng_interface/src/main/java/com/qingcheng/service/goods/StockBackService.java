package com.qingcheng.service.goods;

import com.qingcheng.pojo.order.OrderItem;

import java.util.List;

public interface StockBackService {
    /**
     *���ɿ��ع���¼
     * @param orderItems
     */
    public void addList(List<OrderItem> orderItems);

    /**
     * ִ�п��ع�
     */
    public void doBack();
}
