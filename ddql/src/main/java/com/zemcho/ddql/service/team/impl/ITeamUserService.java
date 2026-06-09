package com.zemcho.ddql.service.team.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.vo.TeamUserVo;
import com.zemcho.ddql.controller.wechat.team.vo.TeamApplicationRecordVo;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamApplicationRecord;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import com.zemcho.ddql.entity.team.TeamDepartment;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.team.*;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.service.personalCenter.MessageAnnouncementService;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import com.zemcho.ddql.service.team.TeamUserService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ITeamUserService implements TeamUserService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Autowired
    private TeamDepartmentMapper teamDepartmentMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    TeamCheckInSettingsMapper teamCheckInSettingsMapper;

    @Autowired
    private TeamApplicationRecordMapper teamApplicationRecordMapper;

    @Autowired
    private MessageAnnouncementService messageAnnouncementService;

    @Override
    public Result selectUserTeams(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Boolean isCheckDeleted = true;
        if (param.getSearchType() != null && param.getSearchType().equals(1)) { //已退出团体而且还有健康币的数据也要返回
            isCheckDeleted = false;
        }
        List<TeamUserVo> teamUsers = teamUserMapper.selectListByUserId(userId, isCheckDeleted);
        List<TeamUserVo> list = new ArrayList<>();
        if (teamUsers != null && !teamUsers.isEmpty()) {
            List<Integer> teamIds = teamUsers.stream().map(TeamUserVo::getTeamId).toList();
            List<Team> teams = teamMapper.selectByIds(teamIds, null);
            Map<Integer, Team> teamMap = teams.stream().collect(Collectors.toMap(Team::getId, team -> team));

            List<TeamCheckInSettings> teamCheckInSettingsList = teamCheckInSettingsMapper.selectByTeamIds(teamIds);
            Map<Integer, TeamCheckInSettings> teamCheckInSettingsMap = new HashMap<>();
            if (teamCheckInSettingsList != null && !teamCheckInSettingsList.isEmpty()) {
                teamCheckInSettingsMap =
                        teamCheckInSettingsList.stream().collect(Collectors.toMap(TeamCheckInSettings::getTeamId,
                                teamCheckInSettings -> teamCheckInSettings));
            }

            for (TeamUserVo teamUser : teamUsers) {
                if (param.getSearchType() != null && param.getSearchType().equals(1)) {
                    if (teamUser.getDeleteTime() != null && teamUser.getHealthyCoin() <= 0) {
                        continue;
                    }
                }

                Team team = teamMap.get(teamUser.getTeamId());
                if (team == null || team.getStatus() == 1) {
                    continue;
                }

                teamUser.setTeam(team);
                teamUser.setTeamCheckInSettings(teamCheckInSettingsMap.get(teamUser.getTeamId()));

                list.add(teamUser);
            }
        }

        return Result.success("获取成功", list);
    }

    @Override
    public Result selectTeamUserList(TeamUserSearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<TeamUserVo> list = teamUserMapper.selectList(param);
        PageInfo<TeamUserVo> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    // 此处不能修改团队用户信息
    @Override
    public Result updateTeamUser(TeamUser teamUser) {
        if (teamUser.getTeamId() == null || teamUser.getUserId() == null) {
            return Result.error("参数错误");
        }
        if (!teamUserMapper.ifExist(teamUser.getTeamId(), teamUser.getUserId(), true)) {
            return Result.error("用户不存在");
        }

        // 如果传入了 departmentId，需要校验
        if (teamUser.getDepartmentId() != null) {
            // 查询团队是否开启多部门管理
            Team team = teamMapper.selectById(teamUser.getTeamId());
            if (team == null) {
                return Result.error("团队不存在");
            }
            if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
                return Result.error("该团队未开启多部门管理，无法设置部门");
            }
            // 校验部门是否属于该团队
            TeamDepartment department = teamDepartmentMapper.selectById(teamUser.getDepartmentId());
            if (department == null || !department.getTeamId().equals(team.getId())) {
                return Result.error("部门不存在或不属于该团队");
            }
        }

        teamUserMapper.update(teamUser);
        return Result.success("修改成功");
    }

    @Override
    public Result addTeamUser(TeamUser teamUser) {
        if (teamUser.getTeamId() == null || teamUser.getUserPhone() == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectByPhone(teamUser.getUserPhone());
        if (casUser == null) {
            return Result.error("该手机号尚未注册小程序");
        }

        TeamUser existTeamUser = teamUserMapper.selectByTeamIdAndUserId(teamUser.getTeamId(), casUser.getId(), false);
        if (existTeamUser != null && existTeamUser.getDeleteTime() == null) {
            return Result.error("该用户已在团队中");
        }

        Team team = teamMapper.selectById(teamUser.getTeamId());
        if (team == null) {
            return Result.error("团队不存在");
        }

        if (team.getIsMultiDepartment() != null && team.getIsMultiDepartment() == 1) {
            if (teamUser.getDepartmentId() == null) {
                return Result.error("开启多部门管理的团队必须选择部门");
            }
            TeamDepartment department = teamDepartmentMapper.selectById(teamUser.getDepartmentId());
            if (department == null || !department.getTeamId().equals(team.getId())) {
                return Result.error("部门不存在或不属于该团队");
            }
        } else {
            teamUser.setDepartmentId(null);
        }

        if (existTeamUser == null) {
            TeamUser newTeamUser = new TeamUser();
            newTeamUser.setTeamId(teamUser.getTeamId());
            newTeamUser.setUserId(casUser.getId());
            newTeamUser.setType(teamUser.getType() != null ? teamUser.getType() : 2);
            newTeamUser.setUserName(teamUser.getUserName());
            newTeamUser.setUserPhone(teamUser.getUserPhone());
            newTeamUser.setDepartmentId(teamUser.getDepartmentId());
            newTeamUser.setHealthyCoin(0);
            newTeamUser.setJoinType(3); // 3 represents manually added by admin
            newTeamUser.setStatus(0);
            newTeamUser.setCreateTime(LocalDateTime.now());
            teamUserMapper.insert(newTeamUser);
        } else {
            teamUserMapper.restoreById(existTeamUser.getId());
            existTeamUser.setDepartmentId(teamUser.getDepartmentId());
            existTeamUser.setType(teamUser.getType() != null ? teamUser.getType() : 2);
            existTeamUser.setUserName(teamUser.getUserName());
            teamUserMapper.update(existTeamUser);
        }

        teamMapper.incPeopleNumber(teamUser.getTeamId(), 1);
        return Result.success("新增成员成功");
    }

    /**
     * 编辑团体用户状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setUserStatus(ChangeParam param) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        teamUserMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    /**
     * 删除团体用户
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Result deleteTeamUserByAdmin(DeleteParam param) {
        Set<Integer> ids = param.getDeleteIds();

        if (ids != null && ids.size() > 0) {
            List<TeamUser> list = teamUserMapper.selectByIds(ids);
            if (list == null || list.isEmpty()) {
                return Result.error("数据不存在，请刷新查看");
            }
            Map<Integer, Integer> teamUserNumMap = new HashMap<>();
            for (TeamUser item : list) {
                if (item.getHealthyCoin() > 0) {
                    return Result.error("所选成员在团队还有健康币,不可删除");
                }

                if (teamUserNumMap.containsKey(item.getTeamId())) {
                    teamUserNumMap.put(item.getTeamId(), teamUserNumMap.get(item.getTeamId()) + 1);
                } else {
                    teamUserNumMap.put(item.getTeamId(), 1);
                }
            }

            teamUserMapper.deleteByIds(ids);
            for (Map.Entry<Integer, Integer> entry : teamUserNumMap.entrySet()) {
                teamMapper.decPeopleNumber(entry.getKey(), entry.getValue());
            }
        }

        return Result.success("操作成功");
    }

    @Transactional
    @Override
    public Result deleteTeamUser(Integer teamId, Integer userId, String token) {
        if (teamId == null || userId == null) {
            return Result.error("参数错误");
        }
        // 解析token获取当前操作用户信息
        Integer manageUserId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        // 从团队用户列表中获取当前操作用户 和 被删除用户信息
        TeamUser manageUser = teamUserMapper.selectByTeamIdAndUserId(teamId, manageUserId, true);
        TeamUser deleteUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (deleteUser == null) {
            return Result.error("用户不存在");
        }
        // 创建者不能退出团队
        if (deleteUser.getType().equals(0)) {
            return Result.error("创建者不能退出团队");
        }
//        if (deleteUser.getHealthyCoin() > 0) {
//            return Result.error("该成员在团队还有健康币,不可退出");
//        }
        //
        if (!deleteUser.getUserId().equals(manageUser.getUserId()) && manageUser.getType().equals(2)) {
            return Result.error("没有权限");
        }

        //更新团队人数
        teamMapper.decPeopleNumber(teamId, 1);

        teamUserMapper.delete(teamId, userId);

        return Result.success("删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveTeamApplicationRecord(TeamApplicationRecord teamApplication) {
        // 检查参数
        if (teamApplication.getTeamId() == null || teamApplication.getUserId() == null) {
            return Result.error("参数错误");
        }
        if (teamApplication.getJoinType() == null) {
            return Result.error("申请加入方式不能为空");
        }
//        if (!teamMapper.ifExist(teamApplication.getTeamId())) {
//            return Result.error("团队不存在");
//        }
        Team teamInfo = teamMapper.selectById(teamApplication.getTeamId());
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }
        Integer isUserAuth = teamInfo.getIsUserAuth();

        // 检查团队用户是否存在
//        if (teamUserMapper.ifExist(teamApplication.getTeamId(), teamApplication.getUserId(), true)) {
//            return Result.error("团队用户已存在");
//        }
        TeamUser existTeamUser = teamUserMapper.selectByTeamIdAndUserId(teamApplication.getTeamId(),
                teamApplication.getUserId(), false);
        if (existTeamUser != null && existTeamUser.getDeleteTime() == null) {
            return Result.error("团队用户已存在");
        }
//        if (teamApplicationRecordMapper.checkUserCanApply(teamApplication.getTeamId(), teamApplication.getUserId())) {
//            return Result.error("您已申请过，不可重复申请");
//        }

        // 检查申请的部门是否是一个合法的部门
        if (teamInfo.getIsMultiDepartment() != null && teamInfo.getIsMultiDepartment() == 1) {
            // 该团体开启了多部门管理
            List<TeamDepartment> teamDepartments = teamDepartmentMapper.selectByTeamId(teamInfo.getId());
            List<Integer> departmentIds = teamDepartments.stream().map(TeamDepartment::getId).toList();
            if (teamApplication.getDepartmentId() == null || !departmentIds.contains(teamApplication.getDepartmentId())) {
                return Result.error("申请的部门不存在");
            }
        } else {
            // 该团体没有开启多部门
            teamApplication.setDepartmentId(null);
        }

        TeamApplicationRecord teamApplicationRecord = new TeamApplicationRecord();
        teamApplicationRecord.setTeamId(teamApplication.getTeamId());
        teamApplicationRecord.setDepartmentId(teamApplication.getDepartmentId());
        teamApplicationRecord.setUserId(teamApplication.getUserId());
        teamApplicationRecord.setUserName(teamApplication.getUserName());
        teamApplicationRecord.setUserPhone(teamApplication.getUserPhone());
        teamApplicationRecord.setStatus(isUserAuth == 0 ? 1 : 0);
        teamApplicationRecord.setJoinType(teamApplication.getJoinType());
        teamApplicationRecord.setCreateTime(LocalDateTime.now());
        teamApplicationRecordMapper.insert(teamApplicationRecord);

        //不用审核直接进入
        if (isUserAuth == 0) {
            // 插入新纪录到TeamUser表
            if (existTeamUser == null) {
                TeamUser teamUser = new TeamUser();
                teamUser.setTeamId(teamApplicationRecord.getTeamId());
                teamUser.setUserId(teamApplicationRecord.getUserId());
                teamUser.setType(2);
                // 如果团队开启多部门管理，分配默认部门
                if (teamInfo.getIsMultiDepartment() != null && teamInfo.getIsMultiDepartment() == 1) {
                    // 设置部门
                    teamUser.setDepartmentId(teamApplicationRecord.getDepartmentId());
                }
                teamUser.setUserName(teamApplicationRecord.getUserName());
                teamUser.setUserPhone(teamApplicationRecord.getUserPhone());
                teamUser.setHealthyCoin(0);
                teamUser.setJoinType(teamApplicationRecord.getJoinType());
                teamUser.setStatus(0);
                teamUser.setCreateTime(LocalDateTime.now());
                teamUserMapper.insert(teamUser);
            } else {
                teamUserMapper.restoreById(existTeamUser.getId());
            }

            // 更新团队人数
            teamMapper.incPeopleNumber(teamApplicationRecord.getTeamId(), 1);
        }

        return Result.success("操作成功");
    }

    /**
     * 查询团队申请记录
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result selectTeamApplicationRecord(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = param.getSearchId();
        if (teamId == null) {
            return Result.error("参数错误");
        }

        TeamUser checkTeamUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (checkTeamUser == null || checkTeamUser.getType().equals(2)) {
            return Result.error("您无权查看该团体申请信息");
        }

        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<TeamApplicationRecordVo> list = teamApplicationRecordMapper.selectList(param);
        PageInfo<TeamApplicationRecordVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateTeamApplicationRecord(TeamApplicationRecord teamApplication, String token) {
        // 查出该条审核记录
        TeamApplicationRecord teamApplicationRecord = teamApplicationRecordMapper.selectById(teamApplication.getId());
        if (teamApplicationRecord == null) {
            return Result.error("该申请不存在");
        }
        if (teamApplication.getStatus() != 1 && teamApplication.getStatus() != 2) {
            return Result.error("请选择处理结果");
        }
        // 解析token获取当前操作用户信息
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        TeamUser manageUser = teamUserMapper.selectByTeamIdAndUserId(teamApplicationRecord.getTeamId(), userId, true);
        // 是否可以操作
        if (manageUser == null || (manageUser.getType() != 0 && manageUser.getType() != 1)) {
            return Result.error("无权限操作");
        }

        // 如果传入了 departmentId，校验部门是否合法
        Integer newDepartmentId = teamApplication.getDepartmentId();
        if (newDepartmentId != null) {
            // 查询团队是否开启多部门管理
            Team team = teamMapper.selectById(teamApplicationRecord.getTeamId());
            if (team == null) {
                return Result.error("团队不存在");
            }
            if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
                return Result.error("该团队未开启多部门管理，无法设置部门");
            }
            // 校验部门是否属于该团队
            TeamDepartment department = teamDepartmentMapper.selectById(newDepartmentId);
            if (department == null || !department.getTeamId().equals(team.getId())) {
                return Result.error("部门不存在或不属于该团队");
            }
            // 更新申请记录的部门ID
            teamApplicationRecord.setDepartmentId(newDepartmentId);
        }

        TeamUser existTeamUser = null;
        if (teamApplication.getStatus() == 1) {
            existTeamUser = teamUserMapper.selectByTeamIdAndUserId(teamApplicationRecord.getTeamId(),
                    teamApplicationRecord.getUserId(), false);
            if (existTeamUser != null && existTeamUser.getDeleteTime() == null) {
                return Result.error("该团队用户已存在");
            }
        }

        // 如果是允许加入
        if (teamApplication.getStatus() == 1) {
            // 插入新纪录到TeamUser表
            if (existTeamUser == null) {
                TeamUser teamUser = new TeamUser();
                teamUser.setTeamId(teamApplicationRecord.getTeamId());
                teamUser.setUserId(teamApplicationRecord.getUserId());
                teamUser.setType(2);
                teamUser.setUserName(teamApplicationRecord.getUserName());
                teamUser.setUserPhone(teamApplicationRecord.getUserPhone());
                // 设置部门（使用可能更新后的部门ID）
                teamUser.setDepartmentId(teamApplicationRecord.getDepartmentId());
                teamUser.setHealthyCoin(0);
                teamUser.setJoinType(teamApplicationRecord.getJoinType());
                teamUser.setStatus(0);
                teamUser.setCreateTime(LocalDateTime.now());
                teamUserMapper.insert(teamUser);
            } else {
                teamUserMapper.restoreById(existTeamUser.getId());
            }

            // 更新团队人数
            teamMapper.incPeopleNumber(teamApplicationRecord.getTeamId(), 1);
        }
        
        // 更新审核状态
        teamApplicationRecord.setStatus(teamApplication.getStatus());
        teamApplicationRecordMapper.update(teamApplicationRecord);

        // 审核结果通知用户 发送消息公告
        // 查出team
        Team team = teamMapper.selectById(teamApplicationRecord.getTeamId());
        String title = "加入" + team.getName() + "团队审核结果通知";
        String content = teamApplicationRecord.getStatus() == 1 ? "申请加入" + team.getName() + "审核通过" :
                "申请加入" + team.getName() + "审核未通过";
        messageAnnouncementService.insert(teamApplicationRecord.getUserId(), title, content);

        return Result.success("操作成功");
    }

    @Override
    @Transactional
    public Result updateTeamUserType(TeamUser teamUser, String token) {
        if (teamUser.getTeamId() == null || teamUser.getUserId() == null || teamUser.getType() < 0 || teamUser.getType() > 2) {
            return Result.error("参数错误");
        }
        Integer teamId = teamUser.getTeamId();
        Integer userId = teamUser.getUserId();
        Integer type = teamUser.getType();
        // 查出当前用户信息
        TeamUser updateUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (updateUser == null) {
            return Result.error("信息不存在");
        }
        // 解析token获取当前操作用户信息
        Integer manageUserId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        TeamUser manageUser = teamUserMapper.selectByTeamIdAndUserId(teamId, manageUserId, true);
        if (manageUser == null || (manageUser.getType() != 0 && manageUser.getType() != 1)) {
            return Result.error("无权限操作");
        }

        // 根据要操作的类型查询
        // 0 将当前用户类型转为创建者
        if (type == 0) {
            if (manageUser.getType() != 0) {
                return Result.error("无权限操作");
            }
            // 转让创建者身份 原创建者改为普通成员
            manageUser.setType(2);
            teamUserMapper.update(manageUser);

            updateUser.setType(0);
        }
        // 1将当前用户类型转为管理员
        if (type == 1) {
            // 检查管理员是否已超三个
            Integer count = teamUserMapper.getCount(teamId, null, 1);
            if (count >= 3) {
                return Result.error("当前团队管理员已超三个");
            }
            updateUser.setType(1);
        }
        // 2 将当前用户类型转为普通用户
        if (type == 2) {
            updateUser.setType(2);
        }
        // 更新
        teamUserMapper.update(updateUser);
        return Result.success("操作成功");
    }

    /**
     * 修改团队成员信息
     *
     * @param teamUser
     * @param token
     * @return
     */
    @Override
    public Result wechatUpdateTeamUser(TeamUser teamUser, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer id = teamUser.getId();
        if (id == null) {
            return Result.error("参数错误");
        }

        String userName = teamUser.getUserName();
        if (userName == null || "".equals(userName)) {
            return Result.error("姓名不能为空");
        }
        String userPhone = teamUser.getUserPhone();
//        if (userPhone == null || "".equals(userPhone)) {
//            return Result.error("手机号不能为空");
//        }

        TeamUser teamUserInfo = teamUserMapper.selectById(id);
        if (teamUserInfo == null) {
            return Result.error("记录不存在");
        }
        // 只有管理员和创建者可编辑部门
        if (!teamUserInfo.getUserId().equals(userId)) {
            TeamUser checkTeamUser = teamUserMapper.selectByTeamIdAndUserId(teamUserInfo.getTeamId(), userId, true);
            if (checkTeamUser == null || checkTeamUser.getType().equals(2)) {
                return Result.error("您无权修改该成员信息");
            }
        }

        TeamUser updateTeamUser = new TeamUser();
        updateTeamUser.setId(id);
        updateTeamUser.setTeamId(teamUserInfo.getTeamId());
        updateTeamUser.setUserId(teamUserInfo.getUserId());
        updateTeamUser.setUserName(userName);
        updateTeamUser.setUserPhone(userPhone);
        updateTeamUser.setDepartmentId(teamUser.getDepartmentId());
        teamUserMapper.update(updateTeamUser);

        return Result.success("操作成功");
    }

    @Override
    public Result selectTeamUserAndDepartmentList(TeamUserSearchParam param) {
        // 返回值
        Map<String, Object> result = new HashMap<>();
        result.put("user", new ArrayList<>());
        result.put("departments", new ArrayList<>());

        // 检查参数
        if (param.getTeamId() == null) {
            return Result.error("参数错误");
        }







        // 查询是否是多部门管理的团队
        Team team = teamMapper.selectById(param.getTeamId());
        if (team.getIsMultiDepartment() != null && team.getIsMultiDepartment().equals(1)) {
            // 多部门管理的团队 查询直属团队下的成员
            param.setDepartmentId(0);
            List<TeamUserVo> teamUsers = teamUserMapper.selectList(param);
            result.put("user", teamUsers);
            // 查询团队下面的部门
            List<TeamDepartment> teamDepartments = teamDepartmentMapper.selectByTeamId(param.getTeamId());
            result.put("departments", teamDepartments);
        } else {
            // 不是多部门管理 直接查询所有成员即可
            param.setDepartmentId(null);
            List<TeamUserVo> teamUsers = teamUserMapper.selectList(param);
            result.put("user", teamUsers);
        }


        return Result.success("获取成功", result);
    }
}
