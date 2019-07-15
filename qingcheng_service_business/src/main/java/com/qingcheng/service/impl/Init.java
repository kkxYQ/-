package com.qingcheng.service.impl;

import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {
    //实现InitializingBean接口的类会在启动时自动调用

    @Autowired
    private AdService adService;

    public void afterPropertiesSet() throws Exception {
        System.out.println ("--缓存预热--");
        adService.saveAllAdToRedis ();

    }
}
