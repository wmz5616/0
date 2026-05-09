package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @title: Config
 * @Description:
 * @Date: 2024/1/23 17:16
 */
@Data
public class Config {
    private Integer id;

    private String key;

    private String value;

    private String remark;

    // 类型：1基础配置、2设备配置
    private Integer type;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Timestamp updateTime;
}
