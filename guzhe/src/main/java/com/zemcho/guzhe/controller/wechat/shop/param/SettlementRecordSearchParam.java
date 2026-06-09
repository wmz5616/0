package com.zemcho.guzhe.controller.wechat.shop.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.common.param.PageParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 小程序商家端结算记录查询参数
 */
@Data
public class SettlementRecordSearchParam extends PageParam {
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
