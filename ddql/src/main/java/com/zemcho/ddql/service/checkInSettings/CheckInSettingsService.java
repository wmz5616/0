package com.zemcho.ddql.service.checkInSettings;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;

public interface CheckInSettingsService {

    Result getSettings(SearchParam param);

    Result updateSettings(CheckInSettings checkInSettings);

}
