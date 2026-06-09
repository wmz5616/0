package com.zemcho.ddql.service.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.team.TeamDepartment;

import java.util.List;
import java.util.List;

/**
 * 团队部门Service接口
 */
public interface TeamDepartmentService {

    /**
     * 创建部门
     *
     * @param department 部门信息
     * @param token      用户token
     * @return 创建结果
     */
    Result createDepartment(TeamDepartment department, String token);

    /**
     * 更新部门
     *
     * @param department 部门信息
     * @param token      用户token
     * @return 更新结果
     */
    Result updateDepartment(TeamDepartment department, String token);

    /**
     * 删除部门
     *
     * @param departmentId 部门ID
     * @param token        用户token
     * @return 删除结果
     */
    /**
     * 新增部门（后台系统使用，无权限校验）
     */
    Result sysCreateDepartment(TeamDepartment department);

    /**
     * 更新部门（后台系统使用，无权限校验）
     */
    Result sysUpdateDepartment(TeamDepartment department);

    /**
     * 删除部门（后台系统使用，无权限校验）
     */
    Result sysDeleteDepartment(Integer departmentId);

    /**
     * 删除部门
     *
     * @param departmentId 部门ID
     * @param token        用户token
     * @return 删除结果
     */
    Result deleteDepartment(Integer departmentId, String token);


    /**
     * 获取团队的部门列表
     *
     * @param teamId 团队ID
     * @return 部门列表
     */
    Result getDepartmentList(Integer teamId);

    /**
     * 设置成员部门
     *
     * @param teamUserId   团队成员记录ID
     * @param departmentId 部门ID（null表示移除部门）
     * @param token        用户token
     * @return 设置结果
     */
    Result setUserDepartment(Integer teamUserId, Integer departmentId, String token);

    /**
     * 批量设置成员部门
     *
     * @param teamId       团队ID
     * @param teamUserIds  团队成员记录ID列表
     * @param departmentId 目标部门ID
     * @param token        用户token
     * @return 设置结果
     */
    Result batchSetUserDepartment(Integer teamId, List<Integer> teamUserIds, Integer departmentId, String token);

    /**
     * 切换团队多部门管理开关
     *
     * @param teamId           团队ID
     * @param isMultiDepartment 0关闭，1开启
     * @param token            用户token
     * @return 操作结果
     */
    Result toggleMultiDepartment(Integer teamId, Integer isMultiDepartment, String token);

    /**
     * 获取部门成员列表
     *
     * @param departmentId 部门ID
     * @return 成员列表
     */
    Result getDepartmentUserList(Integer departmentId);
}
