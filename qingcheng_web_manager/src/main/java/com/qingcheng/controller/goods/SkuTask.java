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
     * 间隔一小时执行库存回滚
     */
    @Scheduled(cron = "0 * * * * ?")
    public void orderTimeOutLogic(){
        System.out.println ("执行库存回滚");
        stockBackService.doBack ();
        System.out.println ("结束");
    }

}
