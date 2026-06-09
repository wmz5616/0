package com.zemcho.guzhe.controller.screen_order.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * 后台店位订单列表返回
 */
@Data
public class ScreenOrderManageListVo {
    private Integer orderCount;

    private Integer totalAmount;

    private String totalAmountText;

    private PageInfo<ScreenOrderManageItemVo> pageInfo;
}
