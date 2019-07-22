package com.qingcheng.service.impl;

import com.qingcheng.service.order.CartService;
import com.qingcheng.utils.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从redis中提取购物车
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println ("从redis中提取购物车"+username);
        List<Map<String, Object>> carlist = (List <Map <String, Object>>) redisTemplate.boundHashOps (CacheKey.CART_LIST).get (username);
        if (carlist==null){
            carlist=new ArrayList <> ();
        }
        return carlist;
    }

    /**
     * 添加商品到购物车
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {
        //实现思路：?遍历购物车，如果购物车中存在该商品则累加数量，如果不存在则添加购物车项
        //获取购物车
        List <Map <String, Object>> cartList = findCartList (username);

        boolean flag=false;//商品是否在购物车中存在

        for (Map <String, Object> map : cartList) {


        }


    }
}
