package com.qingcheng.pojo.order;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_transaction_sheet")
public class TransactionSheet {
    private String numberVisitors;//浏览人数


    private String numberPlaced;//下单人数


    private String numberOrders;//订单数

    private String numberUnits;//下单件数

    private String numberAmount;//下单金额


    private String numberRefundAmount;//退款金额


    private String numberPayments;//付款人数


    private String numberValidOrders;//有效订单数

    private String numberPaymentOrders;//付款订单数

    private String numberPiece;//付款件数

    private String payMoney;//付款金额

    private String numberPay;//已经支付

    private Date time;//时间

    public String getNumberVisitors() {
        return numberVisitors;
    }

    public void setNumberVisitors(String numberVisitors) {
        this.numberVisitors = numberVisitors;
    }

    public String getNumberPlaced() {
        return numberPlaced;
    }

    public void setNumberPlaced(String numberPlaced) {
        this.numberPlaced = numberPlaced;
    }

    public String getNumberOrders() {
        return numberOrders;
    }

    public void setNumberOrders(String numberOrders) {
        this.numberOrders = numberOrders;
    }

    public String getNumberUnits() {
        return numberUnits;
    }

    public void setNumberUnits(String numberUnits) {
        this.numberUnits = numberUnits;
    }

    public String getNumberAmount() {
        return numberAmount;
    }

    public void setNumberAmount(String numberAmount) {
        this.numberAmount = numberAmount;
    }

    public String getNumberRefundAmount() {
        return numberRefundAmount;
    }

    public void setNumberRefundAmount(String numberRefundAmount) {
        this.numberRefundAmount = numberRefundAmount;
    }

    public String getNumberPayments() {
        return numberPayments;
    }

    public void setNumberPayments(String numberPayments) {
        this.numberPayments = numberPayments;
    }

    public String getNumberValidOrders() {
        return numberValidOrders;
    }

    public void setNumberValidOrders(String numberValidOrders) {
        this.numberValidOrders = numberValidOrders;
    }

    public String getNumberPaymentOrders() {
        return numberPaymentOrders;
    }

    public void setNumberPaymentOrders(String numberPaymentOrders) {
        this.numberPaymentOrders = numberPaymentOrders;
    }

    public String getNumberPiece() {
        return numberPiece;
    }

    public void setNumberPiece(String numberPiece) {
        this.numberPiece = numberPiece;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getNumberPay() {
        return numberPay;
    }

    public void setNumberPay(String numberPay) {
        this.numberPay = numberPay;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
