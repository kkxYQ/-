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
     *��Ӷ���
     * @param id id
     * @param time ��ɱ��Ʒʱ��
     * @param username �û���¼��
     * @return
     */
    public Boolean add(Long id, String time, String username) {
        //��redis��ȡ����Ʒ
        SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps ("SeckillGoods_" + time).get (id);

        //���û�п�棬��ֱ���׳��쳣
        if(goods==null || goods.getStockCount()<=0){
            throw new RuntimeException("������!");
        }
        //����п�棬�򴴽���ɱ��Ʒ����
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(goods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setSellerId(goods.getSellerId());
        seckillOrder.setCreateTime(new Date ());
        seckillOrder.setStatus("0");

        //����ɱ��������redis��
        redisTemplate.boundHashOps ("SeckillOrder").put (username,seckillOrder);

        //���ٿ��
        goods.setStockCount (goods.getStockCount ()-1);

        //�жϵ�ǰ��Ʒ�Ƿ��п��
        if(goods.getStockCount ()<=0){
            //������Ʒ����ͬ����mysql��
            seckillGoodsMapper.updateByPrimaryKeySelective (goods);
            //�����Redis�е�����
            redisTemplate.boundHashOps ("SeckillGoods_"+time).delete (id);
        }else {
            //����п��������ͬ����redis��
            redisTemplate.boundHashOps ("SeckillGoods_"+time).put (id,goods);
        }
        return true;
    }
}
