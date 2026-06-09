package com.zemcho.ddql.service.statistic.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInPlaceVo;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderCountVo;
import com.zemcho.ddql.controller.statistic.param.MonitorParam;
import com.zemcho.ddql.controller.statistic.vo.CoinCountVo;
import com.zemcho.ddql.controller.statistic.vo.PlaceCheckInCountVo;
import com.zemcho.ddql.controller.statistic.vo.PlaceCheckInRankVo;
import com.zemcho.ddql.controller.statistic.vo.VisitTrendStatVo;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInCountVo;
import com.zemcho.ddql.entity.equipment.Equipment;
import com.zemcho.ddql.mapper.cas.CasUserCheckInRecordMapper;
import com.zemcho.ddql.mapper.cas.CasUserCoinLogMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.sys.DailyVisitTrendMapper;
import com.zemcho.ddql.service.statistic.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @title: IMonitorService
 * @Description:
 * @Date: 2025/11/5 19:32
 */
@Service
public class IMonitorService implements MonitorService {
    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private DailyVisitTrendMapper dailyVisitTrendMapper;

    @Autowired
    private CasUserCheckInRecordMapper casUserCheckInRecordMapper;

    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private CheckInPlaceMapper checkInPlaceMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    /**
     * 系统数据统计
     *
     * @param param
     * @return
     */
    @Override
    public Result systemStat(MonitorParam param) {
        Result checkResult = MonitorParam.checkTimeParam(param);
        if (!checkResult.success()) {
            return checkResult;
        }

        LocalDateTime startTime = param.getStartTime();
        LocalDateTime endTime = param.getEndTime();

        //统计打卡数据
        SearchParam checkInCountParam = new SearchParam();
        checkInCountParam.setSearchIntStatus(2);
        checkInCountParam.setStartTime(startTime);
        checkInCountParam.setEndTime(endTime);
        UserCheckInCountVo checkInStat = casUserCheckInRecordMapper.selectCount(checkInCountParam);

        //统计金币获取量
        UserCoinLogParam coinLogParam = new UserCoinLogParam();
        coinLogParam.setCoinType(2);
        coinLogParam.setNumType(1);
        coinLogParam.setStartTime(startTime);
        coinLogParam.setEndTime(endTime);
        List<CoinCountVo> coinCountList = casUserCoinLogMapper.selectCount(coinLogParam);
        CoinCountVo coinStat = new CoinCountVo();
        coinStat.setCoinType(2);
        coinStat.setCoinNum(0);
        if (coinCountList != null && coinCountList.size() > 0) {
            coinStat = coinCountList.get(0);
        }

        //统计商品兑换数据
        SearchParam exchangeOrderCountParam = new SearchParam();
        exchangeOrderCountParam.setSearchStatusList(Arrays.asList(1, 2));
        exchangeOrderCountParam.setStartTime(startTime);
        exchangeOrderCountParam.setEndTime(endTime);
        ExchangeOrderCountVo exchangeOrderStat = exchangeOrderMapper.selectCount(exchangeOrderCountParam);

        Map<String, Object> result = new HashMap<>();
        result.put("checkInStat", checkInStat);
        result.put("coinStat", coinStat);
        result.put("exchangeOrderStat", exchangeOrderStat);

        return Result.success("获取成功", result);
    }

    /**
     * 平台用户统计
     *
     * @return
     */
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

    /**
     * 用户活跃度统计
     *
     * @return
     */
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

    /**
     * 平台流量情况
     *
     * @param param
     * @return
     */
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
        if ("week".equals(groupType)) {
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
        } else {
            return "week";
        }
    }

    /**
     * 场地打卡量排行榜
     *
     * @param param
     * @return
     */
    @Override
    public Result placeCheckInRankLists(MonitorParam param) {
        Result checkResult = MonitorParam.checkTimeParam(param);
        if (!checkResult.success()) {
            return checkResult;
        }

        LocalDateTime startTime = param.getStartTime();
        LocalDateTime endTime = param.getEndTime();

        //获取所有启用的场地
        SearchParam placeParam = new SearchParam();
        placeParam.setSearchIntStatus(0);
        List<CheckInPlaceVo> placeList = checkInPlaceMapper.select(placeParam);
        if (placeList == null || placeList.isEmpty()) {
            return Result.success("获取成功", new ArrayList<>());
        }

        //获取场地关联的设备
        List<Integer> placeIds = placeList.stream().map(m -> m.getId()).collect(Collectors.toList());
        List<Equipment> equipmentList = equipmentMapper.selectByPlaceIds(placeIds);
        Map<Integer, Equipment> equipmentMap = new HashMap<>();
        if (equipmentList != null && !equipmentList.isEmpty()) {
            equipmentMap = equipmentList.stream().collect(Collectors.toMap(m -> m.getCheckInPlaceId(), m -> m));
        }

        //统计场地打卡数据
        SearchParam checkInParam = new SearchParam();
        checkInParam.setSearchIntStatus(2);
        checkInParam.setStartTime(startTime);
        checkInParam.setEndTime(endTime);
        List<PlaceCheckInCountVo> checkInList = casUserCheckInRecordMapper.selectCountByPlace(checkInParam);
        Map<Integer, Integer> checkInMap = new HashMap<>();
        if (checkInList != null && !checkInList.isEmpty()) {
            checkInMap = checkInList.stream().collect(Collectors.toMap(m -> m.getPlaceId(), m -> m.getCheckInNum()));
        }

        List<PlaceCheckInRankVo> rankList = new ArrayList<>();
        for (CheckInPlaceVo place : placeList) {
            PlaceCheckInRankVo rankVo = new PlaceCheckInRankVo();
            rankVo.setPlaceId(place.getId());
            rankVo.setPlaceName(place.getName());
            rankVo.setCheckInTypeId(place.getCheckInTypeId());
            rankVo.setCheckInTypeName(place.getCheckInTypeName());

            Equipment equipmentInfo = equipmentMap.get(place.getId());
            if (equipmentInfo != null) {
                rankVo.setEquipmentId(equipmentInfo.getId());
                rankVo.setEquipmentName(equipmentInfo.getSerialNumber());
            }

            rankVo.setCheckInNum(checkInMap.getOrDefault(place.getId(), 0));

            rankList.add(rankVo);
        }

        // 根据打卡量进行排序
        rankList.sort((o1, o2) -> {
            // 首先按打卡量降序排列
            int coinCompare = Integer.compare(o2.getCheckInNum(), o1.getCheckInNum());
            if (coinCompare != 0) {
                return coinCompare;
            }
            // 打卡量相同的情况下，按场地id降序排列
            return Integer.compare(o2.getPlaceId(), o1.getPlaceId());
        });

        // 设置排名
        int rank = 1;
        int sameRankCount = 1;
        Integer previousCheckInNum = null;
        for (int i = 0; i < rankList.size(); i++) {
            PlaceCheckInRankVo current = rankList.get(i);

            if (i == 0) {
                // 第一个用户排名为1
                current.setRank(rank);
                previousCheckInNum = current.getCheckInNum();
            } else {
                // 如果打卡量都相同，则排名相同
                if (current.getCheckInNum().equals(previousCheckInNum)) {
                    current.setRank(rank);
                    sameRankCount++;
                } else {
                    // 不同则排名递增
                    rank += sameRankCount;
                    current.setRank(rank);
                    sameRankCount = 1;
                    previousCheckInNum = current.getCheckInNum();
                }
            }
        }

        return Result.success("获取成功", rankList);
    }
}
