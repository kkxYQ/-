package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.StockBackMapper;
import com.qingcheng.pojo.goods.StockBack;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = StockBackService.class)
public class StockBackServiceImpl implements StockBackService{
    @Autowired
    private StockBackMapper stockBackMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Transactional
    public void addList(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            StockBack stockBack = new StockBack ();
            stockBack.setOrderId (orderItem.getOrderId ());
            stockBack.setSkuId (orderItem.getSkuId ());
            stockBack.setStatus("0");
            stockBack.setNum (orderItem.getNum ());
            stockBack.setCreateTime(new Date());
            stockBackMapper.insert (stockBack);
        }
    }

    /**
     * ִ�п��ع�
     */
    @Transactional
    public void doBack() {
        System.out.println ("huigunkaishi");
        //��ѯ���ع�����״̬Ϊ0�ļ�¼
        StockBack stockBack = new StockBack ();
        stockBack.setStatus ("0");
        List <StockBack> stockBackList = stockBackMapper.select (stockBack);
        for (StockBack back : stockBackList) {
            //��ӿ��
            skuMapper.add (back.getSkuId (),-back.getNum ());
            //��������
            skuMapper.reduce (back.getSkuId (),-back.getNum ());
            back.setStatus ("1");//�Ѵ���
            back.setBackTime (new Date ());
            stockBackMapper.updateByPrimaryKey (back);
        }
        System.out.println ("kucunhuigun renwujiesu");

    }
}
