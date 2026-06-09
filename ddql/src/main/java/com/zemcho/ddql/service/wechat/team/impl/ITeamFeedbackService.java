package com.zemcho.ddql.service.wechat.team.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.wechat.team.param.FeedBackParam;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamFeedback;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.team.TeamFeedbackMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.wechat.team.TeamFeedbackService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 团体意见反馈Service实现
 */
@Service
public class ITeamFeedbackService implements TeamFeedbackService {

    @Autowired
    private TeamFeedbackMapper teamFeedbackMapper;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Override
    public Result submitFeedback(FeedBackParam feedback, String token) {
        // 获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 验证用户
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        // 验证团队ID
        Integer teamId = feedback.getTeamId();
        if (teamId == null) {
            return Result.error("团队ID不能为空");
        }

        // 验证团队存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 验证用户是否属于该团队
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, 0);
        boolean isMember = teamUserList.stream().anyMatch(tu -> tu.getTeamId().equals(teamId));
        if (!isMember) {
            return Result.error("用户不属于该团队");
        }

        // 验证反馈内容
        String content = feedback.getContent();
        if (content == null || content.trim().isEmpty()) {
            return Result.error("反馈内容不能为空");
        }
        if (content.length() > 50) {
            return Result.error("反馈内容不能超过50个字符");
        }

        TeamFeedback teamFeedback = new TeamFeedback();

        // 设置用户ID
        teamFeedback.setUserId(userId);
        teamFeedback.setTeamId(teamId);
        teamFeedback.setContent(content);
        // 设置默认值
        if (feedback.getIsAnonymous() == null) {
            teamFeedback.setIsAnonymous(0);
        }
        teamFeedback.setIsAnonymous(feedback.getIsAnonymous());
        if(feedback.getIsAnonymous()==1){
            teamFeedback.setUserName("匿名用户");
        }else{
            teamFeedback.setUserName(casUser.getName()!=null?casUser.getName():casUser.getNickname());
        }

        // 插入反馈
        int result = teamFeedbackMapper.insert(teamFeedback);
        if (result > 0) {
            return Result.success("提交成功");
        } else {
            return Result.error("提交失败");
        }
    }

    @Override
    public Result getFeedbackList(Integer teamId, String token) {
        // 获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 验证用户
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        // 验证团队存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 验证用户是否属于该团队
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, 0);
        boolean isMember = teamUserList.stream().anyMatch(tu -> tu.getTeamId().equals(teamId));
        if (!isMember) {
            return Result.error("用户不属于该团队");
        }

        // 查询反馈列表
        List<TeamFeedback> feedbackList = teamFeedbackMapper.selectByTeamId(teamId);
        return Result.success("获取成功", feedbackList);
    }
}