package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService;

    /**
     * 得到2019-04-15这一天得数据
     * @return
     */
    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){

        LocalDate localDate = LocalDate.now ().minusDays (1);//得到昨天的日期

        String s="2019-04-15";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(s, fmt);

        List <CategoryReport> categoryReports = categoryReportService.CategoryReport (date);
        return categoryReports;
    }

    /**
     * 统计类目
     * @param date1
     * @param date2
     * @return
     */
    @GetMapping("/category1Count")
    public List<Map> category1Count(String date1,String date2){
        return categoryReportService.category1Count (date1,date2);
    }


}
