package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ryan
 */
@Data
public class Notice {
    // 主键
    private Integer id;

    // 文章标题
    private String title;

    // 正文内容
    private String content;

    // 类型 1通知公告 2消息提醒
    private Integer type;

    // 状态：1未发布，2已发布
    private Integer status;

    // 是否立即发布 0否 1是
    private Boolean isPublish;

    // 是否置顶 0否 1是
    private Boolean isTop;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    //删除时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;
}