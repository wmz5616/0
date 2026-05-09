package com.zemcho.guzhe.controller.supermarket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SupermarketInfoVo {
    /**
     * 商超id
     */
    private Integer id;

    /**
     * 封面图
     */
    private String coverImage;

    private String logoImage;
    /**
     * 商超名称
     */
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 商超描述
     */
    private String description;

    /**
     * 商超营业时间
     */
    private String businessHours;

    /**
     * 商超地址
     */
    private String address;

    /**
     * 商超经度
     */
    private String longitude;

    /**
     * 商超纬度
     */
    private String latitude;

    /**
     * 启用状态 0禁用 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;

}
