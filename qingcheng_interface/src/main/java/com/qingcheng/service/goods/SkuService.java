package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import org.omg.PortableInterceptor.Interceptor;

import java.util.*;

/**
 * sku业务逻辑层
 */
public interface SkuService {


    public List<Sku> findAll();


    public PageResult<Sku> findPage(int page, int size);


    public List<Sku> findList(Map<String,Object> searchMap);


    public PageResult<Sku> findPage(Map<String,Object> searchMap,int page, int size);


    public Sku findById(String id);

    public void add(Sku sku);


    public void update(Sku sku);


    public void delete(String id);

    /**
     * 保存全部价格到缓存中
     */
    public void saveAllPriceToRedis();

    /**
     * 查询价格缓存
     * @param id
     * @return
     */
    public Integer findPrice(String id);

    /**
     * 根据sku id 更新商品价格
     * @param id
     * @param price
     */
    public void savePriceRedisById(String id,Integer price);

    /**
     * 根据sku id 删除商品价格缓存
     * @param id
     */
    public void deletePriceFromRedis(String id);

    /**
     * 导入索引库
     */
    public void importToEs();
}
