package com.qingcheng.seckill;

import com.qingcheng.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 获取指定时间对应的秒杀商品列表
     * @param key
     * @return
     */
    public List<SeckillGoods> list(String key);

    /**
     * 根据id查询商品详情页
     * @param time 时间区间
     * @param id 商品ID
     * @return
     */
    public SeckillGoods one(String time,Long id);
}
