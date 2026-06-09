package com.zemcho.ddql.controller.statistic.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.common.Result;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @title: MonitorParam
 * @Description:
 * @Date: 2025/11/5 19:39
 */
@Data
public class MonitorParam {
    // "week", "month", "year", "custom"
    @NotNull(message = "请选择时间段")
    private String timeDimension;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    public static Result checkTimeParam(MonitorParam param) {
        LocalDateTime startTime = param.getStartTime();
        LocalDateTime endTime = param.getEndTime();
        String timeDimension = param.getTimeDimension();

        // 参数校验
        if ("custom".equals(timeDimension)) {
            if (startTime == null || endTime == null) {
                return Result.error("自定义时间范围必须指定开始时间和结束时间");
            }
            if (startTime.isAfter(endTime)) {
                return Result.error("开始时间不能大于结束时间");
            }
        } else {
            // 根据维度自动计算时间范围
            switch (timeDimension) {
                case "week":
                    startTime = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).with(LocalTime.MIN);
                    endTime = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).with(LocalTime.MAX);
                    break;
                case "month":
                    java.time.YearMonth currentMonth = java.time.YearMonth.now();
                    startTime = currentMonth.atDay(1).atStartOfDay();
                    endTime = currentMonth.atEndOfMonth().atTime(23, 59, 59);
                    break;
                case "year":
                    java.time.Year currentYear = java.time.Year.now();
                    startTime = currentYear.atDay(1).atStartOfDay();
                    endTime = currentYear.atMonth(12).atEndOfMonth().atTime(23, 59, 59);
                    break;
                default:
                    return Result.error("不支持的时间维度");
            }
        }

        param.setStartTime(startTime);
        param.setEndTime(endTime);

        return Result.success("success");
    }
}
