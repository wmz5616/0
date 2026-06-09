package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomePageBanner {
    // id
    private Integer id;
    // 图片的url
    private String pic;
    // 链接的类型 0小程序页面 1H5页面
    private Integer type;
    // 跳转链接的url
    private String url;
    // 是否启用 0不启用 1启用
    private Integer status;

    // 排序字段
    private Integer sort;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
