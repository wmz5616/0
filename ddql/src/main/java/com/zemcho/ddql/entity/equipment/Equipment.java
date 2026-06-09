package com.zemcho.ddql.entity.equipment;

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

    /**
     * id
     */
    private Integer id;

    /**
     * 关联的打卡场所id
     */
    private Integer checkInPlaceId;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 在线状态 0在线 1离线
     */
    private Integer onlineStatus;

    /**
     * 启用状态 0启用 1禁用
     */
    private Integer enableStatus;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 排序字段
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
