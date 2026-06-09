package com.zemcho.ddql.util.wechatpay.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商家零钱转账自定义请求体
 */
public class InitiateBatchTransferRequestNew {
    /**
     * 商户appid Y 说明：申请商户号的appid或商户号绑定的appid（企业号corpid即为此appid）
     */
    @SerializedName("appid")
    private String appid;

    /**
     * 商户单号 Y 说明：商户系统内部的商家单号，要求此参数只能由数字、大小写字母组成，在商户系统内部唯一
     */
    @SerializedName("out_bill_no")
    private String outBillNo;

    /**
     * 转账场景ID Y 说明：该笔转账使用的转账场景，可前往“商户平台-产品中心-商家转账”中申请。
     */
    @SerializedName("transfer_scene_id")
    private String transferSceneId;

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
     * 通知地址 N 说明：异步接收微信支付结果通知的回调地址，通知url必须为公网可访问的url，必须为https，不能携带参数。
     */
    @SerializedName("notify_url")
    private String notifyUrl;

    /**
     * 转账场景报备信息 Y 说明：各转账场景下需报备的内容，可通过 产品文档 了解
     */
    @SerializedName("transfer_scene_report_infos")
    private List<TransferSceneReportInfoNew> transferSceneReportInfos = new ArrayList<>();

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOutBillNo() {
        return outBillNo;
    }

    public void setOutBillNo(String outBillNo) {
        this.outBillNo = outBillNo;
    }

    public String getTransferSceneId() {
        return transferSceneId;
    }

    public void setTransferSceneId(String transferSceneId) {
        this.transferSceneId = transferSceneId;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public List<TransferSceneReportInfoNew> getTransferSceneReportInfos() {
        return transferSceneReportInfos;
    }

    public void setTransferSceneReportInfos(List<TransferSceneReportInfoNew> transferSceneReportInfos) {
        this.transferSceneReportInfos = transferSceneReportInfos;
    }

    public InitiateBatchTransferRequestNew() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InitiateBatchTransferRequestNew that = (InitiateBatchTransferRequestNew) o;
        return Objects.equals(appid, that.appid) && Objects.equals(outBillNo, that.outBillNo) && Objects.equals(transferSceneId, that.transferSceneId) && Objects.equals(openid, that.openid) && Objects.equals(userName, that.userName) && Objects.equals(transferAmount, that.transferAmount) && Objects.equals(transferRemark, that.transferRemark) && Objects.equals(notifyUrl, that.notifyUrl) && Objects.equals(transferSceneReportInfos, that.transferSceneReportInfos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appid, outBillNo, transferSceneId, openid, userName, transferAmount, transferRemark, notifyUrl, transferSceneReportInfos);
    }

    @Override
    public String toString() {
        return "InitiateBatchTransferRequestNew{" +
                "appid='" + appid + '\'' +
                ", outBillNo='" + outBillNo + '\'' +
                ", transferSceneId='" + transferSceneId + '\'' +
                ", openid='" + openid + '\'' +
                ", userName='" + userName + '\'' +
                ", transferAmount=" + transferAmount +
                ", transferRemark='" + transferRemark + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", transferSceneReportInfos=" + transferSceneReportInfos +
                '}';
    }
}
