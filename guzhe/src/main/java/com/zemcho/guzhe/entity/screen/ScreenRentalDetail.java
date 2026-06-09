package com.zemcho.guzhe.entity.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 屏幕店位租用明细
 */
@Data
public class ScreenRentalDetail {
    // 明细ID
    private Long id;

    // 订单ID
    private Long orderId;

    // 设备ID
    private Integer equipmentId;

    // 商家ID
    private Integer shopId;

    // 商超ID
    private Integer businessCircleId;

    // 租用年份
    private Integer rentalYear;

    // 租用月份
    private Integer rentalMonth;

    // 占用店位数，固定为1
    private Integer slotCount;

    // 订单状态：0-待确认(未审核) 1-待生效(已审核未到租期) 2-生效中(已审核租期生效中) 3-已完成(租期全部结束) 4-已驳回(审核不通过) 5-已撤销(订单已撤销)
    private Integer status;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
