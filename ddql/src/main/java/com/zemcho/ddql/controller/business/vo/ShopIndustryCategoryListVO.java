package com.zemcho.ddql.controller.business.vo;


import lombok.Data;

@Data
public class ShopIndustryCategoryListVO {

    private Integer shopId;

    private Integer industryCategoryId;

    private String industryCategoryName;

    public String getIndustryCategoryName() {
        return industryCategoryName;
    }

    public void setIndustryCategoryName(String industryCategoryName) {
        this.industryCategoryName = industryCategoryName;
    }
}
