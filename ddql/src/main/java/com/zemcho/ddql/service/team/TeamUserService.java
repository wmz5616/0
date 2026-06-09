package com.zemcho.ddql.service.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.entity.team.TeamApplicationRecord;
import com.zemcho.ddql.entity.team.TeamUser;
import jakarta.validation.Valid;

import java.util.List;


public interface TeamUserService {

    // 查询用户的团队列表
    Result selectUserTeams(SearchParam param, String token);

    // 查询团队成员
    Result addTeamUser(TeamUser teamUser);

    Result selectTeamUserList(TeamUserSearchParam param);

    // 编辑团队成员的信息(不包括类型) 根据teamId 和 userId 更新
    Result updateTeamUser(TeamUser teamUser);

    /**
     * 编辑团体用户状态
     *
     * @param param
     * @return
     */
    public Result setUserStatus(ChangeParam param);

    /**
     * 删除团体用户
     *
     * @param param
     * @return
     */
    public Result deleteTeamUserByAdmin(DeleteParam param);

    // 删除团队成员 teamId 和 userId
    Result deleteTeamUser(Integer teamId, Integer userId, String token);

    // 新增添加加入团队审核
    Result saveTeamApplicationRecord(TeamApplicationRecord teamApplication);

    /**
     * 查询团队申请记录
     *
     * @param param
     * @param token
     * @return
     */
    Result selectTeamApplicationRecord(SearchParam param, String token);

    // 审核(编辑)添加团队成员 根据id 更新status
    Result updateTeamApplicationRecord(TeamApplicationRecord teamApplication, String token);

    // 变更团队成员的类型 传teamId userId 和 要将其修改的类型 type
    Result updateTeamUserType(TeamUser teamUser, String token);

    /**
     * 小程序端修改团队成员信息
     *
     * @param teamUser
     * @param token
     * @return
     */
    Result wechatUpdateTeamUser(TeamUser teamUser, String token);

    /**
     * 查询团队成员列表和和部门 部门下的成员
     * @param param
     * @return
     */
    Result selectTeamUserAndDepartmentList(TeamUserSearchParam param);
}
