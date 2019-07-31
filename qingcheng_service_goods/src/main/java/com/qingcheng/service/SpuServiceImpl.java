package com.qingcheng.service;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.goods.SkuService;
import com.qingcheng.goods.SpuService;
import com.qingcheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    @Autowired
    private AuditRecordsMapper auditRecordsMapper;
    @Autowired
    private SkuService skuService;

    /**
     * 返回全部记录
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     *  逻辑删除
     * @param id
     */
    public void delete(String id) {

        //删除缓存中的价格
        Map map=new HashMap ();
        map.put ("skuId",id);
        List<Sku> list = skuService.findList (map);
        for (Sku sku : list) {
            skuService.deletePriceFromRedis (sku.getId ());
        }

        Spu spu = new Spu ();
        spu.setIsDelete ("1");
        spuMapper.updateByPrimaryKeySelective (spu);

    }

    /**
     * 还原商品
     * @param id
     */
    public void reduction(String id) {
        Spu spu = new Spu ();
        spu.setIsDelete ("0");
        spuMapper.updateByPrimaryKeySelective (spu);
    }

    /**
     * 保存商品
     * @param spuandSku
     */
    @Transactional
    public void saveSpuandSku(SpuandSku spuandSku) {
        //保存一个spu的商品信息
        Spu spu = spuandSku.getSpu ();

        if(spu.getId ()==null){//新增商品
            spu.setId (idWorker.nextId ()+"");
            spuMapper.insert (spu);
        }else{
            //删除原来的sku列表
            Example example = new Example (Sku.class);
            Example.Criteria criteria = example.createCriteria ();
            criteria.andEqualTo ("spuId",spu.getId ());
            skuMapper.deleteByExample (example);
            //执行spu修改
            spuMapper.updateByPrimaryKeySelective (spu);
        }

        //保存sku列表信息
        Date date=new Date ();
        //分类对象
        Category category = categoryMapper.selectByPrimaryKey (spu.getCategory3Id ());

        List <Sku> skuList = spuandSku.getSkuList();
        for (Sku sku : skuList) {

            if(sku.getId ()==null){//新增
                sku.setId(idWorker.nextId ()+"");
                sku.setUpdateTime (date);//修改日期
            }

            sku.setSpuId (spu.getId ());//关联spu表
            //sku 名称=spu名称+规格列表spec_items
            String name = spu.getName ();

            if(sku.getSpec ()==null||"".equals (sku.getSpec ())){
                sku.setSpec ("{}");
            }
            //sku.getSpec (){"颜色":"红","机身内存":"64g"}
            Map<String,String> specMap = JSON.parseObject (sku.getSpec (), Map.class);

            for (String value : specMap.values ()) {
                name+=" "+value;
            }
            sku.setName (name);//名称
            sku.setCreateTime (date);//创建日期
            sku.setUpdateTime (date);//修改日期
            sku.setCategoryId (spu.getCategory3Id ());//分类ID
            sku.setCategoryName (category.getName ());//分类名称
            sku.setCommentNum (0);//评论数
            sku.setSaleNum (0);//销售数量
            skuMapper.insert (sku);

            //重新将价格更新到缓存
            skuService.savePriceRedisById (sku.getId (),sku.getPrice ());
        }
        //建立分类与品牌的关联
        CategoryBrand categoryBrand = new CategoryBrand ();
        categoryBrand.setBrandId (spu.getBrandId ());
        categoryBrand.setCategoryId (spu.getCategory3Id ());
        int count = categoryBrandMapper.selectCount (categoryBrand);
        if(count==0){
            categoryBrandMapper.insert (categoryBrand);
        }

    }

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    public SpuandSku findSpuandSkuById(String id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey (id);
        //查询sku列表 sku中的外键不是主键所以必须要用到条件进行查询
        //结果等效于  select * from sku where spuId=#{id }
        Example example = new Example (Sku.class);
        Example.Criteria criteria = example.createCriteria ();
        criteria.andEqualTo ("spuId",id);//property对应的是实体类的属性字段
        List <Sku> skuList = skuMapper.selectByExample (example);
        //封装返回
        SpuandSku spuandSku = new SpuandSku ();
        spuandSku.setSpu (spu);
        spuandSku.setSkuList (skuList);
        return spuandSku;
    }

    /**
     * 商品审核与下架
     * @param id
     * @param status
     * @param meagess
     */
    @Transactional
    public void audit(String id, String status, String meagess) {
        //1.修改状态，审核状态和上架状态
        Spu spu = new Spu ();
        spu.setId (id);
        spu.setStatus (status);
        if("1".equals (status)){//审核通过
            spu.setIsMarketable ("1");//自动上架
        }
        spuMapper.updateByPrimaryKeySelective (spu);
        //2.记录商品审核
        //3.记录商品日志
        AuditRecords audit = new AuditRecords ();
        Date date = new Date ();
        audit.setTime (date);
        audit.setName ("admin");
        audit.setCaption (spu.getCaption ());
        if("1".equals (spu.getStatus ())){
            audit.setResult ("审核通过");
        }else{
            audit.setResult ("审核不通过");
            audit.setDetails ("图片不清晰");
        }
        Sku sku = new Sku ();
        audit.setPrice (sku.getPrice ());
        String skuStatus = sku.getStatus ();
        if("1".equals (skuStatus)){
            audit.setStatus ("正常");
        }
        if("2".equals (skuStatus)){
            audit.setStatus ("下架");
        }
        if("3".equals (skuStatus)){
            audit.setStatus ("删除");
        }
        auditRecordsMapper.insertSelective (audit);
    }
    /**
     * 下架商品
     * @param id
     */
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey (id);
        spu.setIsMarketable ("0");//下架状态
        spuMapper.updateByPrimaryKeySelective (spu);
    }
    /**
     * 上架商品
     * @param id
     */
    public void put(String id) {
        //1.修改状态
        //2.记录商品日志
        Spu spu = spuMapper.selectByPrimaryKey (id);

        AuditRecords audit = new AuditRecords ();
        if("1".equals (spu.getStatus ())){

            audit.setResult ("审核不通过");
            audit.setDetails ("图片不清晰");

            throw new RuntimeException ("此商品未通过审核");
        }
        audit.setResult ("审核通过");
        audit.setStatus ("正常");
        spu.setIsMarketable ("1");
        auditRecordsMapper.insertSelective (audit);
        spuMapper.updateByPrimaryKeySelective (spu);





    }
    /**
     * 批量上架商品
     * @param ids
     * @return
     */
    public int putMany(Long[] ids) {
        Spu spu = new Spu ();
        spu.setIsMarketable ("1");//上架
        //批量修改
        Example example = new Example (Spu.class);
        Example.Criteria criteria = example.createCriteria ();
        criteria.andIn ("id", Arrays.asList (ids));//id
        criteria.andEqualTo ("isMarketable","0");//下架
        criteria.andEqualTo ("status","1");//审核通过
        criteria.andEqualTo ("isDelete","0");//非删除的
        return spuMapper.updateByPrimaryKeySelective (spu);
    }



    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
            }
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
            }
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
            }
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
            }
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
            }
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andLike("isMarketable","%"+searchMap.get("isMarketable")+"%");
            }
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andLike("isEnableSpec","%"+searchMap.get("isEnableSpec")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
