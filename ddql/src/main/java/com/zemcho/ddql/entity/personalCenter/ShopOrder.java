package com.zemcho.ddql.entity.personalCenter;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopOrder {
    /**
     * 订单ID
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 设备ID
     */
    private Integer equipmentId;

    /**
     * 门店ID
     */
    private Integer shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 商圈ID
     */
    private Integer businessCircleId;

    /**
     * 商圈名称
     */
    private String businessCircleName;

    /**
     * 订单总金额，单位：分
     */
    private Integer totalAmount;

    /**
     * 抵扣金币数
     */
    private Integer deductCoin;

    /**
     * 抵扣金币金额，单位：分
     */
    private Integer deductAmount;

    /**
     * 实付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 下单时间/支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    /**
     * 下单用户ID
     */
    private Integer userId;

    /**
     * 下单手机号
     */
    private String phone;

    /**
     * 下单用户昵称
     */
    private String nickName;

    /**
     * 订单状态 1完成 2退款
     */
    private Integer status;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 分账状态：0未分账，1已分账
     */
    private Integer divideStatus;

    /**
     * 分账金额，单位：分
     */
    private Integer divideAmount;

    //通莞支付订单号
    private String upOrderId;

    /**
     * 支付成功时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 分账时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime divideTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
