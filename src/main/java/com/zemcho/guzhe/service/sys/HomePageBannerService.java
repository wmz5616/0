package com.zemcho.guzhe.service.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.sys.HomePageBanner;

import java.util.List;

public interface HomePageBannerService {

    Result insert(HomePageBanner data);

    Result update(HomePageBanner data);

    Result selectAll();

    Result selectShowLists();

    Result deleteByIds(List<Integer> ids);

    /**
     * 编辑首页轮播图状态
     *
     * @param param
     * @return
     */
    Result setStatus(SearchParam param);

    /**
     * 修改首页轮播图顺序
     *
     * @param param
     * @return
     */
    Result setPageBannerSort(SearchParam param);
}
