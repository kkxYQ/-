package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.pojo.goods.SpuandSku;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String,Object> searchMap);


    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);


    public Spu findById(String id);

    public void add(Spu spu);


    public void update(Spu spu);


    public void delete(String id);

    public void saveSpuandSku(SpuandSku spuandSku);

    public SpuandSku findSpuandSkuById(String id);

    public void audit(String id,String status,String meagess);

    /**
     * 下架商品
     * @param id
     */
    public void pull(String id);

    /**
     * 上架商品
     * @param id
     */
    public void put(String id);

    /**
     * 批量上架商品
     * @param ids
     * @return
     */
    public int putMany(Long[] ids);

    /**
     * 还原商品修改spu表is_delete字段为0
     * @param id
     */
    public void reduction(String id);
}
