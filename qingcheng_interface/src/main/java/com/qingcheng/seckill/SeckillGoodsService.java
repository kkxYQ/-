package com.qingcheng.seckill;

import com.qingcheng.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * ��ȡָ��ʱ���Ӧ����ɱ��Ʒ�б�
     * @param key
     * @return
     */
    public List<SeckillGoods> list(String key);

    /**
     * ����id��ѯ��Ʒ����ҳ
     * @param time ʱ������
     * @param id ��ƷID
     * @return
     */
    public SeckillGoods one(String time,Long id);
}
