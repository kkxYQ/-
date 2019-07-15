package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CategoryReportMapper extends Mapper<CategoryReport>{
    @Select ("SELECT " +
            "oi.category_id1 AS categoryId1, " +
            "oi.category_id2 AS categoryId2, " +
            "oi.category_id3 AS categoryId3, " +
            "DATE_FORMAT( o.pay_time,'%Y-%m-%d') AS countDate , " +
            "Sum( oi.num ) AS num, " +
            "Sum( oi.money ) AS money " +
            "FROM " +
            "tb_order AS o, " +
            "tb_order_item AS oi  " +
            "WHERE " +
            "o.id = oi.order_id " +
            "AND o.is_delete = 0 " +
            "AND o.pay_status = 1 " +
            "AND DATE_FORMAT( o.pay_time,'%Y-%m-%d')=#{date} " +
            "GROUP BY " +
            "oi.category_id1, " +
            "oi.category_id2, " +
            "oi.category_id3, " +
            "DATE_FORMAT( o.pay_time,'%Y-%m-%d')")
    public List<CategoryReport> getCategory(@Param ("date") LocalDate date);

    @Select ("SELECT" +
            " category_id1 AS categoryId1, " +
            " v.name AS categoryName, " +
            " SUM(num) AS num, " +
            " SUM(money) AS money " +
            "FROM " +
            "    tb_category_report t,v_category v " +
            "WHERE " +
            "  t.category_id1=v.id " +
            "and " +
            "  count_date>=#{date1} " +
            "AND " +
            "count_date<=#{date2} " +
            "GROUP BY " +
            " category_id1,v.`name`")
    public List<Map> category1Count(@Param ("date1") String date1,@Param ("date2") String date2);
}
