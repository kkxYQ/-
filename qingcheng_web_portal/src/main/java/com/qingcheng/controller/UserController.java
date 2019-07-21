package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @GetMapping("/sendSms")
    public Result sendSms(String phone){
        userService.sendSms (phone);
        return new Result ();
    }
    @PostMapping("/save")
    public Result save(@RequestBody User user, String code){
        //密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder ();
        String newpassword = encoder.encode (user.getPassword ());
        user.setPassword (newpassword);

        userService.add (user,code);
        return new Result();
    }
}
