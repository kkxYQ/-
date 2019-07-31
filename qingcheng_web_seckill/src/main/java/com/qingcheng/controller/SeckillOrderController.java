package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.seckill.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill/order")
public class SeckillOrderController {


    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 用户下单操作
     * 调用service层增加订单
     * 匿名访问：anonymousUser
     * @param id 商品id
     * @return
     */
    @GetMapping("/add")
    public Result add(String time,Long id){
        try {
            //获取用户名
            String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
            //用户没有登陆
            if (username.equalsIgnoreCase ("anonymousUser")){
                return new Result (403,"请先登陆");
            }
            //调用service增加订单
            Boolean add = seckillOrderService.add (id, time, username);
            if(add){
                return new Result (0,"下单成功");
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return new Result(1,e.getMessage());
        }
        return new Result (1,"秒杀下单失败");
    }
}
