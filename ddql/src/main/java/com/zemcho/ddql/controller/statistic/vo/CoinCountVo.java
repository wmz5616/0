package com.zemcho.ddql.controller.statistic.vo;

import lombok.Data;

/**
 * @title: CoinCountVo
 * @Description:
 * @Date: 2025/11/7 17:35
 */
@Data
public class CoinCountVo {
    //币类型：1健康币、2金币
    private Integer coinType;

    //币数量
    private Integer coinNum;
}
