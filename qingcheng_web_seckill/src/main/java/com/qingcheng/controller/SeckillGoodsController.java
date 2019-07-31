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
     * ���ݹ������ȡʱ��˵�
     * @return
     */
    @RequestMapping("/menus")
    public List <Date> dateMenus(){
        return DateUtil.getDateMenus ();
    }

    /**
     * ������ɱʱ��β�ѯ��Ʒ����
     * @param key 2019-5-7 16:00�����Ե���DateUtils������ת��2019050716��ʽ
     * @return
     */
    @RequestMapping("/list")
    public List<SeckillGoods> list(String key){
      return seckillGoodsService.list (DateUtil.formatStr (key));
    }

    /**
     * ������Ʒid��ѯ��Ʒ��ϸҳ
     * @param time
     * @param id
     * @return
     */
    @RequestMapping("/one")
    public SeckillGoods one(String time,Long id){
       return seckillGoodsService.one (time,id);
    }
}
