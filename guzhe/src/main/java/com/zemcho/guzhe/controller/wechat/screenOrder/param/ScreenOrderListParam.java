package com.zemcho.guzhe.controller.wechat.screenOrder.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.common.param.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 店位订单查询参数
 */
@Data
public class ScreenOrderListParam extends PageParam {
    /**
     * 开始日期，默认当月第一天
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 结束日期，默认当月最后一天
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 所属商超名称，模糊筛选
     */
    private String businessCircleName;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 设备编号，模糊筛选
     */
    private String serialNumber;

    /**
     * 店铺ID（商家管理员名下店铺）
     */
    @NotNull(message = "shopId不能为空")
    private Integer shopId;
}
