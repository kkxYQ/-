package com.qingcheng.service.goods;

import com.qingcheng.pojo.order.OrderItem;

import java.util.List;

public interface StockBackService {
    /**
     *生成库存回滚记录
     * @param orderItems
     */
    public void addList(List<OrderItem> orderItems);

    /**
     * 执行库存回滚
     */
    public void doBack();
}
