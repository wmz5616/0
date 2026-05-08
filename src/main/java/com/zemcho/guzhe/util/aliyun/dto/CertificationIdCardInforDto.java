package com.zemcho.guzhe.util.aliyun.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @title: CertificationIdCardInforDto
 * @Description:
 * @Date: 2025/10/10 16:22
 */
@Data
public class CertificationIdCardInforDto {
    private String province;

    private String city;

    private String district;

    private String area;

    private String sex;

    private LocalDate birthday;
}
