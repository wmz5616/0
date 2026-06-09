package com.zemcho.guzhe.controller.screen_order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台店位订单审核参数
 */
@Data
public class ScreenOrderManageAuditParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 审核结果：1-确认 2-驳回
     */
    @NotNull(message = "审核结果不能为空")
    private Integer result;

    /**
     * 审核意见/驳回原因
     */
    private String remark;

    /**
     * 上传文件URL
     */
    private String fileUrl;
}
