package com.zemcho.guzhe.controller.screen_order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台店位订单操作记录
 */
@Data
public class ScreenOrderManageLogVo {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime operationTime;

    private Integer operationType;

    private String operationTypeText;

    private Integer operationResult;

    private String operationResultText;

    private String operatorName;

    private String operatorPhone;

    private String operatorText;

    private Integer displayType;

    private String displayTypeText;

    private String operationRemark;

    private String fileUrl;
}
