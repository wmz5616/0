package com.zemcho.ddql.controller.common.vo;

import lombok.Data;

/**
 * @title: BusinessCircleCommonVo
 * @Description:
 * @Date: 2025/10/16 10:48
 */
@Data
public class BusinessCircleCommonVo {
    // 主键ID
    private Integer id;

    // 商圈名称
    private String name;

    // 商圈经纬度 经度,维度
    private String location;

    // 商圈详细地址名称
    private String locationName;

    // 排序值，默认为0，数值越大排在前面
    private Integer sortOrder;

    // 启用状态 0禁用 1启用
    private Integer status = 1;
}
