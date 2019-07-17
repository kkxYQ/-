package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {
    /**
     * 根据分类名称查询品牌列表
     * @param categoryName
     * @return
     */
    @Select ("SELECT " +
            " tb_brand.`name`, " +
            " tb_brand.image  " +
            "FROM " +
            " tb_brand, " +
            " tb_category_brand, " +
            " tb_category " +
            "WHERE " +
            " tb_brand.id = tb_category_brand.brand_id " +
            " AND tb_category_brand.category_id = tb_category.id " +
            " AND tb_category.`name` = #{name} " +
            "ORDER BY " +
            " tb_brand.seq ASC")
    public List<Map> findListByCategoryName(@Param ("name") String categoryName);

    @Select ("")
    public List<Map> ss(String categoryName);
}
