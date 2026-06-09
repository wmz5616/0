package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.entity.team.TeamFeedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团体意见反馈Mapper
 */
public interface TeamFeedbackMapper {

    /**
     * 新增反馈
     */
    Integer insert(TeamFeedback feedback);


    /**
     * 根据团队ID查询反馈列表
     */
    List<TeamFeedback> selectByTeamId(@Param("teamId") Integer teamId);
}