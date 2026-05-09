package com.zemcho.guzhe.controller.sys.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeBannerParam {
    // id
    private Integer id;

    // 图片的url
    @NotBlank(message = "图片的url不能为空")
    private String banUrl;

    // 链接的类型 0小程序页面 1H5页面
    @NotNull(message = "链接的类型不能为空")
    private Integer type;

    // 跳转链接的url
    @NotBlank(message = "链接的地址不能为空")
    private String url;

    // 是否启用 0不启用 1启用(默认)
    private Integer status;

    // 排序字段
    private Integer sort;

    // 名称
    @NotBlank(message = "名称不能为空")
    private String name;

    // 别名
    private String nickName;

    //权限控制(0:全部用户(默认),1：管理员)
    private Integer SortType;


    //展示类型 0-首页轮banner图，1-首页快捷入口
    @NotNull(message = "展示类型不能为空")
    private Integer bannerType;
}
