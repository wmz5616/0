package com.zemcho.ddql.controller.wechat.personalCenter.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WechatShopOrderInfoVo {
    private Integer id;
    private String orderNo;
    private Integer equipmentId;
    private Integer shopId;
    private String shopName;
    private Integer businessCircleId;
    private String businessCircleName;
    private Integer totalAmount;
    private Integer deductCoin;
    private Integer deductAmount;
    private Integer payAmount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    private Integer userId;
    private String phone;
    private String nickName;
    private Integer status;
    private String refundReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundTime;
    private String remark;
    private List<WechatShopOrderDetailVo> detailList;
}
