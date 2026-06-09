package com.zemcho.guzhe.controller.app.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设备截图保存参数
 */
@Data
public class AppEquipmentScreenshotSaveParam {
    @NotNull(message = "设备截图ID不能为空")
    private Long screenshotId;

    /**
     * base64 图片数据，失败时可不传
     */
    private String base64Image;

    /**
     * 截图状态 0-待截图 1-成功 2-失败
     */
    @NotNull(message = "截图状态不能为空")
    private Integer screenshotStatus;

    /**
     * 截图失败原因，可选
     */
    private String failReason;
}
