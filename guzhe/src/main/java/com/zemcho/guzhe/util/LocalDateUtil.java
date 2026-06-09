package com.zemcho.guzhe.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * @title: LocalDateUtil
 * @Description:
 * @Date: 2022/11/18 16:30
 */
public class LocalDateUtil {
    /**
     * 获取本日开始时间
     *
     * @return
     */
    public static String getStartTime() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本日结束时间
     *
     * @return
     */
    public static String getEndTime() {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取某日的开始时间戳
     *
     * @param dateTime
     * @return
     */
    public static long getStartTimestamp(LocalDateTime dateTime) {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.MIN);
        return getMilliByTime(startTime);
    }

    /**
     * 获取某日的结束时间戳
     *
     * @param dateTime
     * @return
     */
    public static long getEndTimestamp(LocalDateTime dateTime) {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.MAX);
        return getMilliByTime(endTime);
    }

    /**
     * 获取本周开始时间
     *
     * @return
     */
    public static String getStartWeek() {
        LocalDate today = LocalDate.now();
        LocalDate oneDayOfWeek = getOneDayOfWeek(today, 1);
        return LocalDateTime.of(oneDayOfWeek, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本周的结束时间
     *
     * @return
     */
    public static String getEndWeek() {
        LocalDate today = LocalDate.now();
        LocalDate oneDayOfWeek = getOneDayOfWeek(today, 7);
        return LocalDateTime.of(oneDayOfWeek, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取一周内的某一天
     *
     * @param today 这周内任意一天的日期
     * @param day   想要获取一周中的第几天
     * @return
     */
    private static LocalDate getOneDayOfWeek(TemporalAccessor today, int day) {
        TemporalField fieldIso = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        LocalDate localDate = LocalDate.from(today);
        return localDate.with(fieldIso, day);
    }

    /**
     * 获取本月的开始日期
     *
     * @return
     */
    public static String getOneDayOfMonth() {
        LocalDate date = LocalDate.now();
        LocalDate with = date.with(TemporalAdjusters.firstDayOfMonth());
        return LocalDateTime.of(with, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本月的结束日期
     *
     * @return
     */
    public static String getEndDayOfMonth() {
        LocalDate date = LocalDate.now();
        LocalDate with = date.with(TemporalAdjusters.lastDayOfMonth());
        return LocalDateTime.of(with, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取指定某月的开始日期
     *
     * @param year
     * @param month
     * @return
     */
    public static String getMonthStartOneDay(int year, int month) {
//        LocalDate localDate = LocalDate.now();
        return LocalDateTime.of(LocalDate.of(year, month, 1), LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy" +
                "-MM-dd HH:mm:ss"));
    }

    /**
     * 获取指定月的结束日期
     *
     * @param year
     * @param month
     * @return
     */
    public static String getMonthEndTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atEndOfMonth();
        return LocalDateTime.of(localDate, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本年开始日期
     *
     * @return
     */
    public static String getOneDayOfYear() {
        LocalDate date = LocalDate.now();
        LocalDate with = date.with(TemporalAdjusters.firstDayOfYear());
        return LocalDateTime.of(with, LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取本年度结束日期
     *
     * @return
     */
    public static String getEndDayOfYear() {
        LocalDate date = LocalDate.now();
        LocalDate with = date.with(TemporalAdjusters.lastDayOfYear());
        return LocalDateTime.of(with, LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 字符串转LocalDateTime
     *
     * @param str
     * @return
     */
    public static LocalDateTime strToLDT(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(str, formatter);
        return localDateTime;
    }

    /**
     * 字符串转LocalDateTime
     *
     * @param str
     * @param pattern
     * @return
     */
    public static LocalDateTime strToLDT(String str, String pattern) {
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime;
        if ("yyyy-MM-dd".equals(pattern)) {
            localDateTime = LocalDate.parse(str, formatter).atStartOfDay();
        } else {
            localDateTime = LocalDateTime.parse(str, formatter);
        }
        return localDateTime;
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertDateToLDT(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param time
     * @return
     */
    public static Date convertLDTToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取指定日期的毫秒时间戳
     *
     * @param time
     * @return
     */
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的秒时间戳
     *
     * @param time
     * @return
     */
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取指定时间的指定格式
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String formatTime(LocalDateTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取指定时间的指定格式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatLocalDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param pattern
     * @return
     */
    public static String formatNow(String pattern) {
        return formatTime(LocalDateTime.now(), pattern);
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLDT(long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 计算时间差
     *
     * @param endDate   最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(LocalDateTime endDate, LocalDateTime startTime) {
        // 获得两个时间的毫秒时间差异
        long diff = getMilliByTime(endDate) - getMilliByTime(startTime);
        return timeDistance(diff);
    }

    /**
     * 根据毫秒时间差异返回时间差说明
     *
     * @param diff
     * @return
     */
    public static String timeDistance(long diff) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        if ((diff % nd % nh % nm / ns) > 0) {
            min += 1;
        }
        // 计算差多少秒//输出结果
//        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 计算两个时间相差天数
     *
     * @param endDate
     * @param startTime
     * @return
     */
    public static long computeDiffDay(LocalDateTime endDate, LocalDateTime startTime) {
        // 获得两个时间的毫秒时间差异
        long diff = getMilliByTime(endDate) - getMilliByTime(startTime);

        long nd = 1000 * 24 * 60 * 60;

        // 计算差多少天
        long day = diff / nd;

        return day;
    }
}
