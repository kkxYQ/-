package com.qingcheng.service.goods;
import java.util.Map;

public interface SkuSearchService {
    /**
     * 关键字搜索逻辑
     * @param searchMap
     * @return
     */
    public Map search(Map<String,String> searchMap);
}
