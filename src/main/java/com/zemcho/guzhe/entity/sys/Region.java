package com.zemcho.guzhe.entity.sys;

import lombok.Data;

@Data
public class Region {
    //id
    private Integer id;

    //上级id
    private Integer pid;

    //类型：0国,1省,2城,3地区
    private Integer level;

    //名称
    private String nickName;

    //名称（含类型名称）
    private String name;

    //编码
    private String code;
}
