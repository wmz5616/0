package com.zemcho.guzhe.entity.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    private Integer id;

    /**
     * 设备编号（唯一）
     */
    private String serialNumber;

    /**
     * 所属商超ID
     */
    private Integer supermarketId;

    /**
     * 客服电话
     */
    private String contactPhone;

    /**
     * 设备状态：在线/离线
     */
    private Integer onlineStatus;

    /**
     * 启用状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 店位每月租金（分）
     */
    private Integer money;

    /**
     * 排序（数值大的排前面）
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    //当前版本id
    private Integer versionId;

    //版本序列号
    private String versionSerialNumber;

    //版本release
    private Integer release;

    //设备最近一次请求时间，主要用于检测设备是否离线
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime appRequireTime;

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
}

