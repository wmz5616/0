package com.zemcho.guzhe.util.uuid;

import org.apache.commons.lang.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @title: OrderNoUtil
 * @Description: 订单号生成工具
 * @Date: 2024/2/21 14:34
 */
public class OrderNoUtil {
    /**
     * 生成单号（25位）：时间（精确到毫秒）+5位id+3位随机数
     *
     * @param id
     * @return
     */
    public static synchronized String generateNo(Integer id) {
        //时间（精确到毫秒）
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(ofPattern);
        //3位随机数
        String randomNumeric = RandomStringUtils.randomNumeric(3);
        //5位用户id
        int subStrLength = 5;
        String sUserId = id.toString();
        int length = sUserId.length();
        String str;
        if (length >= subStrLength) {
            str = sUserId.substring(length - subStrLength, length);
        } else {
            str = String.format("%0" + subStrLength + "d", id);
        }
        String orderNum = localDate + str + randomNumeric;

        return orderNum;
    }
}
