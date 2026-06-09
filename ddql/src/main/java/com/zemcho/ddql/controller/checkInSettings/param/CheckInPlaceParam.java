package com.zemcho.ddql.controller.checkInSettings.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInPlaceParam {
    /**
     * id
     */
    private Integer id;

    /**
     * 场所名称
     */
    private String name;

    /**
     * 场所地址
     */
    private String address;

    /**
     * 经纬度
     */
    private String location;

    /**
     * 打卡方式 0扫码打卡 1距离打卡
     */
    private Integer checkInMethod;

    /**
     * 打卡距离 单位米 距离打卡时需填写此字段
     */
    private Integer checkInDistance;

    /**
     * 关联的打卡类型id
     */
    private Integer checkInTypeId;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 启用状态 0启用 1禁用
     */
    private Integer status;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 场地介绍
     */
    private String introduction;

    /**
     * 场地照片的url集合 以;分隔
     */
    private List<String> images;


    /**
     * 场所的管理员的id
     */
    private List<Integer> userIds;

    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
