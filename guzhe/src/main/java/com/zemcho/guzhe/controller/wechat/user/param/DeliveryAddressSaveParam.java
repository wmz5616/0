package com.zemcho.guzhe.controller.wechat.user.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @title: DeliveryAddressSaveParam
 * @Description:
 * @Date: 2026/4/27 14:15
 */
@Data
public class DeliveryAddressSaveParam {
    private Integer id;

    /**
     * 是否是默认 0否 1是
     */
    @NotNull(message = "默认标识为空")
    private Integer isDefault;

    /**
     * 收货人名称
     */
    @NotBlank(message = "收货人名称为空")
    private String name;

    /**
     * 联系电话
     */
    @NotBlank(message = "手机号码为空")
    private String phone;

    /**
     * 地区id
     */
    @NotNull(message = "请选择地区")
    private Integer regionId;

    /**
     * 收货地址
     */
    @NotBlank(message = "详细地址为空")
    private String address;

    /**
     * 收货地址的经纬度
     */
    @NotBlank(message = "请选择收货地址")
    private String location;
}
