package com.qingcheng.service.impl;

import com.qingcheng.service.goods.BrandService;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpecService;
import com.qingcheng.utils.CacheKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {
    //实现InitializingBean接口的类会在启动时自动调用

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpecService specService;

    public void afterPropertiesSet() throws Exception {
        System.out.println ("--缓存预热--");
        categoryService.saveCategoryTreeToRedis ();//加载商品分类导航缓存
        skuService.saveAllPriceToRedis ();//加载价格数据
        skuService.importToEs ();//加载数据到ES中
        //品牌列表缓存
        if(!redisTemplate.hasKey (CacheKey.BRAND_LIST)){
            brandService.savrRedis ();
        }
        //规格列表缓存
        if(!redisTemplate.hasKey (CacheKey.SPEC_LIST)){
            specService.saveToRedis ();
        }
    }
}
