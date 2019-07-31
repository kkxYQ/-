package com.qingcheng.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/redirect")
public class RedirectController {

    /**
     * 登陆地址跳转
     * referer是header的一部分，
     * 浏览器向web服务器发送请求的时候，一般会带上Referer，告诉服务器我是从哪个页面链接过来的。
     * @param referer
     * @return
     */
    @RequestMapping("/back")
    public String redirect(@RequestHeader(value = "Referer",required = false)String referer) {
        if (!StringUtils.isEmpty (referer)){//地址不为空重定向到
            return "redirect:"+referer;
        }
        return "redirect:/seckill-index.html";//地址为空重定向到秒杀页
    }
}
