package com.zemcho.guzhe.controller.wechat.screen.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 单个设备租用选择项
 */
@Data
public class ScreenRentalSelectionParam {
    @NotNull(message = "设备id为空")
    private Integer equipmentId;

    @NotEmpty(message = "租用月份为空")
    private List<String> rentalMonths;
}
