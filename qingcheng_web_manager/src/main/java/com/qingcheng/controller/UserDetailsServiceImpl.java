package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.system.AdminService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Reference
    private AdminService adminService;


    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //查询管理员
        Map map = new HashMap ();
        map.put ("loginName",s);
        map.put ("status","1");
        List<Admin> list = adminService.findList (map);
        if(list.size ()==0){
            return  null;
        }
        System.out.println ("经过UserDetailsService.......");
        //构建角色集合，项目中此处是根据用户名查询出的用户的角色列表
        List<GrantedAuthority> listGrantedAuthority = new ArrayList <GrantedAuthority> ();
        listGrantedAuthority.add (new SimpleGrantedAuthority ("ROLE_ADMIN"));
        return new User (s,list.get (0).getPassword (),listGrantedAuthority);
    }
}
