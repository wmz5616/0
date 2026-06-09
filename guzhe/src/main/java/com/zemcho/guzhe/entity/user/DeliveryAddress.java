package com.zemcho.guzhe.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: DeliveryAddress
 * @Description:
 * @Date: 2026/4/27 13:56
 */
@Data
public class DeliveryAddress {
    /**
     * id
     */
    private Integer id;

    /**
     * 关联的用户id
     */
    private Integer userId;

    /**
     * 是否是默认 0否 1是
     */
    private Integer isDefault;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
