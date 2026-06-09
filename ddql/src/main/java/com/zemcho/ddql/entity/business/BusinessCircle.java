package com.zemcho.ddql.entity.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusinessCircle {
    // 主键ID
    private Integer id;

    // 商圈封面图URL
    private String coverImageUrl;

    // 商圈轮播图URL
    private String logoImageUrl;

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

    // 商圈介绍
    private String description;

    // 备注
    private String remark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
