package com.zemcho.guzhe.controller.wechat.screenOrder.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 查询设备截图参数
 */
@Data
public class ScreenOrderScreenshotQueryParam {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 可选，不传默认返回该设备最新截图
     */
    private Long screenshotId;
}
