package com.zemcho.guzhe.job.cron;

import cn.binarywang.wx.miniapp.bean.analysis.WxMaVisitTrend;
import com.zemcho.guzhe.entity.sys.DailyVisitTrend;
import com.zemcho.guzhe.mapper.sys.DailyVisitTrendMapper;
import com.zemcho.guzhe.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @title: WxDailyVisitTrendTask
 * @Description: 每日访问小程序数据统计任务
 * @Date: 2025/10/27 17:31
 */
@Component
public class WxDailyVisitTrendTask {
    @Autowired
    DailyVisitTrendMapper dailyVisitTrendMapper;

    /**
     * 每天晚上21点执行一次 (实际上是凌晨2点执行 0 0 2 * * ?)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        // 每天同步过去 3 天的数据，防止宕机导致漏拉
        for (int i = 1; i <= 3; i++) {
            LocalDate targetDate = LocalDateTime.now().minusDays(i).toLocalDate();
            syncDataForDate(targetDate);
        }
    }

    public void syncDataForDate(LocalDate targetDate) {
        // 转为java.util.Date 类型
        Date date = Date.from(targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Integer year = targetDate.getYear();
        String monthStr = YearMonth.from(targetDate).toString();

        List<WxMaVisitTrend> wxMaVisitTrendList = WechatUtil.getDailyVisitTrend(date, date);
        if (wxMaVisitTrendList != null && !wxMaVisitTrendList.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            // 创建 DateTimeFormatter 实例，指定格式为 yyyyMMdd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            List<DailyVisitTrend> data = new ArrayList<>();
            for (WxMaVisitTrend wxMaVisitTrend : wxMaVisitTrendList) {
                LocalDate refDate = LocalDate.parse(wxMaVisitTrend.getRefDate(), formatter);
                Long sessionCnt = wxMaVisitTrend.getSessionCnt();
                Long visitPv = wxMaVisitTrend.getVisitPv();
                Long visitUv = wxMaVisitTrend.getVisitUv();
                Long visitUvNew = wxMaVisitTrend.getVisitUvNew();

                DailyVisitTrend dailyVisitTrend = new DailyVisitTrend();
                dailyVisitTrend.setYear(year);
                dailyVisitTrend.setMonth(monthStr);
                dailyVisitTrend.setDate(refDate);
                dailyVisitTrend.setSessionCnt(sessionCnt);
                dailyVisitTrend.setVisitPv(visitPv);
                dailyVisitTrend.setVisitUv(visitUv);
                dailyVisitTrend.setVisitUvNew(visitUvNew);
                dailyVisitTrend.setCreatedAt(now);
                data.add(dailyVisitTrend);
            }

            dailyVisitTrendMapper.deleteByDate(targetDate);
            dailyVisitTrendMapper.insertAll(data);
        }
    }
}
