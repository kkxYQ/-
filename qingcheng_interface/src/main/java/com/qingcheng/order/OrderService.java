package com.qingcheng.order;
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

    /**
     * 订单保存需要在订单页面显示订单号以及支付的金额
     * @param order
     * @return
     */
    public Map<String,Object> add(Order order);


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

    /**
     * 修改订单状态
     * @param orderId 订单Id
     * @param transactionId 微信平台返回的流水号
     */
    public void updatePayStatus(String orderId,String transactionId);


}
