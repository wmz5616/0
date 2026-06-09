package com.zemcho.ddql.service.team.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.team.TeamCheckInSettingsMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.team.TeamCheckInSettingsService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ITeamCheckInSettingsService implements TeamCheckInSettingsService {
    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamCheckInSettingsMapper teamCheckInSettingsMapper;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Override
    public Result update(TeamCheckInSettings data, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = data.getTeamId();
        if (!teamMapper.ifExist(teamId)) {
            return Result.error("团队不存在");
        }
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (teamUser == null || (teamUser.getType() != 0 && teamUser.getType() != 1)) {
            return Result.error("您没有权限修改此团队信息");
        }
        teamCheckInSettingsMapper.updateByTeamId(data);
        return Result.success("操作成功");
    }

    @Override
    public Result select(SearchParam param) {
        Integer teamId = param.getSearchId();
        if (!teamMapper.ifExist(teamId)) {
            return Result.error("团队不存在");
        }
        TeamCheckInSettings data = teamCheckInSettingsMapper.selectByTeamId(teamId);
        return Result.success("查询成功", data);
    }
}
