package com.zemcho.guzhe.util.tgy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseData {

    // 响应编码 示例值：NIG00000	请求成功	NIG00001	必填参数有误	NIG00002	数据验证有误	NIG00003	并发请求报错	NIG99999	系统未知异常
    private String returnCode;
    // 响应描述 示例值：请求成功
    private String returnMsg;
    // 商户编号
    private String merchantNo;
    // 入网请求号（查询入网进度用）
    private String requestNo;
    // 申请单编号
    private String applicationNo;
    // 申请状态 REVIEWING:申请审核中 REVIEW_BACK:申请已驳回 AGREEMENT_SIGNING:协议待签署 BUSINESS_OPENING:业务开通中 COMPLETED:申请已完成
    private String applicationStatus;
    // “申请已驳回”或者“申请已完成”时，回传的审核意见
    private String auditOpinion;

}
