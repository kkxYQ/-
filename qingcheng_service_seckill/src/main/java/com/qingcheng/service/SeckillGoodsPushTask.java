package com.qingcheng.service;
import	java.text.SimpleDateFormat;

import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/10 * * * * ?")
    public void loadGoodsPushRedis(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println (sdf.format (new Date ()));
        //获取时间段集合
        List <Date> dateMenus = DateUtil.getDateMenus ();
        for (Date startTime : dateMenus) {
            //namespace = SeckillGoods_20195712
            String extName = DateUtil.date2Str (startTime);
            //根据时间段数据查询对应的商品秒杀数据
            Example example = new Example (SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria ();
            //1.商品必须通过审核
            criteria.andEqualTo ("status","1");
            //2.库存大于0
            criteria.andGreaterThan ("stockCount",0);
            //3.开始时间小于等于活动开始时间
            criteria.andGreaterThanOrEqualTo ("startTime",startTime);
            //4.活动结束时间小于开始时间+2小时
            criteria.andLessThan ("endTime",DateUtil.addDateHour (startTime,2));
            //5.排除之间已经加载到redis缓存中的数据
            Set keys = redisTemplate.boundHashOps ("SeckillGoods_" + extName).keys ();
            if (keys!=null&&keys.size ()>0){
                criteria.andNotIn ("id",keys);
            }
            //查询数据
            List <SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample (example);
            System.out.println (sdf.format(startTime)+"共查询到商品:"+seckillGoods.size ());

            //将数据保存到缓存中
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps ("SeckillGoods_" + extName).put (seckillGood.getId (),seckillGood);
            }
        }


    }
}
