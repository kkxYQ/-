package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {
    /**
     * 根据商品分类查询商品列表
     * @param categoryName
     * @return
     */
    @Select ("SELECT  " +
            " tb_spec.`name`,  " +
            " tb_spec.`options`   " +
            "from   " +
            " tb_spec   " +
            "where   " +
            "  template_id in (select template_id from tb_category where name=#{name})   " +
            "ORDER BY seq")
    public List<Map> findListBycategroyName(@Param ("name") String categoryName);

}
