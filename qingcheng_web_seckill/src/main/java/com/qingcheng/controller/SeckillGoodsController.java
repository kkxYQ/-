package com.qingcheng.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.seckill.SeckillGoodsService;
import com.qingcheng.utils.DateUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/seckill/goods")
public class SeckillGoodsController {

     @Reference
     private SeckillGoodsService seckillGoodsService;

    /**
     * 根据工具类获取时间菜单
     * @return
     */
    @RequestMapping("/menus")
    public List <Date> dateMenus(){
        return DateUtil.getDateMenus ();
    }

    /**
     * 根据秒杀时间段查询商品集合
     * @param key 2019-5-7 16:00，可以调用DateUtils，将它转成2019050716格式
     * @return
     */
    @RequestMapping("/list")
    public List<SeckillGoods> list(String key){
      return seckillGoodsService.list (DateUtil.formatStr (key));
    }

    /**
     * 根据商品id查询商品详细页
     * @param time
     * @param id
     * @return
     */
    @RequestMapping("/one")
    public SeckillGoods one(String time,Long id){
       return seckillGoodsService.one (time,id);
    }
}
