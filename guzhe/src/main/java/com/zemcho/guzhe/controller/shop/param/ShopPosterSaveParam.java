package com.zemcho.guzhe.controller.shop.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @title: ShopPosterSaveParam
 * @Description:
 * @Date: 2026/5/7 9:06
 */
@Data
public class ShopPosterSaveParam {
    @NotNull(message = "商家id不能为空")
    private Integer shopId;

    @NotEmpty(message = "海报图片不能为空")
    private List<String> urlList;
}
