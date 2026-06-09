package com.zemcho.ddql.controller.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: ExchangeOrderRefundApplyListVo
 * @Description:
 * @Date: 2025/10/14 15:42
 */
@Data
public class ExchangeOrderRefundApplyListVo {
    // 主键id
    private Integer id;

    // 订单id
    private Integer orderId;

    // 订单编号
    private String orderNo;

    //下单用户id
    private Integer userId;

    //下单手机号
    private String phone;

    //下单用户昵称
    private String nickName;

    //商品id
    private Integer productId;

    //商品编号
    private String productNo;

    //商品名称
    private String productName;

    //规格
    private String specification;

    //单位
    private String unit;

    //是否是虚拟商品（0:否, 1:是）
    private Integer isVirtual;

    //兑换数量
    private Integer num;

    //支付总金额（金币）
    private Integer amount;

    // 退货申请原因
    private String reason;

    // 退货申请图片，多个用英文逗号隔开
    private String images;

    // 状态: 1审核中、2审核通过、3审核驳回
    private Integer status;

    // 审核管理员id
    private Integer adminId;

    // 审核管理员账号
    private String account;

    // 审核管理员姓名
    private String name;

    // 退货金额(金币)
    private Integer refundAmount;

    // 审核时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    // 审核说明
    private String auditRemark;

    // 申请时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
