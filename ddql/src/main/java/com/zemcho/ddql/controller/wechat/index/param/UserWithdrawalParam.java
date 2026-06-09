package com.zemcho.ddql.controller.wechat.index.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @title: UserWithdrawalParam
 * @Description:
 * @Date: 2025/10/10 9:03
 */
@Data
public class UserWithdrawalParam {
    @NotNull(message = "团体id为空")
    private Integer teamId;

    @NotNull(message = "提现金额为空")
    private Integer amount;
}
