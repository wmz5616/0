package com.zemcho.ddql.controller.wechat.index.dto;

import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInRankCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInRankVo;
import com.zemcho.ddql.controller.team.vo.TeamUserVo;
import com.zemcho.ddql.entity.team.TeamFeedback;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 打卡排行榜导出数据封装
 */
@Data
public class CheckInExportData {
    private String teamName;
    private String startTime;
    private String endTime;
    private int totalMemberCount;
    private int totalCheckInNum;
    private int totalHealthCoin;
    private int totalCheckInTime;
    private int activeMemberCount;
    private float avgCheckInNum;
    private float activeRate;
    private List<UserCheckInRankVo> top3List;
    private List<DepartmentStat> departmentStats;
    private Map<String, Integer> exerciseTypeMap;
    private List<TeamFeedback> feedbackList;
    private List<TeamUserVo> teamUserList;
    private List<UserCheckInRankCountVo> checkInCountList;
}