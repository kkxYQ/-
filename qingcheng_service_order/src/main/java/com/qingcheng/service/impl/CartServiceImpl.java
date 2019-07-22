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
     * ��redis����ȡ���ﳵ
     * @param username
     * @return
     */
    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println ("��redis����ȡ���ﳵ"+username);
        List<Map<String, Object>> carlist = (List <Map <String, Object>>) redisTemplate.boundHashOps (CacheKey.CART_LIST).get (username);
        if (carlist==null){
            carlist=new ArrayList <> ();
        }
        return carlist;
    }

    /**
     * �����Ʒ�����ﳵ
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addItem(String username, String skuId, Integer num) {
        //ʵ��˼·��?�������ﳵ��������ﳵ�д��ڸ���Ʒ���ۼ��������������������ӹ��ﳵ��
        //��ȡ���ﳵ
        List <Map <String, Object>> cartList = findCartList (username);

        boolean flag=false;//��Ʒ�Ƿ��ڹ��ﳵ�д���

        for (Map <String, Object> map : cartList) {


        }


    }
}
