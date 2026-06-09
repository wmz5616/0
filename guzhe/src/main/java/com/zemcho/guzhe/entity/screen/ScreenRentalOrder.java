package com.zemcho.guzhe.entity.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 屏幕店位租用订单
 */
@Data
public class ScreenRentalOrder {
    // 订单id
    private Long id;

    // 订单编号
    private String orderNo;

    // 设备ID
    private Integer equipmentId;

    // 商家ID
    private Integer shopId;

    // 商家名称
    private String shopName;

    // 商超ID
    private Integer businessCircleId;

    // 商超名称
    private String businessCircleName;

    // 订单总金额
    private Integer totalAmount;

    // 下单时间/支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;

    // 下单用户id
    private Integer userId;

    // 下单手机号
    private String phone;

    // 下单用户昵称
    private String nickName;

    // 展示内容类型：1-商品 2-海报
    private Integer displayType;

    // 订单状态：0-待确认(未审核) 1-待生效(已审核未到租期) 2-生效中(已审核租期生效中) 3-已完成(租期全部结束) 4-已驳回(审核不通过) 5-已撤销(订单已撤销)
    private Integer status;

    // 备注/驳回原因
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
