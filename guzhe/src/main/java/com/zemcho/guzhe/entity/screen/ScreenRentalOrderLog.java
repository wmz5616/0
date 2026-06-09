package com.zemcho.guzhe.entity.screen;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 屏幕店位订单操作记录
 */
@Data
public class ScreenRentalOrderLog {
    // 记录ID
    private Long id;

    // 订单ID
    private Long orderId;

    // 操作类型：1-创建订单 2-编辑展示内容 3-后台审核 4-后台撤销
    private Integer operationType;

    // 操作结果：0-无 1-确认 2-驳回
    private Integer operationResult;

    // 操作人ID
    private Integer operatorId;

    // 操作人姓名
    private String operatorName;

    // 操作人手机号
    private String operatorPhone;

    // 展示内容类型：1-商品 2-海报，仅编辑展示内容时有值
    private Integer displayType;

    // 操作说明：审核意见/驳回原因/撤销原因
    private String operationRemark;

    // 上传文件URL
    private String fileUrl;

    // 操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime operationTime;
}
