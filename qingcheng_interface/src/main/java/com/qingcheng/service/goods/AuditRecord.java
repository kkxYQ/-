package com.qingcheng.service.goods;

public interface AuditRecord {
    //2.记录商品审核日志
    //3.记录商品日志
    public void insert(AuditRecord audit);
}
