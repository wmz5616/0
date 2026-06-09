package com.zemcho.ddql.controller.wechat.index.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.entity.personalCenter.Region;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * @title: ExchangeOrderAddressVo
 * @Description:
 * @Date: 2025/10/13 20:18
 */
@Data
public class ExchangeOrderAddressVo {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 用户收货地址记录id
     */
    private Integer addressId;

    /**
     * 收货人名称
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 地区id
     */
    private Integer regionId;

    /**
     * 地区信息
     */
    LinkedList<Region> regionList;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货地址的经纬度
     */
    private String location;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
