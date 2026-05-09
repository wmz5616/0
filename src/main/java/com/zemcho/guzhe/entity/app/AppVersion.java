package com.zemcho.guzhe.entity.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppVersion {

    /**
     * id
     */
    private Integer id;

    /**
     * 版本编号
     */
    private String serialNumber;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 排序字段
     */
    private Integer release;

    /**
     * 是否发布 0发布 1不发布
     */
    private Integer isPublish;

    /**
     * 名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;
}
