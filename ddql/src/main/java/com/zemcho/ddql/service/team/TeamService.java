package com.zemcho.ddql.service.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.controller.team.param.TeamVerificationParam;
import com.zemcho.ddql.controller.team.vo.TeamListVo;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamVerification;

import java.util.List;

public interface TeamService {

    // 创建团队
    public Result addTeam(Team team, String token, Boolean isAdmin);

    // 更新团队
    public Result updateTeam(Team team);

    /**
     * 编辑团队状态
     *
     * @param param
     * @return
     */
    public Result setStatus(ChangeParam param);

    // 更新团队(小程序)
    public Result updateTeam(Team team, String token);

    // 删除团队
    public Result deleteTeam(Integer deleteId, String token, Boolean isAdmin);

    // 查询团队列表
    public Result selectTeamList(TeamSearchParam param);

    /**
     * 统计团队充值订单数据
     *
     * @param param
     * @return
     */
    public Result teamRechargeOrderCount(SearchParam param);

    // 添加团队验证记录
    public Result addVerificationRecord(TeamVerificationParam param, String token, Boolean isAdmin);

    // 更新团队验证记录
    public Result updateVerificationRecord(TeamVerificationParam param, String token, Boolean isAdmin);

    // 查询资质验证审核列表
    public Result selectVerificationRecord(SearchParam param);

    // 人工审核资质验证
    public Result auditVerificationRecord(TeamVerificationParam param, String token);

    List<TeamListVo> selectTeamListToExport(TeamSearchParam param);

    /**
     * 团队邀请小程序码
     *
     * @param param
     * @return
     */
    Result teamUserInvitationQrCode(SearchParam param);

    /**
     * 获取团队详情
     *
     * @param param
     * @param token
     * @return
     */
    Result getWechatTeamInfo(SearchParam param, String token);

    /**
     * 初始化团队的默认部门
     *
     * @param teamId 团队ID
     */
    void initDefaultDepartment(Integer teamId);

    /**
     * 团队是否是多部门
     *
     * @param param
     * @return
     */
    Result isMultiDepartment(SearchParam param);
}
