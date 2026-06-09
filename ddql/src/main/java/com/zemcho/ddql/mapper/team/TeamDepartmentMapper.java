package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.entity.team.TeamDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团队部门Mapper
 */
@Mapper
public interface TeamDepartmentMapper {

    /**
     * 插入部门
     *
     * @param department 部门实体
     * @return 影响行数
     */
    int insert(@Param("department") TeamDepartment department);

    /**
     * 更新部门
     *
     * @param department 部门实体
     * @return 影响行数
     */
    int update(@Param("department") TeamDepartment department);

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    TeamDepartment selectById(@Param("id") Integer id);

    /**
     * 根据团队ID查询部门列表
     *
     * @param teamId 团队ID
     * @return 部门列表
     */
    List<TeamDepartment> selectByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据团队ID查询启用的部门列表（按排序）
     *
     * @param teamId 团队ID
     * @return 部门列表
     */
    List<TeamDepartment> selectActiveByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据团队ID删除部门列表（按排序）
     *
     * @param teamId 团队ID
     * @return 部门列表
     */
    int deleteByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据ID删除部门（软删除）
     *
     * @param id 部门ID
     * @param deleteTime 删除时间
     * @return 影响行数
     */
    int deleteById(@Param("id") Integer id, @Param("deleteTime") LocalDateTime deleteTime);

    /**
     * 根据团队ID统计部门数量
     *
     * @param teamId 团队ID
     * @return 部门数量
     */
    int countByTeamId(@Param("teamId") Integer teamId);

    /**
     * 查询团队下最大的排序值
     *
     * @param teamId 团队ID
     * @return 最大排序值
     */
    Integer selectMaxSortByTeamId(@Param("teamId") Integer teamId);

    // 根据id查询团队是否多部门管理
    Integer ifMultiDepartment(@Param("teamId") Integer teamId);
}
