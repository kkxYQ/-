package com.qingcheng.dao;

import com.qingcheng.pojo.order.TransactionSheet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionSheetMapper extends Mapper<TransactionSheet> {

    //下单人数
    @Select ("select count(*) numberPlaced from (" +
            "select username,count(username) as numberOrders,sum(total_num) as numberUnits,sum(total_money) as numberAmount " +
            "from tb_order " +
            "where DATE_FORMAT( create_time,'%Y-%m-%d')= #{date} " +
            "GROUP BY username ) s")
    public TransactionSheet getPlacedNumber(@Param ("date")LocalDate date);

    //订单数
    @Select("select count(username) as numberOrders,sum(total_num) as numberUnits,sum(total_money) as numberAmount " +
            "from tb_order " +
            "where DATE_FORMAT( create_time,'%Y-%m-%d')= #{date} " +
            "GROUP BY username ")
    public TransactionSheet getOrder(@Param ("date")LocalDate date);

    //付款人数
    @Select("select count(*) numberPayments from ( " +
            "select username,count(username) ,sum(total_num),sum(total_money) " +
            "from tb_order  " +
            "where pay_status='1' and DATE_FORMAT( create_time,'%Y-%m-%d')= #{date}   " +
            "GROUP BY username) s ")
    public TransactionSheet payPlacedNumber(@Param ("date")LocalDate date);

    //付款订单数
    @Select("select count(username) as numberValidOrders,count(username) as numberPaymentOrders,sum(total_num) as numberPiece,sum(total_money) as payMoney,pay_status as numberPay " +
            "from tb_order  " +
            "where pay_status='1' and DATE_FORMAT( create_time,'%Y-%m-%d')= #{date}   " +
            "GROUP BY username")
    public TransactionSheet payOrder(@Param ("date")LocalDate date);

    //退款金额
    @Select ("select return_money as numberRefundAmount from tb_return_order where DATE_FORMAT( apply_time,'%Y-%m-%d')= #{date}")
    public TransactionSheet getReund(@Param ("date") LocalDate date);

    //折线图
    @Select ("select pay_money,number_refund_amount,number_payments,number_piece,number_placed,number_visitors  " +
            "from tb_transaction_sheet  " +
            "WHERE time>=#{date1} and time<=#{date2} ")
    public List<Map> broken(@Param ("date1") String date1,@Param ("date2") String date2);

    //漏斗图
    @Select ("select number_placed,number_visitors,number_payments  " +
            "from tb_transaction_sheet  " +
            "WHERE time=#{date1}  ")
    public List<Map> funnel(@Param ("date1") String date1);

}
