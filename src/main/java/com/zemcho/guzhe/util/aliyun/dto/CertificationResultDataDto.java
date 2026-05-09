package com.zemcho.guzhe.util.aliyun.dto;

import lombok.Data;

/**
 * @title: CertificationResultDataDto
 * @Description:
 * @Date: 2025/10/10 16:22
 */
@Data
public class CertificationResultDataDto {
    private String realname;

    private String idcard;

    private Boolean isok;

    private CertificationIdCardInforDto IdCardInfor;
}
