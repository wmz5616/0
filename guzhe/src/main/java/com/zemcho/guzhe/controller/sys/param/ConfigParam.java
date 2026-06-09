package com.zemcho.guzhe.controller.sys.param;

import com.zemcho.guzhe.entity.sys.Config;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * @title: ConfigParam
 * @Description:
 * @Date: 2024/1/24 8:52
 */
@Data
public class ConfigParam {
    @NotEmpty(message = "配置信息不能为空")
    private List<Config> configData;
}
