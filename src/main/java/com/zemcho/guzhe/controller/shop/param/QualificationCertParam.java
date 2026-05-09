package com.zemcho.guzhe.controller.shop.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资质认证参数类
 */
@Data
public class QualificationCertParam {
    // 主键ID
    private Integer id;

    // 营业执照
    @NotBlank(message = "营业执照不能为空")
    private String businessLicense;

    // 其他附件
    private String otherFile;

    // 联系电话
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    // 联系邮箱
    @NotBlank(message = "联系邮箱不能为空")
    private String email;

    // 对应的商家id
    private Integer shopId;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}