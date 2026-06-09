package com.zemcho.guzhe.controller.wechat.shop.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * 结算记录列表返回
 */
@Data
public class SettlementRecordListVo {
    private Long totalCount = 0L;

    private String totalAmount = "0.00";

    private PageInfo<SettlementRecordItemVo> pageInfo;
}
