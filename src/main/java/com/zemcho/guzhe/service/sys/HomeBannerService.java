package com.zemcho.guzhe.service.sys;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.sys.param.HomeBannerParam;
import com.zemcho.guzhe.entity.sys.HomePageBanner;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;

/**
 * @author HXH
 */
public interface HomeBannerService {

    Result insert(HomeBannerParam param);

    Result update(HomeBannerParam param);

    Result deleteByIds(ArrayList<Integer> integers,Integer bannerType);

    Result setStatus(SearchParam param);

    Result setPageBannerSort(SearchParam param);

    Result selectLists();

    Result selectbanner();
}
