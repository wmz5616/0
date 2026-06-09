package com.zemcho.guzhe.service.data.Impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.data.param.MonitorParam;
import com.zemcho.guzhe.controller.data.vo.BusinessEquipmentVO;
import com.zemcho.guzhe.controller.data.vo.VisitTrendStatVo;
import com.zemcho.guzhe.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.order.OrderMapper;
import com.zemcho.guzhe.mapper.sys.DailyVisitTrendMapper;
import com.zemcho.guzhe.service.data.MonitorService;
import com.zemcho.guzhe.util.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HXH
 */
@Service
public class IMonitorService implements MonitorService {

    @Autowired
    private CasUserMapper casUserMapper;
    @Autowired
    private DailyVisitTrendMapper dailyVisitTrendMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result userStat() {
        //总用户数（今日用户数）
        Integer totalNum = casUserMapper.countByTime(null);

        //昨日用户数
        LocalDateTime yesterdayTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        Integer yesterdayNum = casUserMapper.countByTime(yesterdayTime);

        Map<String, Integer> result = new HashMap<>();
        result.put("totalNum", totalNum);
        result.put("yesterdayNum", yesterdayNum);

        return Result.success("获取成功", result);
    }

    @Override
    public Result activeStat() {
        //统计总的浏览数
        Integer totalVisitPv = dailyVisitTrendMapper.countVisitPvByDate(null, null);
        totalVisitPv = totalVisitPv == null ? 0 : totalVisitPv;

        //统计本周浏览数
        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        Integer weekVisitPv = dailyVisitTrendMapper.countVisitPvByDate(startDate, endDate);
        weekVisitPv = weekVisitPv == null ? 0 : weekVisitPv;

        //统计上周浏览数
        startDate = LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(1);
        endDate = LocalDate.now().with(DayOfWeek.SUNDAY).minusWeeks(1);
        Integer lastWeekVisitPv = dailyVisitTrendMapper.countVisitPvByDate(startDate, endDate);
        lastWeekVisitPv = lastWeekVisitPv == null ? 0 : lastWeekVisitPv;

        Map<String, Integer> result = new HashMap<>();
        result.put("totalVisitPv", totalVisitPv);
        result.put("weekVisitPv", weekVisitPv);
        result.put("lastWeekVisitPv", lastWeekVisitPv);

        return Result.success("获取成功", result);

    }

    @Override
    public Result visitTrendStat(MonitorParam param) {
        Result checkResult = MonitorParam.checkTimeParam(param);
        if (!checkResult.success()) {
            return checkResult;
        }

        LocalDateTime startTime = param.getStartTime();
        LocalDateTime endTime = param.getEndTime();

        // 确定分组策略
        String groupType = determineGroupType(startTime, endTime);
        param.setTimeDimension(groupType);

        // 获取统计数据
        List<VisitTrendStatVo> statData = dailyVisitTrendMapper.selectVisitTrendStat(param);
        Map<String, VisitTrendStatVo> statMap = new HashMap<>();
        if (statData != null) {
            statMap = statData.stream().collect(Collectors.toMap(m -> m.getDataGroup(), m -> m));
        }

        // 补全缺失的日期数据
        List<VisitTrendStatVo> data = new ArrayList<>();
        if ("today".equals(groupType)) {
            // 按天补全（当日）
            LocalDate current = startTime.toLocalDate();
            LocalDate end = endTime.toLocalDate();
            while (!current.isAfter(end)) {
                String dateStr = current.toString();
                if (statMap.containsKey(dateStr)) {
                    data.add(statMap.get(dateStr));
                } else {
                    VisitTrendStatVo emptyData = new VisitTrendStatVo();
                    emptyData.setDataGroup(dateStr);
                    data.add(emptyData);
                }
                current = current.plusDays(1);
            }
        } else if ("week".equals(groupType)) {
            // 按天补全
            LocalDate current = startTime.toLocalDate();
            LocalDate end = endTime.toLocalDate();

            while (!current.isAfter(end)) {
                String dateStr = current.toString();
                if (statMap.containsKey(dateStr)) {
                    data.add(statMap.get(dateStr));
                } else {
                    VisitTrendStatVo emptyData = new VisitTrendStatVo();
                    emptyData.setDataGroup(dateStr);
                    data.add(emptyData);
                }
                current = current.plusDays(1);
            }
        } else if ("month".equals(groupType)) {
            // 按月补全
            YearMonth current = YearMonth.from(startTime);
            YearMonth end = YearMonth.from(endTime);

            while (!current.isAfter(end)) {
                String monthStr = current.toString();
                if (statMap.containsKey(monthStr)) {
                    data.add(statMap.get(monthStr));
                } else {
                    VisitTrendStatVo emptyData = new VisitTrendStatVo();
                    emptyData.setDataGroup(monthStr);
                    data.add(emptyData);
                }
                current = current.plusMonths(1);
            }
        } else if ("year".equals(groupType)) {
            // 按年补全
            int current = startTime.getYear();
            int end = endTime.getYear();

            while (current <= end) {
                String yearStr = String.valueOf(current);
                if (statMap.containsKey(yearStr)) {
                    data.add(statMap.get(yearStr));
                } else {
                    VisitTrendStatVo emptyData = new VisitTrendStatVo();
                    emptyData.setDataGroup(yearStr);
                    data.add(emptyData);
                }
                current++;
            }
        }

        // 重新排序
        data.sort((m1, m2) -> m1.getDataGroup().compareTo(m2.getDataGroup()));

        return Result.success("获取成功", data);
    }

    @Override
    public Result getOrderData() {
        // 统计今日订单数据
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        //统计今日订单数据
        BusinessDataVO todayData = orderMapper.selectSystemOrderData(todayStart, todayEnd);
        // 统计累计订单数据
        BusinessDataVO allData = orderMapper.selectSystemOrderData(null, null);

        // 处理空值
        if (todayData == null) {
            todayData = new BusinessDataVO();
            todayData.setOrderCount(0);
            todayData.setRevenue(BigDecimal.ZERO);
        }
        if (allData == null) {
            allData = new BusinessDataVO();
            allData.setOrderCount(0);
            allData.setRevenue(BigDecimal.ZERO);
        }

        Map<String, Object> result = new HashMap<>();
        // 今日订单数 (统计状态：待使用、待发货、已发货、已完成、退款中、已过期)
        result.put("todayOrderNum", todayData.getOrderCount() == null ? 0 : todayData.getOrderCount());
        // 今日总金额 (统计状态：待使用、待发货、已发货、已完成、退款中、已过期)
        result.put("todayTotalAmount", todayData.getRevenue());
        // 累计订单总数 (按支付算)
        result.put("allOrderNum", allData.getOrderCount());
        // 累计总金额
        result.put("allTotalAmount", allData.getRevenue());

        return Result.success("获取成功", result);
    }

    @Override
    public Result getBusinessEquipment(MonitorParam param) {
        Result checkResult = MonitorParam.checkTimeParam(param);
        if (!checkResult.success()) {
            return checkResult;
        }

        List<BusinessEquipmentVO> dataList = orderMapper.
                selectBusinessEquipment(param.getStartTime(), param.getEndTime());

        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        return Result.success("获取成功", dataList);
    }

    @Override
    public void businessExport(MonitorParam param, HttpServletResponse response) {
        try {
            // 参数为空时，默认按周导出
            if (param == null) {
                param = new MonitorParam();
                param.setTimeDimension("week");
            } else if (param.getTimeDimension() == null || param.getTimeDimension().isEmpty()) {
                param.setTimeDimension("week");
            }

            // 校验时间参数
            Result checkResult = MonitorParam.checkTimeParam(param);
            if (!checkResult.success()) {
                return;
            }

            // 获取终端经营情况数据
            List<BusinessEquipmentVO> dataList = orderMapper.selectBusinessEquipment(param.getStartTime(), param.getEndTime());

            if (dataList == null) {
                dataList = new ArrayList<>();
            }

            // 调试日志
            System.out.println("=== 导出数据调试 ===");
            System.out.println("查询时间范围: " + param.getStartTime() + " ~ " + param.getEndTime());
            System.out.println("数据条数: " + dataList.size());
            for (BusinessEquipmentVO vo : dataList) {
                System.out.println("设备: " + vo.getSerialNumber()
                        + ", 订单数: " + vo.getOrderNum()
                        + ", 待发货: " + vo.getPendingDeliveryNum()
                        + ", 已完成: " + vo.getCompletedNum()
                        + ", 收入: " + vo.getOrderIncome());
            }

            // 生成导出文件名：终端经营情况+导出时间
            String exportTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
            String fileName = "终端经营情况_" + exportTime;

            // 导出Excel
            ExcelUtil.exportToWeb(response, dataList, fileName, "终端经营情况", BusinessEquipmentVO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 确定分组类型
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String determineGroupType(LocalDateTime startTime, LocalDateTime endTime) {
        long weeks = java.time.temporal.ChronoUnit.DAYS.between(startTime, endTime) + 1;
        long months = java.time.temporal.ChronoUnit.MONTHS.between(startTime, endTime);
        long years = java.time.temporal.ChronoUnit.YEARS.between(startTime, endTime);

        if (years > 0) {
            return "year";
        } else if (months > 0) {
            return "month";
        } else if (weeks <= 1) {
            return "today";
        } else {
            return "week";
        }
    }
}