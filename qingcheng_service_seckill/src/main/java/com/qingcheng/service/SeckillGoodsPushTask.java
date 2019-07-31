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
        //��ȡʱ��μ���
        List <Date> dateMenus = DateUtil.getDateMenus ();
        for (Date startTime : dateMenus) {
            //namespace = SeckillGoods_20195712
            String extName = DateUtil.date2Str (startTime);
            //����ʱ������ݲ�ѯ��Ӧ����Ʒ��ɱ����
            Example example = new Example (SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria ();
            //1.��Ʒ����ͨ�����
            criteria.andEqualTo ("status","1");
            //2.������0
            criteria.andGreaterThan ("stockCount",0);
            //3.��ʼʱ��С�ڵ��ڻ��ʼʱ��
            criteria.andGreaterThanOrEqualTo ("startTime",startTime);
            //4.�����ʱ��С�ڿ�ʼʱ��+2Сʱ
            criteria.andLessThan ("endTime",DateUtil.addDateHour (startTime,2));
            //5.�ų�֮���Ѿ����ص�redis�����е�����
            Set keys = redisTemplate.boundHashOps ("SeckillGoods_" + extName).keys ();
            if (keys!=null&&keys.size ()>0){
                criteria.andNotIn ("id",keys);
            }
            //��ѯ����
            List <SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample (example);
            System.out.println (sdf.format(startTime)+"����ѯ����Ʒ:"+seckillGoods.size ());

            //�����ݱ��浽������
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps ("SeckillGoods_" + extName).put (seckillGood.getId (),seckillGood);
            }
        }


    }
}
