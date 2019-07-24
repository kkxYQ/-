package com.qingcheng.controller;
import java.io.IOException;
import java.util.HashMap;
import	java.util.Map;
import	java.util.List;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.order.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private  CartService cartService;

    /**
     * ��redis����ȡ���ﳵ
     * @return
     */
    @GetMapping("/findCartList")
    public List<Map<String, Object>>  findCartList(){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        List <Map <String, Object>> list = cartService.findCartList (username);
        return list;
    }

    /**
     * �����Ʒ�����ﳵ
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addItem")
    public Result addItem(String skuId,Integer num){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        cartService.addItem (username,skuId,num);
        return new Result ();
    }

    /**
     * ��Ʒ����ҳ��ת�����ﳵ
     * @param response
     * @param skuId
     * @param num
     * @throws IOException
     */
    @GetMapping("/buy")
    public void buy(HttpServletResponse response,String skuId,Integer num) throws IOException {
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        cartService.addItem (username,skuId,num);
        response.sendRedirect ("/cart.html");
    }

    /**
     * ����ѡ��״̬
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/upadtecheck")
    public Result updateChecked(String skuId,boolean checked){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        cartService.updateChecked (username,skuId,checked);
        return new Result ();
    }

    /**
     * ѡ������ɾ��
     * @return
     */
    @GetMapping("/deleteCheckedCart")
    public Result deleteCheckedCart(){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        cartService.deleteCheckedCart (username);
        return new Result ();
    }

    /**
     * ���㵱ǰ���ﳵ�Żݽ��
     * @return
     */
    @GetMapping("/preferential")
    public Map preferential(){
        String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
        int preferential = cartService.preferential (username);
        Map map=new HashMap ();
        map.put ("preferential",preferential);
        return map;

    }

}
