package com.zemcho.ddql.controller.business.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BusinessCircleParam {
    // 主键ID
    private Integer id;

    // 商圈封面图URL
    private String coverImageUrl;

    // 商圈轮播图URL
    private List<String> logoImageUrl;

    // 商圈名称
    @NotNull(message = "名称不能为空")
    private String name;

    // 商圈经纬度 经度,维度
    @NotNull(message = "经纬度不能为空")
    private String location;

    // 商圈详细地址名称
    private String locationName;

    // 排序值，默认为0，数值越大排在前面
    private Integer sortOrder;

    // 启用状态 0禁用 1启用
    @NotNull(message = "启用状态不能为空")
    private Integer status = 1;

    // 商圈介绍
    @NotNull(message = "介绍不能为空")
    private String description;

    // 备注
    private String remark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
