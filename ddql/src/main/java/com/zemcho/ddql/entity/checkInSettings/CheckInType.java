package com.zemcho.ddql.entity.checkInSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInType {

    /**
     * id
     */
    private Integer id;

    /**
     * 照片url的集合 以;号分隔
     */
    private String images;

    /**
     * 名称
     */
    private String name;

    /**
     * 别名
     */
    private String otherName;

    /**
     * 启用状态 0启用 1禁用
     */
    private Integer status;

    /**
     * 说明
     */
    private String instruction;

    // 排序值，升序
    private Integer sort;
}
