package com.zemcho.guzhe.service.shop;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.entity.shop.IndustryCategoryShop;

import java.util.List;

public interface IndustryCategoryService {

    Result getList();

    Result delByIds(SearchParam param);

    Result updateById(IndustryCategory param);

    Result addIndustryCate(IndustryCategory param);

    Result updateSortByIds(SearchParam param);
}
