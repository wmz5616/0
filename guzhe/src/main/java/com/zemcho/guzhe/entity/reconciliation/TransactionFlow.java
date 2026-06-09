package com.zemcho.guzhe.entity.reconciliation;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionFlow {
    private Integer id;
    private Integer shopId; // 店铺ID
    private LocalDate billDate;
    private LocalDateTime transactionTime;
    private String orderNo;
    private String merchantOrderNo;
    private Long orderId; // 业务订单ID
    private Integer orderType; // 订单类型
    private Integer type; // 类型: 1收款, 2退款
    private Long amount; // 交易金额(分)
    private Long serviceFee; // 手续费(分)
    private String productName;
    private Integer totalQuantity;
    private Long userId; // 下单用户ID
    private String orderUser;
    private String orderUserPhone;
    private String remark;
    private String orderno; // 易宝订单号
    private String originMerchantOrderNo;
    private String originOrderno;
    private LocalDateTime createTime;

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getOrderno() {
        return this.orderno;
    }

    public void setOriginOrderno(String originOrderno) {
        this.originOrderno = originOrderno;
    }

    public String getOriginOrderno() {
        return this.originOrderno;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return this.orderNo;
    }
}
