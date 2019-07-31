package com.qingcheng.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/redirect")
public class RedirectController {

    /**
     * ��½��ַ��ת
     * referer��header��һ���֣�
     * �������web���������������ʱ��һ������Referer�����߷��������Ǵ��ĸ�ҳ�����ӹ����ġ�
     * @param referer
     * @return
     */
    @RequestMapping("/back")
    public String redirect(@RequestHeader(value = "Referer",required = false)String referer) {
        if (!StringUtils.isEmpty (referer)){//��ַ��Ϊ���ض���
            return "redirect:"+referer;
        }
        return "redirect:/seckill-index.html";//��ַΪ���ض�����ɱҳ
    }
}
