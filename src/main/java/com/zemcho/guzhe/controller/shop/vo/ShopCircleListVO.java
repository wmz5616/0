package com.zemcho.guzhe.controller.shop.vo;

import lombok.Data;

/**
 * @title: ShopCircleListVO
 * @Description:
 * @Date: 2025/10/28 18:53
 */
@Data
public class ShopCircleListVO {
    // 商圈id
    private Integer circleId;

    // 店铺id
    private Integer shopId;

    // 商圈名称
    private String circleName;
}
