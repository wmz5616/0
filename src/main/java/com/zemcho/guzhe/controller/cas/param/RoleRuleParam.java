package com.zemcho.guzhe.controller.cas.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @title: RoleRuleParam
 * @Description:
 * @Date: 2025/5/7 17:13
 */
@Data
public class RoleRuleParam {
    @NotNull(message = "角色id不能为空")
    private Integer roleId;

    @NotNull(message = "菜单id为空")
    @NotEmpty(message = "菜单id不能为空")
    private List<Integer> ruleIds;
}
