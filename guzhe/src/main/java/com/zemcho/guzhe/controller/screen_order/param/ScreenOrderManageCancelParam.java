package com.zemcho.guzhe.controller.screen_order.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台店位订单撤销参数
 */
@Data
public class ScreenOrderManageCancelParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "撤销原因不能为空")
    private String cancelReason;

    /**
     * 上传文件URL
     */
    private String fileUrl;
}
