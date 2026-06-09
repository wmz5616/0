package com.zemcho.ddql.util.wechatpay.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * 商家零钱转账自定义返回体
 */
public class InitiateBatchTransferResponseNew {
    /**
     * 商户单号 Y 说明：商户系统内部的商家单号，要求此参数只能由数字、大小写字母组成，在商户系统内部唯一
     */
    @SerializedName("out_bill_no")
    private String outBillNo;

    /**
     * 微信转账单号 Y 说明：微信转账单号，微信商家转账系统返回的唯一标识
     */
    @SerializedName("transfer_bill_no")
    private String transferBillNo;

    /**
     * 单据创建时间 Y 说明：单据受理成功时返回，按照使用rfc3339所定义的格式，格式为yyyy-MM-DDThh:mm:ss+TIMEZONE
     */
    @SerializedName("create_time")
    private String createTime;

    /**
     * 收单据状态 Y 说明：商家转账订单状态
     */
    @SerializedName("state")
    private String state;

    /**
     * 失败原因 Y 说明：订单已失败或者已退资金时，返回失败原因“分”。
     */
    @SerializedName("fail_reason")
    private String failReason;

    /**
     * 跳转领取页面的package信息 Y 说明：跳转微信支付收款页的package信息，APP调起用户确认收款或JSAPI调起用户确认收款需要使用的参数。
     */
    @SerializedName("package_info")
    private String packageInfo;

    public String getOutBillNo() {
        return outBillNo;
    }

    public void setOutBillNo(String outBillNo) {
        this.outBillNo = outBillNo;
    }

    public String getTransferBillNo() {
        return transferBillNo;
    }

    public void setTransferBillNo(String transferBillNo) {
        this.transferBillNo = transferBillNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InitiateBatchTransferResponseNew that = (InitiateBatchTransferResponseNew) o;
        return Objects.equals(outBillNo, that.outBillNo) && Objects.equals(transferBillNo, that.transferBillNo) && Objects.equals(createTime, that.createTime) && Objects.equals(state, that.state) && Objects.equals(failReason, that.failReason) && Objects.equals(packageInfo, that.packageInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outBillNo, transferBillNo, createTime, state, failReason, packageInfo);
    }

    @Override
    public String toString() {
        return "InitiateBatchTransferResponse{" +
                "outBillNo='" + outBillNo + '\'' +
                ", transferBillNo='" + transferBillNo + '\'' +
                ", createTime='" + createTime + '\'' +
                ", state='" + state + '\'' +
                ", failReason=" + failReason +
                ", packageInfo='" + packageInfo + '\'' +
                '}';
    }
}
