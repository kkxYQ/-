package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SkuSearchService;
import com.qingcheng.utils.CacheKey;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class SkuSearchServiceImpl implements SkuSearchService {

        @Autowired
        private RestHighLevelClient restHighLevelClient;
        @Autowired
        private BrandMapper brandMapper;
        @Autowired
        private SpecMapper specMapper;
        @Autowired
        private RedisTemplate redisTemplate;

    /**
     * 关键字查询
     * @param searchMap
     * @return
     */
    public Map search(Map <String, String> searchMap) {
        //1.封装查询请求
        // 创建search请求
        SearchRequest searchRequest = new SearchRequest ("sku");//将请求限制为sku索引
        searchRequest.types ("doc");//设置查询的类型
        //构造查询请求体 {query:}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder ();
        //查询参数设置  布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery ();

        //1.1关键字搜索  匹配查询
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery ("name", searchMap.get ("keywords"));
        boolQueryBuilder.must (matchQueryBuilder);
        //1.2商品分类   过滤查询
        if(searchMap.get ("category")!=null){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery ("categoryName", searchMap.get ("category"));
            boolQueryBuilder.filter (termQueryBuilder);
        }
        //1.3品牌分类  过滤查询
        if(searchMap.get ("brand")!=null){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery ("brandName", searchMap.get ("brand"));
            boolQueryBuilder.filter (termQueryBuilder);
        }
        //1.4规格过滤
        for (String key : searchMap.keySet ()) {
            if (key.startsWith ("spec.")){//开始值为spec.为规格
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery (key+".keyword", searchMap.get (key));
                boolQueryBuilder.filter (termQueryBuilder);
            }
        }
        //1.5价格过滤
        if(searchMap.get ("price")!=null){
            String[] prices = searchMap.get ("price").split ("-");
            if(!prices[0].equals (0)){//最低价格不等于0
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery ("price").gte (prices[0] + "00");
                boolQueryBuilder.filter (rangeQueryBuilder);
            }
            if(!prices[1].equals ("*")){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery ("price").gte (prices[1] + "00");
                boolQueryBuilder.filter (rangeQueryBuilder);
            }
        }
        //1.6分页
        Integer pageNo=Integer.parseInt (searchMap.get ("pageNo"));
        Integer paeSize=30;//每页显示的数量

        //起始记录坐标
        int fromIndex=(pageNo-1) * paeSize;
        searchSourceBuilder.from (fromIndex);//页索引
        searchSourceBuilder.size (paeSize);//每页显示的条数

        //排序
        String sort=searchMap.get ("sort");//排序字段
        String sortOrder = searchMap.get ("sortOrder");//排序规则
        if(!"".equals (sort)){
            searchSourceBuilder.sort (sort,SortOrder.valueOf (sortOrder));
        }

        searchSourceBuilder.query (boolQueryBuilder);
        searchRequest.source (searchSourceBuilder);


        //商品分类列表  分组查询
        TermsAggregationBuilder  termsAggregationBuilder= AggregationBuilders.terms ("sku_category").field ("categoryName");
        searchSourceBuilder.aggregation (termsAggregationBuilder);



        //2.封装查询结果
        Map resultMap = new HashMap ();
        try {
            SearchResponse searchResponse = restHighLevelClient.search (searchRequest, RequestOptions.DEFAULT);//执行搜索
            SearchHits searchHits = searchResponse.getHits ();//搜索命中的
            long totalHits = searchHits.getTotalHits ();//搜索请求匹配的匹配总数
            System.out.println ("记录数"+totalHits);
            SearchHit[] hits = searchHits.getHits ();//命中的全局信息
            //2.1商品列表
            List<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>> ();
            for (SearchHit hit : hits) {
                Map <String, Object> skuMap = hit.getSourceAsMap ();
                arrayList.add (skuMap);
            }
            resultMap.put ("rows",arrayList);
            //2.2商品分类列表
            Aggregations aggregations = searchResponse.getAggregations ();
            Map <String, Aggregation> aggregationMap = aggregations.getAsMap ();
            Terms terms = (Terms) aggregationMap.get ("sku_category");//复合聚合
            List <? extends Terms.Bucket> buckets = terms.getBuckets ();//储存桶
            List<String> categoryList = new ArrayList ();
            for (Terms.Bucket bucket : buckets) {
                categoryList.add (bucket.getKeyAsString ());
            }
            resultMap.put ("categoryList",categoryList);


            String categoryName="";//商品分类的名称
            if(searchMap.get ("category")==null){//如果没有分类条件
                if(categoryList.size ()>0){
                    categoryName=categoryList.get (0);//提取分类列表的第一个分类
                }else {
                    categoryName=searchMap.get ("category");//取出参数中的分类名称
                }
            }

            //2.3品牌列表
            if(searchMap.get ("brand")==null){
                //List <Map> brandList = brandMapper.findListByCategoryName (categoryName);
                List<Map> brandList = (List <Map>) redisTemplate.boundHashOps (CacheKey.BRAND_LIST).get (categoryName);
                resultMap.put ("brandList",brandList);
            }
            //2.4规格列表
            //List <Map> specList = specMapper.findListBycategroyName (categoryName);
            List<Map> specList = (List <Map>) redisTemplate.boundHashOps (CacheKey.SPEC_LIST).get (categoryList);
            if(specList!=null){
                for (Map spec : specList) {
                    String[] options = ((String) spec.get ("options")).split (",");//规格选项列表
                    spec.put ("options",options);
                }
            }
            resultMap.put ("specList",specList);
            //2.5分页
            //计算总页数
            long totalCont = searchHits.getTotalHits ();//总条数
            long totalPage =(totalCont%paeSize==0)?totalCont/paeSize:(totalCont/paeSize+1);//总页数
            resultMap.put ("totalPages",totalPage);




        } catch (IOException e) {
            e.printStackTrace ();
        }

        return resultMap;
    }
}
