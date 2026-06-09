package com.zemcho.guzhe.controller.cas.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.common.param.PageParam;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @title: UserCoinLogParam
 * @Description:
 * @Date: 2025/10/9 11:25
 */
@Data
public class UserCoinLogParam extends PageParam {
    // 类型：1打卡、2充值、3兑换、4提现、5兑换退货、6步数达标
    private Integer txnType;

    // 类型：1打卡、2充值、3兑换、4提现、5兑换退货、6步数达标
    private List<Integer> txnTypes;

    // 对应类型的记录id
    private Integer txnId;

    //币类型：1健康币、2金币
    private Integer coinType;

    //变更类型：1增加、2减少
    private Integer numType;

    // 用户id
    private Integer userId;

    private String keyword;

    private String email;

    //团队id
    private Integer teamId;

    //团队ids
    private List<Integer> teamIds;

    private Boolean onlySelf = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
