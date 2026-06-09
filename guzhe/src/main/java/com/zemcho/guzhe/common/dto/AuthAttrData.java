package com.zemcho.guzhe.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @title: AuthJwtData
 * @Description:
 * @Date: 2025/4/30 14:21
 */
@Data
public class AuthAttrData {
    //当前的角色id
    private List<Integer> roleIds;
}
