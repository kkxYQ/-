package com.qingcheng.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//用户中心显示登录名
@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * 获取用户名
     */
    @RequestMapping("/username")
    public Map username(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//得到登录人账号
        System.out.println("当前登录用户："+name);
        if("anonymousUser".equals(name)){ //未登录
            name="";
        }
        Map map=new HashMap ();
        map.put("username", name);
        return map;
    }

}
