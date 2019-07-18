package com.qingcheng.controller.goods;

import com.qingcheng.service.goods.BrandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
/**
 * Ʒ��������
 */
public class BrandTask {
    @com.alibaba.dubbo.config.annotation.Reference
    private BrandService brandService;
    /**
     * �賿ִ��Ʒ�ƻ���
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveToRedis(){
        System.out.println ("Ʒ�ƻ���Ԥ�ȿ�ʼ");
        brandService.savrRedis ();
        System.out.println ("Ʒ�ƻ���Ԥ�Ƚ���");
    }

}
