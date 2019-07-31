package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.goods.SpecService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SpecTask {
    @Reference
    private SpecService specService;
    /**
     * 凌晨执行规格缓存预热
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveToRedis(){
        System.out.println("规格缓存预热开始");
        specService.saveToRedis();
        System.out.println("规格缓存预热结束");
    }
}
