package com.qingcheng.seckill;

public interface SeckillOrderService {

    /**
     * �����ɱ����
     * @param id id
     * @param time ��ɱ��Ʒʱ��
     * @param username �û���¼��
     * @return
     */
    Boolean add(Long id,String time,String username);
}
