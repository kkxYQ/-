package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.GroupOrder;
import com.qingcheng.pojo.order.Order;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String,Object> searchMap);


    public PageResult<Order> findPage(Map<String,Object> searchMap,int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);



    public void delete(String id);

    public GroupOrder getOrders(String id);
    /**
     * 批量发货
     * @param orders
     */
    public void batchSend(List<Order> orders);
    /**
     * 合并订单 传入两个订单id
     * @param idone
     * @param idtwo
     */
    public void merge(String idone, String idtwo);


}
