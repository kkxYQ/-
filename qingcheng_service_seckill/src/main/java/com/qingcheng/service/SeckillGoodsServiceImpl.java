package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.seckill.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Service
@CrossOrigin
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据key 获取商品列表
     * @param key
     * @return
     */
    public List<SeckillGoods> list(String key) {
        return redisTemplate.boundHashOps ("SeckillGoods_"+key).values ();
    }

    /**
     * @param time 时间区间
     * @param id 商品ID
     * @return
     */
    public SeckillGoods one(String time, Long id) {
        SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps ("SeckillGoods_" + time).get (id);
        return goods;
    }
}
