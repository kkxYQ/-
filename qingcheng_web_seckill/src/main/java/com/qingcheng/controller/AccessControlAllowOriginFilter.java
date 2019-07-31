package com.qingcheng.controller;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * �������
 */
public class AccessControlAllowOriginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // ��ʹ��*���Զ������������������Я��CookieʱʧЧ
        String origin = request.getHeader("Origin");
        if(StringUtils.isNotBlank(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // ����Ӧ�����Զ���ͷ
        String headers = request.getHeader("Access-Control-Request-Headers");
        if(StringUtils.isNotBlank(headers)) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        // �����������󷽷�����
        response.setHeader("Access-Control-Allow-Methods", "*");
        // Ԥ�����OPTIONS������ʱ�䣬��λ����
        response.setHeader("Access-Control-Max-Age", "3600");
        // ��ȷ��ɿͻ��˷���Cookie��������ɾ���ֶμ���
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }

}
