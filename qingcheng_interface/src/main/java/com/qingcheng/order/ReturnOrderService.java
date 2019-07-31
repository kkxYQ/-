package com.qingcheng.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.ReturnOrder;

import java.util.*;

/**
 * returnOrder业务逻辑层
 */
public interface ReturnOrderService {


    public List<ReturnOrder> findAll();


    public PageResult<ReturnOrder> findPage(int page, int size);


    public List<ReturnOrder> findList(Map<String,Object> searchMap);


    public PageResult<ReturnOrder> findPage(Map<String,Object> searchMap,int page, int size);


    public ReturnOrder findById(Long id);

    public void add(ReturnOrder returnOrder);


    public void update(ReturnOrder returnOrder);


    public void delete(Long id);

    /**
     * 统一退款
     * @param id 商品id
     * @param money 退款金额
     * @param adminId 管理员的Id
     */
    public void agreeRefund(String id,Integer money,Integer adminId );

    /**
     * 驳回退款
     * @param id  商品id
     * @param remark 驳回消息
     * @param adminId 操作人id
     */
    public void rejectRefund(String id,String remark,Integer adminId );

}
