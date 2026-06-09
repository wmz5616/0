package com.zemcho.ddql.service.appVersion;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.app.AppVersion;

public interface AppVersionService {

    Result add(AppVersion data);

    Result update(AppVersion data);

    Result select(SearchParam param);

    Result delete(Integer id);
}
