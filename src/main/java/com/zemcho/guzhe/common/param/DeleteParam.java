package com.zemcho.guzhe.common.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Ryan
 * @title: DeleteParam
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/9/23 0023 15:43
 */
@Data
public class DeleteParam {
    @NotNull(message = "参数{deleteIds}为空")
    @NotEmpty(message = "参数{deleteIds}为空!")
    private Set<Integer> deleteIds;

    //展示类型 0-首页轮banner图，1-首页快捷入口
    private Integer bannerType;
}
