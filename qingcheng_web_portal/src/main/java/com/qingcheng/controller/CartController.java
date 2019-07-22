//package com.qingcheng.controller;


//import com.alibaba.dubbo.config.annotation.Reference;
//import com.qingcheng.service.order.CartService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Map;

//@RestController
//@RequestMapping("/cart")
//public class CartController {
//
//    @Reference
//    private CartService cartService;
//
//    /**
//     * 从redis中提取购物车
//     * @return
//     */
//    @RequestMapping("/findCartList")
//    public List<Map<String, Object>>  findCartList(){
//        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
//        return cartService.findCartList (username);
//    }
//
//}
