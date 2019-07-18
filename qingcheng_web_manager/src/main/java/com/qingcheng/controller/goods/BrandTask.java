package com.qingcheng.controller.goods;

import com.qingcheng.service.goods.BrandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
/**
 * 品牌任务类
 */
public class BrandTask {
    @com.alibaba.dubbo.config.annotation.Reference
    private BrandService brandService;
    /**
     * 凌晨执行品牌缓存
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveToRedis(){
        System.out.println ("品牌缓存预热开始");
        brandService.savrRedis ();
        System.out.println ("品牌缓存预热结束");
    }

}
