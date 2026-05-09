package com.zemcho.guzhe.common.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @title: AuditParam
 * @Description:
 * @Date: 2024/2/23 11:06
 */
@Data
public class AuditParam {
    @NotNull(message = "参数{auditId}为空")
    private Integer auditId;

    private Boolean status = true;

    private String remark;
}
