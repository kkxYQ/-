package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.seckill.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill/order")
public class SeckillOrderController {


    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * �û��µ�����
     * ����service�����Ӷ���
     * �������ʣ�anonymousUser
     * @param id ��Ʒid
     * @return
     */
    @GetMapping("/add")
    public Result add(String time,Long id){
        try {
            //��ȡ�û���
            String username = SecurityContextHolder.getContext ().getAuthentication ().getName ();
            //�û�û�е�½
            if (username.equalsIgnoreCase ("anonymousUser")){
                return new Result (403,"���ȵ�½");
            }
            //����service���Ӷ���
            Boolean add = seckillOrderService.add (id, time, username);
            if(add){
                return new Result (0,"�µ��ɹ�");
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return new Result(1,e.getMessage());
        }
        return new Result (1,"��ɱ�µ�ʧ��");
    }
}
