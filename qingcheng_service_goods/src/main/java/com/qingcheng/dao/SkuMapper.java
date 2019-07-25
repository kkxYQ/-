package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    /**
     * 减少库存
     * @param skuId
     * @param num
     */
    @Update ("update tb_sku set num=num-#{num} where id=#{id}")
    public void add(@Param ("id") String skuId,@Param ("num") Integer num);

    /**
     *增加销量
     * @param skuId
     * @param num
     */

    @Update ("update tb_sku set sale_num=sale_num+#{num} where id=#{id}")
    public void reduce(@Param ("id") String skuId,@Param ("num") Integer num);

}
