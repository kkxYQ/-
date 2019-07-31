package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.TransactionSheetMapper;
import com.qingcheng.order.TransactionSheetService;
import com.qingcheng.pojo.order.TransactionSheet;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TransactionSheetServiceImpl implements TransactionSheetService {

    @Autowired
    private TransactionSheetMapper transactionSheetMapper;


    String s="2019-04-15";
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = LocalDate.parse(s, fmt);

    /**
     * ��ʱ�����ѯ
     */
    @Override
    public void createData() {

        SimpleDateFormat myFmt=new SimpleDateFormat ("yyyy-MM-dd");
        Date dateTime = null;
        try {
             dateTime = myFmt.parse (s);
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        TransactionSheet tran = new TransactionSheet ();
        tran.setNumberVisitors ("1000");
        tran.setNumberPlaced (transactionSheetMapper.getPlacedNumber (date).getNumberPlaced ());//�µ�����
        TransactionSheet order = transactionSheetMapper.getOrder (date);
        tran.setNumberOrders (order.getNumberOrders ());//������
        tran.setNumberUnits (order.getNumberUnits ());//�µ�����
        tran.setNumberAmount (order.getNumberAmount ());//�µ����
        if(transactionSheetMapper.getReund (date).getNumberRefundAmount ()==null){
            tran.setNumberRefundAmount ("0");//�˿���
        }else {
            tran.setNumberRefundAmount (transactionSheetMapper.getReund (date).getNumberRefundAmount ());//�˿���
        }
        tran.setNumberPayments (transactionSheetMapper.payPlacedNumber (date).getNumberPayments ());//��������
        TransactionSheet sheet = transactionSheetMapper.payOrder (date);
        tran.setNumberValidOrders (sheet.getNumberValidOrders ());//��Ч������
        tran.setNumberPaymentOrders (sheet.getNumberPaymentOrders ());//�������
        tran.setNumberPiece (sheet.getNumberPiece ());//�������
        tran.setPayMoney (sheet.getPayMoney ());//������
        tran.setTime (dateTime);
        tran.setNumberPay (sheet.getNumberPay ());//1 �Ѿ�֧��
        transactionSheetMapper.insert (tran);
    }

    /**
     * ����ʱ���ѯ ����ͼ
     * @param date1
     * @param date2
     * @return
     */
    @Override
    public List<Map> countTracn(String date1, String date2) {
        List <Map> broken = transactionSheetMapper.broken (date1, date2);
        return broken;
    }

    @Override
    public List <Map> countFunnel(String date1) {
        List <Map> funnel = transactionSheetMapper.funnel (date1);
        return funnel;
    }
}
