package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.pojo.seckill.SeckillOrder;
import com.qingcheng.seckill.SeckillOrderService;
import com.qingcheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;

    /**
     *添加订单
     * @param id id
     * @param time 秒杀商品时间
     * @param username 用户登录名
     * @return
     */
    public Boolean add(Long id, String time, String username) {
        //从redis中取出商品
        SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps ("SeckillGoods_" + time).get (id);

        //如果没有库存，则直接抛出异常
        if(goods==null || goods.getStockCount()<=0){
            throw new RuntimeException("已售罄!");
        }
        //如果有库存，则创建秒杀商品订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(goods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(goods.getSellerId());
        seckillOrder.setCreateTime(new Date ());
        seckillOrder.setStatus("0");

        //将秒杀订单存入redis中
        redisTemplate.boundHashOps ("SeckillOrder").put (username,seckillOrder);

        //减少库存
        goods.setStockCount (goods.getStockCount ()-1);

        //判断当前商品是否还有库存
        if(goods.getStockCount ()<=0){
            //并将商品数据同步到mysql中
            seckillGoodsMapper.updateByPrimaryKeySelective (goods);
            //并清空Redis中的数据
            redisTemplate.boundHashOps ("SeckillGoods_"+time).delete (id);
        }else {
            //如果有库存则将数据同步到redis中
            redisTemplate.boundHashOps ("SeckillGoods_"+time).put (id,goods);
        }
        return true;
    }
}
