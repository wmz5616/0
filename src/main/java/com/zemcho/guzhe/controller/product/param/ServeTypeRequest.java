package com.zemcho.guzhe.controller.product.param;

import lombok.Data;

import java.util.List;

@Data
public class ServeTypeRequest {
    private List<CategoryParam> categoryList;
    private Integer shopId;
}
