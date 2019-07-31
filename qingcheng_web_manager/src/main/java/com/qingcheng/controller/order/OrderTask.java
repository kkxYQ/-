package com.qingcheng.controller.order;


import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderTask {

    @Reference
    private CategoryReportService categoryReportService;


    @Scheduled(cron = "0 0 1 * * ?")
    public void createCategoryReportDate(){
        System.out.println ("createCategoryReportDate");
        categoryReportService.createData ();
    }
}
