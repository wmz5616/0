package com.zemcho.guzhe.controller.sys.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
public class NoticeSaveParam {
    private Integer id;

    //标题
    private String title;

    // 分类 1通知公告 2消息提醒
    private Integer type;

    // 是否发布 0否(不立即发布) 1是(立即发布)
    private Boolean isPublish;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    //正文内容
    private String content;

}