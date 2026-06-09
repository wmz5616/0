package com.zemcho.guzhe.common.dto;

import lombok.Data;

/**
 * @title: AuthJwtData
 * @Description:
 * @Date: 2025/4/30 14:21
 */
@Data
public class AuthJwtData {
    private Integer adminId;

    private String account;

    private String name;
}
