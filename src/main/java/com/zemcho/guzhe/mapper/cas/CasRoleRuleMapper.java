package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.entity.cas.CasRoleRule;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasRoleRuleMapper {
    /**
     * 批量插入角色-菜单关联数据
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<CasRoleRule> data);

    /**
     * 根据角色id删除角色-菜单关联数据
     *
     * @param roleIds
     * @return
     */
    Integer deleteByRoleIds(@Param("roleIds") Collection<Integer> roleIds);

    /**
     * 根据角色id删除角色-菜单关联数据
     *
     * @param roleId
     * @return
     */
    Integer deleteByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id查询菜单id
     *
     * @param roleId
     * @return
     */
    List<Integer> selectRuleIdByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id查询菜单id
     *
     * @param roleIds
     * @return
     */
    List<Integer> selectRuleIdByRoleIds(@Param("roleIds") Collection<Integer> roleIds);

    /**
     * 根据角色id查询菜单api
     *
     * @param roleIds
     * @return
     */
    List<String> selectRuleApiByRoleIds(@Param("roleIds") Collection<Integer> roleIds);
}
