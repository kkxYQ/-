package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.goods.StockBackService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SkuTask {


    @Reference
    private StockBackService stockBackService;

    /**
     * ���һСʱִ�п��ع�
     */
    @Scheduled(cron = "0 * * * * ?")
    public void orderTimeOutLogic(){
        System.out.println ("ִ�п��ع�");
        stockBackService.doBack ();
        System.out.println ("����");
    }

}
