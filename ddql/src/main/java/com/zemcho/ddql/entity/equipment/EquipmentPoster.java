package com.zemcho.ddql.entity.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备海报关联表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPoster {

    /**
     * id
     */
    private Integer id;

    /**
     * 关联的设备 id
     */
    private Integer equipmentId;

    /**
     * 图片的url
     */
    private String image;

    /**
     * 投放状态 0待投放 1正常 2已过期
     */
    private Integer status;

    /**
     * 展示时间 单位秒
     */
    private Integer showTime;

    /**
     * 投放的开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime showBeginTime;

    /**
     * 投放的结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime showEndTime;

    /**
     * 排序字段
     */
    private Integer sort;

}
