package com.zemcho.guzhe.controller.log.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.common.param.PageParam;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: LogParam
 * @Description:
 * @Date: 2024/1/24 10:08
 */
@Data
public class LogParam extends PageParam {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String module;

    private String description;

    private String keyword;

    private String ip;
}
