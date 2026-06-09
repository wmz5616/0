package com.zemcho.ddql.util.wechatpay.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * 转账场景报备信息实体类
 */
public class TransferSceneReportInfoNew {
    /**
     * 信息类型 Y 说明：请根据产品文档确认当前转账场景下需传入的信息类型，需按要求填入，有多个字段时需填写完整
     * 如：转账场景为1000-现金营销，需填入活动名称、奖励说明
     */
    @SerializedName("info_type")
    private String infoType;

    /**
     * 信息内容 Y 说明：请根据信息类型，描述当前这笔转账单的转账背景
     * 如：
     * 信息类型为活动名称，请在信息内容描述用户参与活动的名称，如新会员有礼
     * 信息类型为奖励说明，请在信息内容描述用户因为什么奖励获取这笔资金，如注册会员抽奖一等奖
     */
    @SerializedName("info_content")
    private String infoContent;

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfoContent() {
        return infoContent;
    }

    public void setInfoContent(String infoContent) {
        this.infoContent = infoContent;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransferSceneReportInfoNew that = (TransferSceneReportInfoNew) o;
        return Objects.equals(infoType, that.infoType) && Objects.equals(infoContent, that.infoContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infoType, infoContent);
    }

    @Override
    public String toString() {
        return "TransferSceneReportInfo{" +
                "infoType='" + infoType + '\'' +
                ", infoContent='" + infoContent + '\'' +
                '}';
    }

    public TransferSceneReportInfoNew() {
        super();
    }
}
