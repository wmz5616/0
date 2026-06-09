package com.zemcho.ddql.service.checkInSettings;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInTypeParam;

import java.util.List;

public interface CheckInTypeService {

    Result add(CheckInTypeParam param);

    Result update(CheckInTypeParam param);

    Result getCheckInTypeList(SearchParam param);

    Result delete(List<Integer> deleteIds);

    /**
     * 修改打卡类型顺序
     *
     * @param param
     * @return
     */
    Result setSort(SearchParam param);
}
