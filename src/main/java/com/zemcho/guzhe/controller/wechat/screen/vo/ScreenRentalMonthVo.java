package com.zemcho.guzhe.controller.wechat.screen.vo;

import lombok.Data;

/**
 * 店位月份租用状态
 */
@Data
public class ScreenRentalMonthVo {
    private Integer year;

    private Integer month;

    private String monthKey;

    private String monthLabel;

    /**
     * 该月已被租用的店位数
     */
    private Integer usedCount;

    /**
     * 该月剩余可租用店位数
     */
    private Integer remainingCount;

    /**
     * 0-可租用 1-已租满
     */
    private Integer status;
}
