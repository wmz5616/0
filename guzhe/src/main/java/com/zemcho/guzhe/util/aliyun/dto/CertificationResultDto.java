package com.zemcho.guzhe.util.aliyun.dto;

import lombok.Data;

/**
 * @title: CertificationResultDto
 * @Description:
 * @Date: 2025/10/10 16:22
 */
@Data
public class CertificationResultDto {
    private Integer error_code;

    private String reason;

    private CertificationResultDataDto result;
}
