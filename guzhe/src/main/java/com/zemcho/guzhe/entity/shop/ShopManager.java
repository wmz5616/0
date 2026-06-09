package com.zemcho.guzhe.entity.shop;

import lombok.Data;

@Data
public class ShopManager {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 对应的商家
     */
    private Integer shopId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 是否是店长 1是 0否
     */
    private Integer headManager;
}