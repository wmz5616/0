package com.zemcho.guzhe.util.tgy.dto;

import lombok.Data;

@Data
public class WxJsRefundResponse {

    private Integer status;

    private String message;

    private String account;

    private String upOrderId;

    private String lowOrderId;

    private String state;

    private String sign;

}
