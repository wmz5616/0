package com.zemcho.ddql.controller.product.param;

import com.zemcho.ddql.common.param.SearchParam;
import lombok.Data;

@Data
public class ProductSearchParam extends SearchParam {
    // 商品编号
    private String productNo;

    // 商品名称
    private String name;

    // 库存状态（"1" 表示有库存，"0" 表示无库存）
    private Integer stockStatus;

    // 商品分类 ID
    private Integer categoryId;

    // 上架状态
    private Integer status;

    // 是否为虚拟商品
    private Integer isVirtual;

    // 库存数量（具体数值）
    private Integer stock;

    // 支付方式 0 金币 1组合 2现金
    private Integer payWay;

}
