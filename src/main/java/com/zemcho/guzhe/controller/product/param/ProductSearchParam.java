package com.zemcho.guzhe.controller.product.param;

import com.zemcho.guzhe.common.param.SearchParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author HXH
 */
@Data
public class ProductSearchParam extends SearchParam {
    //商家id
    @NotNull(message = "商家id不能为空")
    private Integer shopId;
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
}
