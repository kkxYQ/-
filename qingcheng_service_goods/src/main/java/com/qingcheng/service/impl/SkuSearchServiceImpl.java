package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class SkuSearchServiceImpl implements SkuSearchService {

        @Autowired
        private RestHighLevelClient restHighLevelClient;

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
        //构造查询请求体
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

            resultMap.put ("rows",arrayList);
        } catch (IOException e) {
            e.printStackTrace ();
        }



        return resultMap;
    }
}
