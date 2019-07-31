package com.qingcheng.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2019/5/7 13:07
 *
 ****/
public class DateUtil {

    /***
     * ��yyyy-MM-dd HH:mm��ʽת��yyyyMMddHH��ʽ
     * @param dateStr
     * @return
     */
    public static String formatStr(String dateStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(dateStr);
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * ��ȡָ�����ڵ��賿
     * @return
     */
    public static Date toDayStartHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();
        return start;
    }


    /***
     * ʱ������N����
     * @param date
     * @param minutes
     * @return
     */
    public static Date addDateMinutes(Date date,int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);// 24Сʱ��
        date = calendar.getTime();
        return date;
    }

    /***
     * ʱ�����NСʱ
     * @param hour
     * @return
     */
    public static Date addDateHour(Date date,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);// 24Сʱ��
        date = calendar.getTime();
        return date;
    }

    /***
     * ��ȡʱ��˵�
     * @return
     */
    public static List<Date> getDateMenus(){
        //����һ��List<Date>���ϣ��洢����ʱ���
        List<Date> dates = new ArrayList<Date>();
        //ѭ��12��
        Date date = toDayStartHour(new Date()); //�賿
        for (int i = 0; i <12 ; i++) {
            //ÿ�ε���2Сʱ,��ÿ�ε�����ʱ����뵽List<Date>������
            dates.add(addDateHour(date,i*2));
        }

        //�жϵ�ǰʱ�������ĸ�ʱ�䷶Χ
        Date now = new Date();
        for (Date cdate : dates) {
            //��ʼʱ��<=��ǰʱ��<��ʼʱ��+2Сʱ
            if(cdate.getTime()<=now.getTime() && now.getTime()<addDateHour(cdate,2).getTime()){
                now = cdate;
                break;
            }
        }

        //��ǰ��Ҫ��ʾ��ʱ��˵�
        List<Date> dateMenus = new ArrayList<Date>();
        for (int i = 0; i <5 ; i++) {
            dateMenus.add(addDateHour(now,i*2));
        }
        return dateMenus;
    }

    /***
     * ʱ��ת��yyyyMMddHH
     * @param date
     * @return
     */
    public static String date2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        return simpleDateFormat.format(date);
    }
}
