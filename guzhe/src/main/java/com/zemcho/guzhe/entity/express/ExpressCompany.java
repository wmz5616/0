package com.zemcho.guzhe.entity.express;

import lombok.Data;

/**
 * @title: ExpressCompany
 * @Description:
 * @Date: 2025/10/20 15:33
 */
@Data
public class ExpressCompany {
    // ID
    private Integer id;

    //快递公司名称
    private String name;

    //快递公司编码
    private String code;

    //快递公司类型
    private String type;
}
