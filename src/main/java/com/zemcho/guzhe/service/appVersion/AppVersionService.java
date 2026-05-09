package com.zemcho.guzhe.service.appVersion;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;

public interface AppVersionService {

    Result add(AppVersionParam data);

    Result update(AppVersionParam data);

    Result select(SearchParam param);

    Result delete(Integer id);

}
