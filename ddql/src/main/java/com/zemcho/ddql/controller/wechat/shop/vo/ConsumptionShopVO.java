package com.zemcho.ddql.controller.wechat.shop.vo;

import lombok.Data;

@Data
public class ConsumptionShopVO {

    private Integer id;

    private String name;

    private String coverImageUrl;

    private String location;

    private Double distance;

    private Integer isTop;

    private String info;
}
