package com.zemcho.ddql.common.enums;

/**
 * 订单类型枚举
 */
public enum OrderTypeEnum {
    
    /**
     * 商城订单
     */
    EXCHANGE(1, "商城订单"),
    
    /**
     * 门店订单
     */
    SHOP(2, "门店订单");
    
    private final Integer code;
    private final String desc;
    
    OrderTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}