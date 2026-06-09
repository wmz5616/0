package com.zemcho.ddql.entity.personalCenter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopOrderDetail {
    /**
     * 明细ID
     */
    private Integer id;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单价，单位：分
     */
    private Integer unitPrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 行总金额，单位：分
     */
    private Integer totalAmount;

    /**
     * 该行抵扣金币数
     */
    private Integer deductCoin;

    /**
     * 该行抵扣金币金额，单位：分
     */
    private Integer deductAmount;

    /**
     * 该行实付金额，单位：分
     */
    private Integer payAmount;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

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
