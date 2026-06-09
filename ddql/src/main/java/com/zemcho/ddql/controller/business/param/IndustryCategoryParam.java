package com.zemcho.ddql.controller.business.param;

import lombok.Data;

import java.util.List;

/**
 * @author HXH
 */
@Data
public class IndustryCategoryParam {
    //行业id列表
    private List<Integer> ids;
    //行业名称
    private List<String> name;
}
