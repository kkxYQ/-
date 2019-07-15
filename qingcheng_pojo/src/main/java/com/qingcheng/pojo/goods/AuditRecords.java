package com.qingcheng.pojo.goods;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "audit_records")
public class AuditRecords implements Serializable {
     private String name;//操作人
     private String caption;//商品名称
     private Date time;//审核记录时间
     private String result;//审核状态  商品审核
     private String details;//审核反馈详情 反馈详情
     private Integer price;//审核日志价格
     private String status;//商品上下架状态

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
