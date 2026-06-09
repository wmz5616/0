package com.zemcho.guzhe.controller.product.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryParam {
    // 主键id
    private Integer id = 0;

    // 项目类型名称
    @NotNull(message = "分类名称不能为空")
    private String name;

    // 项目类型排序
    @NotNull(message = "排序不能为空")
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
