package com.zemcho.ddql.service.team.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.vo.TeamUserVo;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamDepartment;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.team.TeamDepartmentMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import com.zemcho.ddql.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 团队部门Service实现类
 */
@Slf4j
@Service
public class ITeamDepartmentService implements TeamDepartmentService {

    @Autowired
    private TeamDepartmentMapper teamDepartmentMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createDepartment(TeamDepartment department, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 参数校验
        if (department.getTeamId() == null) {
            return Result.error("团队ID不能为空");
        }
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return Result.error("部门名称不能为空");
        }

        // 检查团队是否存在
        Team team = teamMapper.selectById(department.getTeamId());
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 检查用户是否有权限（创建者或管理员）
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(department.getTeamId(), userId, true);
        if (teamUser == null || teamUser.getType() > 1) {
            return Result.error("无权限操作");
        }

        // 检查团队是否开启多部门管理
        if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
            return Result.error("该团队未开启多部门管理");
        }

        // 设置默认值
        department.setName(department.getName().trim());
        if (department.getSort() == null) {
            Integer maxSort = teamDepartmentMapper.selectMaxSortByTeamId(department.getTeamId());
            department.setSort(maxSort == null ? 0 : maxSort + 1);
        }
        if (department.getStatus() == null) {
            department.setStatus(1);
        }

        // 插入部门
        int result = teamDepartmentMapper.insert(department);
        if (result > 0) {
            return Result.success("创建成功", department);
        }
        return Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDepartment(TeamDepartment department, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 参数校验
        if (department.getId() == null) {
            return Result.error("部门ID不能为空");
        }

        // 查询部门信息
        TeamDepartment existDepartment = teamDepartmentMapper.selectById(department.getId());
        if (existDepartment == null) {
            return Result.error("部门不存在");
        }

        // 检查用户是否有权限
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(existDepartment.getTeamId(), userId, true);
        if (teamUser == null || teamUser.getType() > 1) {
            return Result.error("无权限操作");
        }

        // 更新部门
        int result = teamDepartmentMapper.update(department);
        if (result > 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDepartment(Integer departmentId, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 查询部门信息
        TeamDepartment department = teamDepartmentMapper.selectById(departmentId);
        if (department == null) {
            return Result.error("部门不存在");
        }

        // 检查用户是否有权限
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(department.getTeamId(), userId, true);
        if (teamUser == null || teamUser.getType() > 1) {
            return Result.error("无权限操作");
        }

        int i = teamUserMapper.ifExistInDepartment(departmentId);
        if (i != 0) {
            return Result.error("部门内存在成员，无法删除");
        }

        // 删除部门（软删除）
        int result = teamDepartmentMapper.deleteById(departmentId, LocalDateTime.now());
        if (result > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sysCreateDepartment(TeamDepartment department) {
        if (department.getTeamId() == null) {
            return Result.error("团队ID不能为空");
        }
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return Result.error("部门名称不能为空");
        }
        Team team = teamMapper.selectById(department.getTeamId());
        if (team == null) {
            return Result.error("团队不存在");
        }
        if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
            return Result.error("该团队未开启多部门管理");
        }
        department.setName(department.getName().trim());
        if (department.getSort() == null) {
            Integer maxSort = teamDepartmentMapper.selectMaxSortByTeamId(department.getTeamId());
            department.setSort(maxSort == null ? 0 : maxSort + 1);
        }
        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        int result = teamDepartmentMapper.insert(department);
        if (result > 0) {
            return Result.success("创建成功", department);
        }
        return Result.error("创建失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sysUpdateDepartment(TeamDepartment department) {
        if (department.getId() == null) {
            return Result.error("部门ID不能为空");
        }
        TeamDepartment exist = teamDepartmentMapper.selectById(department.getId());
        if (exist == null) {
            return Result.error("部门不存在");
        }
        if (department.getName() != null) {
            if (department.getName().trim().isEmpty()) {
                return Result.error("部门名称不能为空");
            }
            exist.setName(department.getName().trim());
        }
        if (department.getSort() != null) {
            exist.setSort(department.getSort());
        }
        if (department.getStatus() != null) {
            exist.setStatus(department.getStatus());
        }
        int result = teamDepartmentMapper.update(exist);
        if (result > 0) {
            return Result.success("更新成功", exist);
        }
        return Result.error("更新失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sysDeleteDepartment(Integer departmentId) {
        if (departmentId == null) {
            return Result.error("部门ID不能为空");
        }
        TeamDepartment exist = teamDepartmentMapper.selectById(departmentId);
        if (exist == null) {
            return Result.error("部门不存在");
        }
        int userCount = teamUserMapper.ifExistInDepartment(departmentId);
        if (userCount != 0) {
            return Result.error("该部门下有成员，无法删除");
        }
        int result = teamDepartmentMapper.deleteById(departmentId, LocalDateTime.now());
        if (result > 0) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @Override
    public Result getDepartmentList(Integer teamId) {
        if (teamId == null) {
            return Result.error("团队ID不能为空");
        }

        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 如果未开启多部门管理，返回空列表
        if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
            return Result.success("该团队未开启多部门管理", List.of());
        }

        List<TeamDepartment> departments = teamDepartmentMapper.selectByTeamId(teamId);
        if (departments != null && !departments.isEmpty()) {
            for (TeamDepartment dept : departments) {
                int count = teamUserMapper.ifExistInDepartment(dept.getId());
                dept.setCount(count);
            }
        }
        return Result.success("获取成功", departments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result setUserDepartment(Integer teamUserId, Integer departmentId, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 查询团队成员信息
        TeamUser teamUser = teamUserMapper.selectById(teamUserId);
        if (teamUser == null) {
            return Result.error("团队成员不存在");
        }

        // 检查操作者是否有权限
        TeamUser operator = teamUserMapper.selectByTeamIdAndUserId(teamUser.getTeamId(), userId, true);
        if (operator == null || operator.getType() > 1) {
            return Result.error("无权限操作");
        }

        // 如果指定了部门ID，检查部门是否存在且属于该团队
        if (departmentId != null) {
            TeamDepartment department = teamDepartmentMapper.selectById(departmentId);
            if (department == null) {
                return Result.error("部门不存在");
            }
            if (!department.getTeamId().equals(teamUser.getTeamId())) {
                return Result.error("部门不属于该团队");
            }
            if (department.getStatus() != 1) {
                return Result.error("部门已禁用");
            }
        }

        // 更新成员部门
        teamUser.setDepartmentId(departmentId);
        int result = teamUserMapper.update(teamUser);
        if (result > 0) {
            return Result.success("设置成功");
        }
        return Result.error("设置失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchSetUserDepartment(Integer teamId, List<Integer> teamUserIds, Integer departmentId, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 参数校验
        if (teamId == null) {
            return Result.error("团队ID不能为空");
        }
//        if (departmentId == null) {
//            return Result.error("部门ID不能为空");
//        }
        if (teamUserIds == null || teamUserIds.isEmpty()) {
            return Result.error("请选择需要编辑的成员");
        }

        // 检查团队是否存在且已开启多部门管理
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return Result.error("团队不存在");
        }
        if (team.getIsMultiDepartment() == null || team.getIsMultiDepartment() == 0) {
            return Result.error("该团队未开启多部门管理");
        }

        // 仅团队创建者和管理员可以批量编辑成员部门
        TeamUser operator = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (operator == null || operator.getType() > 1) {
            return Result.error("无权限操作");
        }

        // 检查目标部门是否存在、属于当前团队且状态正常
        if (departmentId != null) {
            TeamDepartment department = teamDepartmentMapper.selectById(departmentId);
            if (department == null) {
                return Result.error("部门不存在");
            }
            if (!department.getTeamId().equals(teamId)) {
                return Result.error("部门不属于该团队");
            }
            if (department.getStatus() == 0) {
                return Result.error("部门已禁用");
            }
        }

        // 查询需要批量编辑的成员，确保成员记录都存在且属于同一个团队
        List<TeamUser> teamUsers = teamUserMapper.selectByIds(teamUserIds);
        if (teamUsers == null || teamUsers.isEmpty()) {
            return Result.error("成员不存在");
        }
        Set<Integer> existIds = new HashSet<>();
        for (TeamUser item : teamUsers) {
            existIds.add(item.getId());
            if (!item.getTeamId().equals(teamId)) {
                return Result.error("存在不属于该团队的成员");
            }
        }
        if (existIds.size() != teamUserIds.size()) {
            return Result.error("部分成员不存在或已被移除，请刷新后重试");
        }

        // 批量更新成员所属部门
        int result = teamUserMapper.updateDepartmentByIds(teamId, teamUserIds, departmentId);
        if (result > 0) {
            return Result.success("批量编辑成功");
        }
        return Result.error("批量编辑失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result toggleMultiDepartment(Integer teamId, Integer isMultiDepartment, String token) {
        // 解析token获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        // 参数校验
        if (teamId == null) {
            return Result.error("团队ID不能为空");
        }
        if (isMultiDepartment == null || (isMultiDepartment != 0 && isMultiDepartment != 1)) {
            return Result.error("参数错误");
        }

        // 查询团队
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 检查用户是否有权限（只有创建者可以修改）
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (teamUser == null || teamUser.getType() != 0) {
            return Result.error("无权限操作，仅创建者可修改");
        }

        // 如果开启多部门管理
        if (isMultiDepartment == 1) {
            // 检查是否已开启
            if (team.getIsMultiDepartment() != null && team.getIsMultiDepartment() == 1) {
                return Result.error("已开启多部门管理");
            }

            // 更新团队设置
            team.setIsMultiDepartment(1);
            teamMapper.update(team);

            // 初始化默认部门
            // initDefaultDepartment(teamId);

            return Result.success("开启多部门管理成功");
        } else {
            // 关闭多部门管理
            team.setIsMultiDepartment(0);
            // 如果原来有部门 删除 并且把相关成员的部门ID置为null
            teamMapper.update(team);
            teamDepartmentMapper.deleteByTeamId(teamId);
            // 将相关成员的departmentId置为null
            teamUserMapper.clearDepartmentIdByTeamId(teamId);

            return Result.success("关闭多部门管理成功");
        }
    }

    @Override
    public Result getDepartmentUserList(Integer departmentId) {
        // 获取部门成员
        TeamUserSearchParam param = new TeamUserSearchParam();
        param.setDepartmentId(departmentId);

        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<TeamUserVo> teamUsers = teamUserMapper.selectList(param);
        return Result.success("操作成功", new PageInfo<>(teamUsers));
    }
}
