package com.zemcho.guzhe.controller.wechat.screen.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 屏幕店位租用参数
 */
@Data
public class ScreenRentalRentParam {
    @NotNull(message = "商超id为空")
    private Integer businessCircleId;

    @NotNull(message = "商家id为空")
    private Integer shopId;

    @NotNull(message = "展示内容类型为空")
    private Integer displayType;

    @Valid
    @NotEmpty(message = "租用设备为空")
    private List<ScreenRentalSelectionParam> selections;

    private String remark = "";
}
