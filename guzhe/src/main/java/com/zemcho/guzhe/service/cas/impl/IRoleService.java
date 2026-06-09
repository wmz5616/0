package com.zemcho.guzhe.service.cas.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.ChangeOneParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.RoleRuleParam;
import com.zemcho.guzhe.controller.cas.param.RoleSaveParam;
import com.zemcho.guzhe.controller.cas.vo.RuleTreeVO;
import com.zemcho.guzhe.entity.cas.CasRole;
import com.zemcho.guzhe.entity.cas.CasRoleRule;
import com.zemcho.guzhe.entity.cas.CasRule;
import com.zemcho.guzhe.mapper.cas.*;
import com.zemcho.guzhe.service.cas.RoleService;
import com.zemcho.guzhe.service.cas.async.AdminPerAsync;
import com.zemcho.guzhe.util.ListTreeConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @title: IRoleService
 * @Description:
 * @Date: 2025/5/7 14:18
 */
@Service
public class IRoleService implements RoleService {
    @Autowired
    CasRoleMapper casRoleMapper;

    @Autowired
    CasRoleRuleMapper casRoleRuleMapper;

    @Autowired
    CasAdminRoleMapper casAdminRoleMapper;

    @Autowired
    CasRuleMapper casRuleMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    AdminPerAsync adminPerAsync;

    /**
     * 新增角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result addRole(RoleSaveParam param, AuthAttrData authAttrData) {
        String name = param.getName();
        Boolean existName = casRoleMapper.existName(null, name);
        if (existName) {
            return Result.error("角色名称已存在");
        }

        LocalDateTime now = LocalDateTime.now();

        CasRole role = new CasRole();
        role.setName(name);
        role.setStatus(param.getStatus());
        role.setRemark(param.getRemark());
        role.setCreateTime(now);
        casRoleMapper.insert(role);

        return Result.success("操作成功");
    }

    /**
     * 编辑角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result updateRole(RoleSaveParam param, AuthAttrData authAttrData) {
        Integer id = param.getId();
        if (id == null) {
            return Result.error("参数异常");
        }

        CasRole roleInfo = casRoleMapper.selectById(id);
        if (roleInfo == null) {
            return Result.error("记录不存在");
        }

        String name = param.getName();
        Boolean existName = casRoleMapper.existName(id, name);
        if (existName) {
            return Result.error("角色名称已存在");
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            CasRole role = new CasRole();
            role.setId(id);
            role.setName(name);
            role.setStatus(param.getStatus());
            role.setRemark(param.getRemark());
            casRoleMapper.update(role);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        if (!roleInfo.getStatus().equals(param.getStatus())) {
            adminPerAsync.asyncSaveAdminPermissionCacheByRole(List.of(id));
        }

        return Result.success("操作成功");
    }

    /**
     * 复制角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional
    public Result copyRole(SearchParam param, AuthAttrData authAttrData) {
        Integer copyId = param.getSearchId();
        if (copyId == null) {
            return Result.error("参数异常");
        }

        CasRole roleInfo = casRoleMapper.selectById(copyId);
        if (roleInfo == null) {
            return Result.error("记录不存在");
        }

        List<Integer> ruleIds = casRoleRuleMapper.selectRuleIdByRoleId(copyId);

        LocalDateTime now = LocalDateTime.now();

        CasRole role = new CasRole();
        role.setName(roleInfo.getName() + "-复制");
        role.setStatus(roleInfo.getStatus());
        role.setRemark(roleInfo.getRemark());
        role.setCreateTime(now);
        casRoleMapper.insert(role);
        Integer roleId = role.getId();

        if (ruleIds != null && !ruleIds.isEmpty()) {
            List<CasRoleRule> ruleData = new ArrayList<>();
            for (Integer ruleId : ruleIds) {
                CasRoleRule roleRule = new CasRoleRule();
                roleRule.setRoleId(roleId);
                roleRule.setRuleId(ruleId);
                ruleData.add(roleRule);
            }
            casRoleRuleMapper.insertAll(ruleData);
        }

        return Result.success("操作成功");
    }

    /**
     * 获取角色列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result getRoleLists(SearchParam param, AuthAttrData authAttrData) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<CasRole> list = casRoleMapper.selectLists(param);
        PageInfo<CasRole> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 删除角色
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional
    public Result deleteRole(DeleteParam param, AuthAttrData authAttrData) {
        List<Integer> ids = new ArrayList<>(param.getDeleteIds());

        //判断是否已有管理员绑定
        List<Integer> adminIds = casAdminRoleMapper.selectAdminIdByRoleIds(ids);
        if (adminIds != null && !adminIds.isEmpty()) {
            return Result.error("所选记录中已有管理员绑定，请先解除绑定");
        }

        casRoleMapper.deleteByIds(ids);
        casAdminRoleMapper.deleteByRoleIds(ids);
        casRoleRuleMapper.deleteByRoleIds(ids);

        return Result.success("操作成功");
    }

    /**
     * 编辑角色状态
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result setStatus(ChangeOneParam param, AuthAttrData authAttrData) {
        Integer id = param.getChangeId();

        CasRole roleInfo = casRoleMapper.selectById(id);
        if (roleInfo == null) {
            return Result.error("记录不存在");
        }

        if (param.getStatus().equals(roleInfo.getStatus())) {
            return Result.success("操作成功!");
        }

        CasRole role = new CasRole();
        role.setId(id);
        role.setStatus(param.getStatus());
        casRoleMapper.update(role);

        //更新角色下的用户的redis数据
        adminPerAsync.asyncSaveAdminPermissionCacheByRole(List.of(id));

        return Result.success("操作成功");
    }

    /**
     * 获取角色菜单列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getRuleTree(SearchParam param) {
        Integer roleId = param.getSearchId();
        if (roleId == null) {
            return Result.error("参数异常");
        }

        CasRole roleInfo = casRoleMapper.selectById(roleId);
        if (roleInfo == null) {
            return Result.error("记录不存在");
        }

        List<Integer> selectedRuleIds = casRoleRuleMapper.selectRuleIdByRoleId(roleId);
        if (selectedRuleIds == null) {
            selectedRuleIds = new ArrayList<>();
        }

        SearchParam ruleParam = new SearchParam();
        List<RuleTreeVO> ruleList = casRuleMapper.selectLists(ruleParam);
        for (RuleTreeVO item : ruleList) {
            item.setIsSelected(selectedRuleIds.contains(item.getId()));
        }

        //转树结构返回
        List<RuleTreeVO> treeData = new ArrayList<>();
        if (!ruleList.isEmpty()) {
            treeData = ListTreeConvertUtil.listToTreeByMultiNode(ruleList, "parentId", "id", "children");
        }

        return Result.success("获取成功", treeData);
    }

    /**
     * 更新角色菜单权限
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result updateRoleRule(RoleRuleParam param, AuthAttrData authAttrData) {
        Integer roleId = param.getRoleId();
        List<Integer> ruleIds = param.getRuleIds();

        CasRole roleInfo = casRoleMapper.selectById(roleId);
        if (roleInfo == null) {
            return Result.error("记录不存在");
        }

        List<CasRule> rules = casRuleMapper.selectByIds(ruleIds);
        if (rules.size() != ruleIds.size()) {
            return Result.error("菜单数据有误");
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            casRoleRuleMapper.deleteByRoleId(roleId);
            List<CasRoleRule> data = new ArrayList<>();
            for (Integer ruleId : ruleIds) {
                CasRoleRule roleRule = new CasRoleRule();
                roleRule.setRoleId(roleId);
                roleRule.setRuleId(ruleId);
                data.add(roleRule);
            }
            casRoleRuleMapper.insertAll(data);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //更新角色下的用户的redis数据
        adminPerAsync.asyncSaveAdminPermissionCacheByRole(List.of(roleId));

        return Result.success("操作成功");
    }
}
