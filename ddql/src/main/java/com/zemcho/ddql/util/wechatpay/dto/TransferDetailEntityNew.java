package com.zemcho.ddql.util.wechatpay.dto;

import com.google.gson.annotations.SerializedName;
import com.wechat.pay.java.core.cipher.PrivacyDecryptor;

import java.util.Objects;

/**
 * 商户单号查询转账单实体类信息
 */
public class TransferDetailEntityNew {
    /**
     * 商户号 Y 说明：微信支付分配的商户号
     */
    @SerializedName("mch_id")
    private String mchId;

    /**
     * 商户单号 Y 说明：商户系统内部的商家单号，要求此参数只能由数字、大小写字母组成，在商户系统内部唯一
     */
    @SerializedName("out_bill_no")
    private String outBillNo;

    /**
     * 商家转账订单号 Y 说明：商家转账订单的主键，唯一定义此资源的标识
     */
    @SerializedName("transfer_bill_no")
    private String transferBillNo;

    /**
     * 商户appid Y 说明：申请商户号的appid或商户号绑定的appid（企业号corpid即为此appid）
     */
    @SerializedName("appid")
    private String appid;

    /**
     * 单据状态 Y 说明：单据状态
     */
    @SerializedName("state")
    private String state;

    /**
     * 转账金额 Y 说明：转账金额单位为“分”。
     */
    @SerializedName("transfer_amount")
    private Integer transferAmount;

    /**
     * 转账备注 Y 说明：转账备注，用户收款时可见该备注信息，UTF8编码，最多允许32个字符。
     */
    @SerializedName("transfer_remark")
    private String transferRemark;

    /**
     * 失败原因 N 说明：订单已失败或者已退资金时，返回失败原因。
     */
    @SerializedName("fail_reason")
    private String failReason;

    /**
     * 收款用户OpenID Y 说明：商户AppID下，某用户的OpenID
     */
    @SerializedName("openid")
    private String openid;

    /**
     * 收款用户姓名 N 说明：收款方真实姓名。需要加密传入，支持标准RSA算法和国密算法，公钥由微信侧提供。
     * 转账金额 >= 2,000元时，该笔明细必须填写
     * 若商户传入收款用户姓名，微信支付会校验收款用户与输入姓名是否一致，并提供电子回单
     */
    @SerializedName("user_name")
    private String userName;

    /**
     * 单据创建时间 N 说明：单据受理成功时返回，按照使用rfc3339所定义的格式，格式为yyyy-MM-DDThh:mm:ss+TIMEZONE
     */
    @SerializedName("create_time")
    private String createTime;

    /**
     * 最后一次状态变更时间 N 说明：单据最后更新时间，按照使用rfc3339所定义的格式，格式为yyyy-MM-DDThh:mm:ss+TIMEZONE
     */
    @SerializedName("update_time")
    private String updateTime;

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

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

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Integer transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferRemark() {
        return transferRemark;
    }

    public void setTransferRemark(String transferRemark) {
        this.transferRemark = transferRemark;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransferDetailEntityNew that = (TransferDetailEntityNew) o;
        return Objects.equals(mchId, that.mchId) && Objects.equals(outBillNo, that.outBillNo) && Objects.equals(transferBillNo, that.transferBillNo) && Objects.equals(appid, that.appid) && Objects.equals(state, that.state) && Objects.equals(transferAmount, that.transferAmount) && Objects.equals(transferRemark, that.transferRemark) && Objects.equals(failReason, that.failReason) && Objects.equals(openid, that.openid) && Objects.equals(userName, that.userName) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mchId, outBillNo, transferBillNo, appid, state, transferAmount, transferRemark, failReason, openid, userName, createTime, updateTime);
    }

    @Override
    public String toString() {
        return "TransferDetailEntityNew{" +
                "mchId='" + mchId + '\'' +
                ", outBillNo='" + outBillNo + '\'' +
                ", transferBillNo='" + transferBillNo + '\'' +
                ", appid='" + appid + '\'' +
                ", state='" + state + '\'' +
                ", transferAmount=" + transferAmount +
                ", transferRemark='" + transferRemark + '\'' +
                ", failReason='" + failReason + '\'' +
                ", openid='" + openid + '\'' +
                ", userName='" + userName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }

    public TransferDetailEntityNew cloneWithCipher(PrivacyDecryptor encryptor) {
        TransferDetailEntityNew copy = new TransferDetailEntityNew();
        copy.mchId = mchId;
        copy.outBillNo = outBillNo;
        copy.transferBillNo = transferBillNo;
        copy.appid = appid;
        copy.state = state;
        copy.transferAmount = transferAmount;
        copy.transferRemark = transferRemark;
        copy.failReason = failReason;
        copy.openid = openid;
        if (userName != null && !userName.isEmpty()) {
            copy.userName = encryptor.decrypt(userName);
        }
        copy.createTime = createTime;
        copy.updateTime = updateTime;
        return copy;
    }
}
