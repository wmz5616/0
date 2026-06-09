package com.zemcho.ddql.service.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.personalCenter.MessageAnnouncement;

public interface MessageAnnouncementService {

    // 用于新增一条消息
    void insert(Integer userId,String title,String content);

    // searchId
    Result selectUserMessage(String token);

    Result readMessage(Integer id);

    Result readAllMessage(String token);


}
