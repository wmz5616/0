package com.zemcho.guzhe.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author Ryan
 * @title: TimeUtil
 * @projectName master
 * @description: ZEMCHO
 * @date 2021/3/10 0010 10:38
 */
public class TimeUtil {
    public static DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getTimeString(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd_HHmm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
    
    public static List<Map<String, Object>> TimeFormat(List<Map<String, Object>> data){
        List<Map<String, Object>> dataResult = new ArrayList<>();
        for (Map<String, Object> map : data){
            for (Map.Entry<String, Object> entry : map.entrySet()){
                try {
                    Date date1 = new Date(((Timestamp)entry.getValue()).getTime());
                    String result = df2.format(date1);
                    map.replace(entry.getKey(), result);
                }catch (Exception e) {
                    continue;
                }
            }
            dataResult.add(map);
        }
        return dataResult;
    }

    public static Map<String, Object> TimeFormat(Map<String, Object> data){
        for (Map.Entry<String, Object> entry : data.entrySet()){
            try {
                String result = df2.format(((Timestamp)entry.getValue()).getTime());
                data.replace(entry.getKey(), result);
            }catch (Exception e) {
                continue;
            }
        }
        return data;
    }

    public static Date toDate(LocalDateTime localDateTime){
        Date date = Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static void main(String[] args) throws ParseException {
        String a = "aaaa";
        String b = a;
        a.replace("aaaa","bbbb");
        System.out.println(b);
    }
}
