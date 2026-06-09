package com.zemcho.guzhe.service.cas;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.ChangeOneParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.RoleRuleParam;
import com.zemcho.guzhe.controller.cas.param.RoleSaveParam;

public interface RoleService {
    /**
     * 新增角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result addRole(RoleSaveParam param, AuthAttrData authAttrData);

    /**
     * 编辑角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result updateRole(RoleSaveParam param, AuthAttrData authAttrData);

    /**
     * 复制角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result copyRole(SearchParam param, AuthAttrData authAttrData);

    /**
     * 获取角色列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result getRoleLists(SearchParam param, AuthAttrData authAttrData);

    /**
     * 删除角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result deleteRole(DeleteParam param, AuthAttrData authAttrData);

    /**
     * 编辑角色状态
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result setStatus(ChangeOneParam param, AuthAttrData authAttrData);

    /**
     * 获取角色菜单列表
     *
     * @param param
     * @return
     */
    Result getRuleTree(SearchParam param);

    /**
     * 更新角色菜单权限
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result updateRoleRule(RoleRuleParam param, AuthAttrData authAttrData);
}
