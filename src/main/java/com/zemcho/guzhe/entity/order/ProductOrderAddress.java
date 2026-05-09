package com.zemcho.guzhe.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: ProductOrderAddress
 * @Description:
 * @Date: 2026/4/27 20:42
 */
@Data
public class ProductOrderAddress {
    // 主键id
    private Integer id;

    // 订单id
    private Integer orderId;

    // 用户收货地址记录id
    private Integer addressId = 0;

    // 收货人名称
    private String name = "";

    // 联系电话
    private String phone = "";

    // 地区id
    private Integer regionId = 0;

    // 收货地址
    private String address = "";

    // 收货地址的经纬度
    private String location = "";

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
