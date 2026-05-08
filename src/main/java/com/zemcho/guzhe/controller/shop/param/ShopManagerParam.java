package com.zemcho.guzhe.controller.shop.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店铺管理者参数类
 */
@Data
public class ShopManagerParam {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 对应的商家
     */
    @NotNull(message = "商家ID不能为空")
    private Integer shopId;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 是否是店长 1是 0否
     */
    @NotNull(message = "店长标识不能为空")
    private Integer headManager;
}