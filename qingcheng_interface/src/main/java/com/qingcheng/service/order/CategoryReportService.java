package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表服务层接口
 */
public interface CategoryReportService {
    /**
     * 商品类目按日期统计(订单表关联查询)
     * @param date
     * @return
     */
    public List<CategoryReport> CategoryReport(LocalDate date);

    /**
     * 定时任务生成统计数据
     */
    public void createData();

    List<Map> category1Count(String date1, String date2);
}
