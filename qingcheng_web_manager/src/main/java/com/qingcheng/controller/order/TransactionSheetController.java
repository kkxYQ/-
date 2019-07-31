package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.order.TransactionSheetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tran")
public class TransactionSheetController {

    @Reference
    private TransactionSheetService transactionSheetService;


    @GetMapping("/created")
    public void created(){
        transactionSheetService.createData ();
    }


    /**
     * 统计类目 折线图
     * @param date1
     * @param date2
     * @return
     */
    @GetMapping("/count")
    public List<Map> count(String date1, String date2){
    return  transactionSheetService.countTracn (date1,date2);
    }

    /**
     * 统计类目 漏斗图
     * @param date1
     * @return
     */
    @GetMapping("/date")
    public List<Map> getdata(String date1){
    return transactionSheetService.countFunnel (date1);
    }
}
