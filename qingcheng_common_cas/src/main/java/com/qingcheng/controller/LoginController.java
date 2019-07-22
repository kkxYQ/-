package com.qingcheng.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//�û�������ʾ��¼��
@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * ��ȡ�û���
     */
    @RequestMapping("/username")
    public Map username(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//�õ���¼���˺�
        System.out.println("��ǰ��¼�û���"+name);
        if("anonymousUser".equals(name)){ //δ��¼
            name="";
        }
        Map map=new HashMap ();
        map.put("username", name);
        return map;
    }

}
