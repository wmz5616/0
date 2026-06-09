package com.zemcho.guzhe.controller.wechat.screenOrder.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发起设备截图任务参数
 */
@Data
public class ScreenOrderScreenshotTaskParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
