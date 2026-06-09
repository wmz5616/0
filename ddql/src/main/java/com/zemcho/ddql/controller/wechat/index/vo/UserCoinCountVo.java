package com.zemcho.ddql.controller.wechat.index.vo;

import lombok.Data;

/**
 * @title: UserCoinCountVo
 * @Description:
 * @Date: 2025/11/6 19:02
 */
@Data
public class UserCoinCountVo {
    // 用户id
    private Integer userId;

    //币类型：1健康币、2金币
    private Integer coinType;

    //币数量
    private Integer coinNum;
}
