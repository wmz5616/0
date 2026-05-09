package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.controller.cas.vo.AdminRoleListVO;
import com.zemcho.guzhe.entity.cas.CasAdminRole;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasAdminRoleMapper {
    /**
     * 批量新增管理员-角色关联数据
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<CasAdminRole> data);

    /**
     * 根据角色id查询管理员id
     *
     * @param roleIds
     * @return
     */
    List<Integer> selectAdminIdByRoleIds(@Param("roleIds") Collection<Integer> roleIds);

    /**
     * 根据角色id删除管理员-角色关联数据
     *
     * @param roleIds
     * @return
     */
    Integer deleteByRoleIds(@Param("roleIds") Collection<Integer> roleIds);

    /**
     * 根据管理员id删除管理员-角色关联数据
     *
     * @param adminId
     * @return
     */
    Integer deleteByAdminId(@Param("adminId") Integer adminId);

    /**
     * 根据管理员id删除管理员-角色关联数据
     *
     * @param adminIds
     * @return
     */
    Integer deleteByAdminIds(@Param("adminIds") Collection<Integer> adminIds);

    /**
     * 根据管理员id查询角色id
     *
     * @param adminId
     * @param status
     * @return
     */
    List<Integer> selectRoleIdByAdminId(@Param("adminId") Integer adminId, @Param("status") Integer status);

    /**
     * 根据管理员id查询管理员-角色关联数据
     *
     * @param adminIds
     * @return
     */
    List<AdminRoleListVO> selectByAdminIds(@Param("adminIds") Collection<Integer> adminIds);
}
