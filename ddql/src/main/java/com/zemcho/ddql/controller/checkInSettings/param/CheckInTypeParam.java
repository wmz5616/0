package com.zemcho.ddql.controller.checkInSettings.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInTypeParam {
    /**
     * id
     */
    private Integer id;

    /**
     * 照片url的集合
     */
    @NotNull
    private List<String> images;

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 别名
     */
    private String otherName;

    /**
     * 启用状态 0启用 1禁用
     */
    private Integer status = 0;

    /**
     * 说明
     */
    private String instruction;
}
