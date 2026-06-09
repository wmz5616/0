package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: RechargeOrderParam
 * @Description:
 * @Date: 2025/10/10 19:45
 */
@Data
public class RechargeOrderParam {
    @NotNull(message = "团体id为空")
    private Integer teamId;

    @NotNull(message = "充值金额为空")
    private Integer amount;

    // 充值活动id
    private Integer actId = 0;
}
