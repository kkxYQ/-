package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;
//点击查看显示订单订单详情
public class GroupOrder implements Serializable {

    private Order order;//订单对象

    private List<OrderItem> orderItems;//订单详细

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List <OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List <OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
