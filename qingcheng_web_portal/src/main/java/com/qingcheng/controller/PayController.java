package com.qingcheng.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WeixinPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wxpay")
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;


    @GetMapping("/createNative")
    public Map createNative(String orderId){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        Order order = orderService.findById (orderId);
        if (order!=null){
            if ("0".equals (order.getPayStatus ())&&"0".equals (order.getOrderStatus ())&&username.equals (order.getUsername ())){
                return weixinPayService.createNative (orderId, order.getPayMoney (), "http://huyanhong.free.idcfengye.com/wxpay/notify.do");
            }else {
                return null;
            }
        }else {
            return null;
        }
    }


    /**
     * 回调 把二进制的信息转换成xml形式的字符串
     */
    @RequestMapping("/notify")
    public Map notifyLogic(HttpServletRequest request){
        System.out.println ("支付回调成功.....");
        InputStream inStream;
        try {
            inStream=request.getInputStream ();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=inStream.read (bytes))!=-1) {
                outputStream.write (bytes,0,len);
            }
            outputStream.close ();
            inStream.close ();
            String result=new String (outputStream.toByteArray (),"utf-8");
            System.out.println (result);
            weixinPayService.notifyLogic (result);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return new HashMap ();

    }
}
