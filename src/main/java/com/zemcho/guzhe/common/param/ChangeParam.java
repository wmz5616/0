package com.zemcho.guzhe.common.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

@Data
public class ChangeParam {
    @NotNull(message = "参数{changeIds}为空")
    @NotEmpty(message = "参数{changeIds}为空!")
    private Set<Integer> changeIds;

    private Integer status = 1;
}
