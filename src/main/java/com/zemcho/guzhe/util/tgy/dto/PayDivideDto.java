package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

/**
 * @title: PayDivideDto
 * @Description:
 * @Date: 2025/11/17 10:59
 */
@Data
public class PayDivideDto {
    private String account;

    private String upOrderId;

    private String lowDivideOrderId;

    private String divideDetail;

    private String sign;
}
