package com.zemcho.guzhe.service.appVersion;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;
import com.zemcho.guzhe.entity.app.AppVersion;
import jakarta.validation.Valid;

public interface AppVersionService {

    Result add(AppVersionParam data);

    Result update(AppVersionParam data);

    Result select(SearchParam param);

    Result delete(Integer id);

    Result selectLog(SearchParam param);

    Result publish(SearchParam param);
}
