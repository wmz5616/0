package com.zemcho.guzhe.controller.wechat.shop.param;

import com.zemcho.guzhe.entity.shop.ShopManager;
import lombok.Data;

import java.util.List;

/**
 * 商家审核处理参数
 */
@Data
public class ShopAuditHandleParam {
    
    /**
     * 店铺封面图URL
     */
    private String coverImageUrl;
    
    /**
     * 店铺轮播图URL列表（JSON格式）
     */
    private List<String> galleryImages;
    
    /**
     * 店铺名称
     */
    private String name;
    
    /**
     * 店铺经纬度
     */
    private String location;
    
    /**
     * 店铺详细地址
     */
    private String address;
    
    /**
     * 店铺联系人姓名（店长）
     */
    private String userName;
    
    /**
     * 店铺联系电话（店长电话）
     */
    private String phone;
    
    /**
     * 营业开始时间
     */
    private String startTime;
    
    /**
     * 营业结束时间
     */
    private String endTime;
    
    /**
     * 客服电话
     */
    private String customerPhone;
    
    /**
     * 客服微信二维码图片URL
     */
    private String customerCodeImg;
    
    /**
     * 店铺介绍（富文本内容）
     */
    private String description;
    
    /**
     * 行业类别ID列表
     */
    private List<Integer> industryCategoryIds;
    
    /**
     * 商圈ID列表
     */
    private List<Integer> circleIds;
    
    /**
     * 商家管理人员列表
     */
    private List<ShopManager> managers;
}