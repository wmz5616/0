package com.zemcho.ddql.controller.business.param;

import com.zemcho.ddql.entity.business.IndustryCategory;
import lombok.Data;

import java.util.List;

/**
 * @author HXH
 */
@Data
public class IndustryCategoryBatchParam {
    private List<IndustryCategory> categoryList;
}
