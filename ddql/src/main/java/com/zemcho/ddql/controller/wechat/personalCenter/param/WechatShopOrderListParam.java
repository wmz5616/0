package com.zemcho.ddql.controller.wechat.personalCenter.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.common.param.PageParam;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WechatShopOrderListParam extends PageParam {
    private String shopName;
    private Integer status;

    /**
     * 开始日期，格式：yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    /**
     * 结束日期，格式：yyyy-MM-dd
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 查询开始时间，服务层按开始日期补齐为 00:00:00
     */
    private LocalDateTime queryStartTime;

    /**
     * 查询结束时间，服务层按结束日期补齐为 23:59:59
     */
    private LocalDateTime queryEndTime;
}
