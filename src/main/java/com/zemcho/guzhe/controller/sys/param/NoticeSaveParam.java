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
    @NotBlank(message = "标题不能为空")
    private String title;

    // 分类 1通知公告 2消息提醒
    private Integer type;

    // 是否发布 0否 1是
    @NotNull(message = "是否发布不能为空")
    private Boolean isPublish;

    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    //正文内容
    @NotBlank(message = "正文内容不能为空")
    private String content;

}