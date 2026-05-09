package com.zemcho.guzhe.controller.screen_order.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.common.param.PageParam;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台店位订单列表查询参数
 */
@Data
public class ScreenOrderManageListParam extends PageParam {
    /**
     * 支付开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 支付结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 设备编号，模糊查询
     */
    private String serialNumber;

    /**
     * 所属商超，模糊查询
     */
    private String businessCircleName;

    /**
     * 下单商家，模糊查询
     */
    private String merchantName;

    /**
     * 下单人关键字，支持姓名或手机号模糊查询
     */
    private String orderUserKeyword;

    /**
     * 订单号，模糊查询
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer status;
}
