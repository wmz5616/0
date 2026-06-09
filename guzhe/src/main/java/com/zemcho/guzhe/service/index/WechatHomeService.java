package com.zemcho.guzhe.service.index;

import com.zemcho.guzhe.common.Result;

/**
 * @author HXH
 */
public interface WechatHomeService {
    Result selectShowLists();

    Result getTopNotice();

    Result selectLists(String token);

    Result selectbanner(String token);

    Result getBasicConfig();
}
