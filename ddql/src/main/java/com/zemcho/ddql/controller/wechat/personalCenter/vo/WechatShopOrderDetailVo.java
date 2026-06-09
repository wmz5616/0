package com.zemcho.ddql.controller.wechat.personalCenter.vo;

import lombok.Data;

@Data
public class WechatShopOrderDetailVo {
    private Integer id;
    private Integer orderId;
    private String orderNo;
    private String itemName;
    private String specName;
    private String unit;
    private Integer unitPrice;
    private Integer quantity;
    private Integer totalAmount;
    private Integer deductCoin;
    private Integer deductAmount;
    private Integer payAmount;
    private Integer sort;
    private String remark;
}
