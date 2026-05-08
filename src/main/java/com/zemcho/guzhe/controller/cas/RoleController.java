package com.zemcho.guzhe.controller.cas;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.ChangeOneParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.RoleRuleParam;
import com.zemcho.guzhe.controller.cas.param.RoleSaveParam;
import com.zemcho.guzhe.service.cas.RoleService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @title: RoleController
 * @Description:
 * @Date: 2025/5/7 14:15
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    /**
     * 新增角色
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "新增角色", module = "角色管理")
    @RequestMapping("/add")
    public Result addRole(@Validated @RequestBody RoleSaveParam param, BindingResult result,
                          @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.addRole(param, authAttrData);
    }

    /**
     * 编辑角色
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "编辑角色", module = "角色管理")
    @RequestMapping("/update")
    public Result updateRole(@Validated @RequestBody RoleSaveParam param, BindingResult result,
                             @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.updateRole(param, authAttrData);
    }

    /**
     * 复制角色
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "复制角色", module = "角色管理")
    @RequestMapping("/copy")
    public Result copyRole(@Validated @RequestBody SearchParam param, BindingResult result,
                           @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.copyRole(param, authAttrData);
    }

    /**
     * 获取角色列表
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/lists")
    public Result getRoleLists(@Validated @RequestBody SearchParam param, BindingResult result,
                               @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.getRoleLists(param, authAttrData);
    }

    /**
     * 删除角色
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "删除角色", module = "角色管理")
    @RequestMapping("/delete")
    public Result deleteRole(@Validated @RequestBody DeleteParam param, BindingResult result,
                             @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.deleteRole(param, authAttrData);
    }

    /**
     * 编辑角色状态
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "编辑角色状态", module = "角色管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeOneParam param, BindingResult result,
                            @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.setStatus(param, authAttrData);
    }

    /**
     * 获取角色菜单列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/rule/tree")
    public Result getRuleTree(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        return roleService.getRuleTree(param);
    }

    /**
     * 更新角色菜单权限
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "更新角色菜单权限", module = "角色管理")
    @RequestMapping("/rule/update")
    public Result updateRoleRule(@Validated @RequestBody RoleRuleParam param, BindingResult result,
                                 @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return roleService.updateRoleRule(param, authAttrData);
    }
}
