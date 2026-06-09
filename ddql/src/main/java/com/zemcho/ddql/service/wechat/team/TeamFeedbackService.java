package com.zemcho.ddql.service.wechat.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.team.param.FeedBackParam;

/**
 * 团体意见反馈Service
 */
public interface TeamFeedbackService {

    /**
     * 提交意见反馈
     */
    Result submitFeedback(FeedBackParam feedback, String token);

    /**
     * 查询团队反馈列表
     */
    Result getFeedbackList(Integer teamId, String token);
}