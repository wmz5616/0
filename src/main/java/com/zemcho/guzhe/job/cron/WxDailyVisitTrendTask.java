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
     * 每天晚上21点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        //获取昨天的日期
        LocalDate yesterday = LocalDateTime.now().minusDays(2).toLocalDate();
        // 格式化为 yyyyMMdd
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String formattedDate = yesterday.format(formatter);
        // 转为java.util.Date 类型
        Date date = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Integer year = yesterday.getYear();
        String monthStr = YearMonth.from(yesterday).toString();

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

            dailyVisitTrendMapper.deleteByDate(yesterday);
            dailyVisitTrendMapper.insertAll(data);
        }
    }
}
