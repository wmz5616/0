package com.zemcho.guzhe.entity.product;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {
    // 主键ID
    private Integer id;

    // 商品分类名称
    private String name;

    // 排序
    private Integer sort;

    //商家id
    private Integer shopId;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 删除时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;
}
