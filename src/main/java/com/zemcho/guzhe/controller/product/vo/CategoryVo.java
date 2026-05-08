package com.zemcho.guzhe.controller.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVo {
    // 主键ID
    private Integer id;

    // 商品ID
    private Integer productId;

    // 商品分类名称
    private String name;

    // 排序
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
