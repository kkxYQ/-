package com.qingcheng.service.order;
import java.util.List;
import	java.util.Map;


/**
 * 购物车服务
 */
public interface CartService {
    /**
     * 从redis中提取购物车
     * */
    public List<Map< String,Object>> findCartList(String username);

    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    public void addItem(String username,String skuId,Integer num);
}
