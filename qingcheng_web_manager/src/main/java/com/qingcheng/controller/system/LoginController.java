package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.system.AdminService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//主界面显示当前登陆人
//后端编写controller输出当前登录人，前端异步调用
import java.util.HashMap;

import java.util.Map;

@RestController
@RequestMapping("/login")

public class LoginController {
    @Reference
    private AdminService adminService;

    @GetMapping("/name")
    public Map showNmae(){
    String name = SecurityContextHolder.getContext ().getAuthentication ().getName ();
    HashMap map = new HashMap ();
    map.put ("name",name);
    return map;
   }

   @GetMapping("/changePassword")
   public Result changePassword(String oldpassword,String newPassword){
        //根据上下文获取用户名
       String name= SecurityContextHolder.getContext ().getAuthentication ().getName ();
       //查询加密的密码
       Admin admin = adminService.findByIdUsername (name);
       if(admin==null||"".equals (admin)){
           return new Result (1,"用户不存在");
       }
       //密码效验
       boolean checkpw = BCrypt.checkpw (oldpassword, admin.getPassword ());
       if (checkpw){
           admin.setPassword (newPassword);
           adminService.update (admin);
           return new Result ();
       }else {
           return new Result (1,"新密码与旧密码一致");
       }


   }


}
