package com.qingcheng.seckill;

public interface SeckillOrderService {

    /**
     * 添加秒杀订单
     * @param id id
     * @param time 秒杀商品时间
     * @param username 用户登录名
     * @return
     */
    Boolean add(Long id,String time,String username);
}
