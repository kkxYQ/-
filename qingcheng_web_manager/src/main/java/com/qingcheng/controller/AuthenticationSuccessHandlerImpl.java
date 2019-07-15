package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.WebUtil;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //登陆后调用
        System.out.println ("spring框架提供的一个登陆成功处理器组件");
        System.out.println ("登陆成功开始记录日志");
        String name = authentication.getName ();//当前登陆用户
        String ip = request.getRemoteAddr ();//登陆ip
        String header = WebUtil.getBrowserName (request.getHeader ("User-Agent"));//使用的浏览器 简短信息
        String cityByIP = WebUtil.getCityByIP (ip);//使用工具类中的方法根据ip获取地区
        LoginLog loginLog = new LoginLog ();
        loginLog.setIp (ip);
        loginLog.setLoginName (name);
        loginLog.setLoginTime (new Date ());
        loginLog.setBrowserName (header);
        loginLog.setLocation (cityByIP);
        loginLogService.add (loginLog);
        request.getRequestDispatcher ("/main.html").forward (request,response);//转发到main页面
    }
}
