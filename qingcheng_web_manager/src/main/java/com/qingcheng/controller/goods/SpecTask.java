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
     * �賿ִ�й�񻺴�Ԥ��
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void saveToRedis(){
        System.out.println("��񻺴�Ԥ�ȿ�ʼ");
        specService.saveToRedis();
        System.out.println("��񻺴�Ԥ�Ƚ���");
    }
}
