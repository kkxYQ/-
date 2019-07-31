package com.qingcheng.pojo.order;

import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_transaction_sheet")
public class TransactionSheet {
    private String numberVisitors;//�������


    private String numberPlaced;//�µ�����


    private String numberOrders;//������

    private String numberUnits;//�µ�����

    private String numberAmount;//�µ����


    private String numberRefundAmount;//�˿���


    private String numberPayments;//��������


    private String numberValidOrders;//��Ч������

    private String numberPaymentOrders;//�������

    private String numberPiece;//�������

    private String payMoney;//������

    private String numberPay;//�Ѿ�֧��

    private Date time;//ʱ��

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
