package com.zemcho.ddql.entity.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IndustryCategory {

    private Integer id;

    @NotBlank(message = "行业类别不能为空")
    private String name;

    private Integer sort;

}
