package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.pojo.goods.SpuandSku;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SpuService spuService;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value ("${pagePath}")
    private String pagePath;

    /**
     * 生成商品详情页
     * @param id
     */
    @GetMapping("/createPage")
    public void createPage(String id){
        //根据id查询商品信息的组合实体类
        SpuandSku goods = spuService.findSpuandSkuById (id);
        //获取SPU信息
        Spu spu = goods.getSpu ();
        //获取sku列表
        List <Sku> skuList = goods.getSkuList ();
        //查询商品分类
        List<String> categoryList = new ArrayList<String> ();
        categoryList.add(categoryService.findById (spu.getCategory1Id ()).getName());//一级分类
        categoryList.add(categoryService.findById (spu.getCategory2Id ()).getName());//二级分类
        categoryList.add(categoryService.findById (spu.getCategory3Id ()).getName());//三级分类


        //生成sku地址列表
        Map<String,String> urlMap=new HashMap <String, String> ();
        for (Sku sku : skuList) {
            if("1".equals (sku.getStatus ())){     //添加判断，筛选有效sku
                                                   //对原来的json字符串转成对象，在对对象进行排序（只要类容一样，排序就是一样的）
                String specJson = JSON.toJSONString (JSON.parseObject (sku.getSpec ()), SerializerFeature.MapSortField);//转换成json字符串并进行排序
                urlMap.put (specJson,sku.getId ()+".html");//构建了sku的地址列表
            }
        }



        //创建页面（每一个页面位一个sku）
        for (Sku sku : skuList) {
            //1.创建上下文
            Context context = new Context ();
            //数据模型
            Map<String,Object> dateModel = new HashMap ();
            dateModel.put ("spu",spu);
            dateModel.put ("sku",sku);
            dateModel.put ("categoryList",categoryList);//商品分类面包屑
            dateModel.put ("skuImages",sku.getImages ().split (","));//sku图片列表
            dateModel.put ("spuImages",spu.getImages ().split (","));//spu图片列表

            Map paraItems=JSON.parseObject (spu.getParaItems ());
            dateModel.put ("paraItems",paraItems);//参数列表

            Map specItems=JSON.parseObject (sku.getSpec ());
            dateModel.put ("specItems",specItems);//规格列表

            //  {"颜色":["金色","黑色","蓝色"],"版本":["6GB+64GB"]}
            Map<String,List> specMap =(Map) JSON.parseObject (spu.getSpecItems ());//规格和规格选项转换成map集合

            for (String key : specMap.keySet ()) {//循环规格
                List<String> list = specMap.get (key);//["金色","黑色","蓝色"]
                List<Map> mapList=new ArrayList  ();//新的集合   [{'option':'金色',checked:true},{'option':'黑色',checked:false}]
                for (String value : list) {////循环规格选项值
                    Map map=new HashMap ();
                    map.put ("option",value);
                    if(value.equals (specItems.get (key))){//判断此规格组合是否是当前sku的，标记选中状态
                        map.put ("checked",true);
                    }else {
                        map.put ("checked",false);
                    }

                    Map<String,String> spec=(Map) JSON.parseObject (sku.getSpec ());//当前的sku
                    spec.put (key,value);//当前循环到的规格选项，就是你当前点击的
                    String specJson = JSON.toJSONString (spec, SerializerFeature.MapSortField);//转换成json字符串并进行排序

                    map.put ("url",urlMap.get (specJson));//把当前字符串作为key取到url的内容

                    mapList.add (map);
                }
                specMap.put (key,mapList);//用新集合覆盖原来的集合
            }
            dateModel.put ("specMap",specMap);//规格面板

            context.setVariables(dateModel);
            //2准备文件
            File dir = new File (pagePath);
            if(!dir.exists ()){
                dir.mkdirs ();
            }
            File dest = new File (dir, sku.getId () + ".html");
            //3.生成页面
            try {
                PrintWriter writer = new PrintWriter (dest, "UTF-8");
                templateEngine.process ("item",context,writer);
            } catch (Exception e) {
                e.printStackTrace ();
            }

        }

    }

}
