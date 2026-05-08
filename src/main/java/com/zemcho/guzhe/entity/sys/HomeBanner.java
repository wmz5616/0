package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeBanner {
    // id
    private Integer id;

    // 图片的url
    private String banUrl;

    // 链接的类型 0小程序页面 1H5页面
    private Integer type;

    // 跳转链接的url
    private String url;

    // 是否启用 0不启用 1启用(默认)
    private Integer status;

    // 排序字段
    private Integer sort;

    // 名称
    private String name;

    // 别名
    private String nickName;

    //权限控制
    private Integer SortType;

    //展示类型 0-首页banner图，1-首页快捷入口
    private Integer bannerType;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
