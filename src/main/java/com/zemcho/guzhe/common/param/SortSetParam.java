package com.zemcho.guzhe.common.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@Data
public class SortSetParam {
    @NotNull(message = "ids不能为空")
    @NotEmpty(message = "ids不能为空!")
    private List<Integer> ids;
}
