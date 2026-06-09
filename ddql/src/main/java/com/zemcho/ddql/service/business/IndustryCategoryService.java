package com.zemcho.ddql.service.business;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.IndustryCategoryParam;
import com.zemcho.ddql.entity.business.IndustryCategory;

import java.util.List;


public interface IndustryCategoryService {

    Result getList();

    Result delByIds(SearchParam param);

    Result update(List<IndustryCategory> param);

    Result updateSortByIds(SearchParam param);
}
