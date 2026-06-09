package com.zemcho.ddql.controller.wechat.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.controller.wechat.team.param.FeedBackParam;
import com.zemcho.ddql.service.wechat.team.TeamFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 团体意见反馈Controller
 */
@RestController
@RequestMapping("/wechat/team/feedback")
public class TeamFeedbackController {

    @Autowired
    private TeamFeedbackService teamFeedbackService;

    /**
     * 提交意见反馈
     */
    @RequestMapping("/submit")
    public Result submitFeedback(@RequestBody @Validated FeedBackParam feedback,
                                 @RequestHeader("token") String token) {
        return teamFeedbackService.submitFeedback(feedback, token);
    }

    /**
     * 查询团队反馈列表
     */
    @RequestMapping("/list")
    public Result getFeedbackList(@RequestBody @Validated FeedBackParam feedback,
                                  @RequestHeader("token") String token) {
        return teamFeedbackService.getFeedbackList(feedback.getTeamId(), token);
    }
}