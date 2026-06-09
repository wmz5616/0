package com.zemcho.ddql.service.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;

public interface TeamCheckInSettingsService {

    Result update(TeamCheckInSettings data, String token);

    Result select(SearchParam param);
}
