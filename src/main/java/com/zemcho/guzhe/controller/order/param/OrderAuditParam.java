package com.zemcho.guzhe.controller.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: OrderAuditParam
 * @Description:
 * @Date: 2026/05/03 15:16
 */
@Data
public class OrderAuditParam {
    @NotNull(message = "申请id为空")
    private Integer applyId;

    @NotNull(message = "审核结果为空")
    private Integer status;

    private Integer refundAmount = 0;

    private String remark = "";
}
