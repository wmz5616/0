package com.zemcho.guzhe.entity.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HXH
 */
@Data
public class ShopAudit {
    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 申请用户ID，关联 cas_user 表
     * 标识是哪个用户提交的入驻申请
     */
    private Integer userId;

    /**
     * 店铺封面图URL
     * 用于展示店铺的主封面图片地址
     */
    private String coverImageUrl;

    /**
     * 店铺轮播图URL（JSON格式字符串）
     * 存储多张轮播图地址，如：["url1","url2","url3"]
     * 审核通过后会原样迁移到 shop 表
     */
    private String galleryImages;

    /**
     * 店铺名称
     * 需要保证唯一性，会与 shop 表和 shop_audit 表进行双重校验
     */
    private String name;

    /**
     * 店铺经纬度
     * 格式：经度,纬度（如：116.4074,39.9042）
     * 用于地图定位和距离计算
     */
    private String location;

    /**
     * 店铺详细地址
     * 省市区+街道门牌号等完整地址信息
     */
    private String address;

    /**
     * 店铺联系人姓名
     * 通常为店长或负责人姓名
     */
    private String userName;

    /**
     * 店铺联系电话
     * 店长或负责人的手机号码，用于联系沟通
     */
    private String phone;

    /**
     * 营业开始时间
     * 格式：HH:mm:ss（如：09:00:00）
     */
    private String startTime;

    /**
     * 营业结束时间
     * 格式：HH:mm:ss（如：22:00:00）
     */
    private String endTime;

    /**
     * 客服电话
     * 店铺对外提供的客服咨询电话
     */
    private String customerPhone;

    /**
     * 客服微信二维码图片URL
     * 用户可扫码添加客服微信的图片地址
     */
    private String customerCodeImg;

    /**
     * 店铺介绍（富文本内容）
     * 支持HTML格式的详细介绍，展示在店铺详情页
     */
    private String description;

    // 资质认证状态 0未提交 1已通过 2已驳回
    private Integer qualificationCert;

    /**
     * 店铺启用状态 0禁用 1启用
     */
    private Integer status;
    /**
     * 推荐排序
     */
    private Integer recommendOrder;

    /**
     * 提交时间
     * 用户提交入驻申请的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;

    /**
     * 审核状态
     * 0 - 待审核：刚提交申请，等待后台审核
     * 1 - 已通过：审核通过，数据已迁移到 shop 表
     * 2 - 已驳回：审核不通过，可查看驳回原因
     */
    private Integer auditStatus;

    /**
     * 审核人ID
     * 执行审核操作的后台管理员ID，关联 cas_admin 表
     * 审核前为 null
     */
    private Integer auditUser;

    /**
     * 审核时间
     * 后台管理员执行审核操作的时间
     * 审核前为 null
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditTime;

    /**
     * 审核人电话
     * 执行审核操作的后台管理员账号/电话
     * 用于追溯审核责任人
     */
    private String auditPhone;

    /**
     * 驳回原因
     * 当 audit_status = 2（已驳回）时填写
     * 告知用户审核不通过的具体原因，方便修改后重新提交
     */
    private String rejectReason;

    /**
     * 审核通过后生成的店铺ID
     * 当 audit_status = 1 时，此字段存储迁移到 shop 表后生成的新店铺ID
     * 用于关联审核记录和正式店铺记录
     * 审核前和驳回时为 null
     */
    private Integer shopId;

    /**
     * 创建时间
     * 记录首次创建的时间，通常与 submitTime 相同
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 记录最后一次更新的时间
     * 数据库会自动更新（ON UPDATE CURRENT_TIMESTAMP）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
