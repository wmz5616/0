package com.zemcho.guzhe.entity.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperateLog {
    private Integer id;

    private Integer adminId;

    private String name;

    private String account;

    private String module;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    private String ip;

    private String description;

    private String api;

    private String param;

    private String result;

    private String type;
}