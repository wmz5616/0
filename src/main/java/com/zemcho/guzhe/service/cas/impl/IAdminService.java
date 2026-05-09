package com.zemcho.guzhe.service.cas.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.controller.cas.param.AdminSaveParam;
import com.zemcho.guzhe.controller.cas.vo.AdminListVO;
import com.zemcho.guzhe.controller.cas.vo.AdminRoleListVO;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.cas.CasAdminRole;
import com.zemcho.guzhe.entity.cas.CasRole;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.cas.CasAdminRoleMapper;
import com.zemcho.guzhe.mapper.cas.CasRoleMapper;
import com.zemcho.guzhe.service.cas.AdminService;
import com.zemcho.guzhe.service.cas.async.AdminPerAsync;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.decode.HashServiceUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @title: IAdminService
 * @Description:
 * @Date: 2025/5/9 17:15
 */
@Service
public class IAdminService implements AdminService {
    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    CasAdminRoleMapper casAdminRoleMapper;

    @Autowired
    CasRoleMapper casRoleMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    OtherConfig otherConfig;

    @Autowired
    AdminPerAsync adminPerAsync;

    @Resource(name = "redisTemplate")
    RedisTemplate redisTemplate;

    /**
     * 新增管理员
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result addAdmin(AdminSaveParam param, AuthAttrData authAttrData) {
        String account = param.getAccount();
        Boolean existAccount = casAdminMapper.existAccount(null, account);
        if (existAccount) {
            return Result.error("手机号已存在");
        }

        List<Integer> roleIds = param.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            SearchParam roleParam = new SearchParam();
            roleParam.setSearchIds(roleIds);
            List<CasRole> roleList = casRoleMapper.selectLists(roleParam);
            if (roleList == null || roleIds.size() != roleList.size()) {
                return Result.error("所选角色错误，请重新选择");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String password = param.getPassword();
        if (password == null || password.equals("")) {
            return Result.error("登录密码为空!");
        }
        password = HashServiceUtil.computeHash(password);

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer adminId;
        try {
            CasAdmin adminData = new CasAdmin();
            adminData.setAccount(account);
            adminData.setPassword(password);
            adminData.setName(param.getName());
            adminData.setStatus(param.getStatus());
            adminData.setRemark(param.getRemark());
            adminData.setCreateTime(now);
            casAdminMapper.insert(adminData);
            adminId = adminData.getId();

            if (roleIds != null && !roleIds.isEmpty()) {
                List<CasAdminRole> data = new ArrayList<>();
                for (Integer roleId : roleIds) {
                    CasAdminRole adminRole = new CasAdminRole();
                    adminRole.setAdminId(adminId);
                    adminRole.setRoleId(roleId);
                    data.add(adminRole);
                }
                casAdminRoleMapper.insertAll(data);
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //更新用户redis数据
        adminPerAsync.asyncSaveAdminPermissionCache(adminId);

        return Result.success("操作成功");
    }

    /**
     * 编辑管理员
     *
     * @param param
     * @param authAttrData
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result updateAdmin(AdminSaveParam param, AuthAttrData authAttrData, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer id = param.getId();
        if (id == null) {
            return Result.error("参数异常");
        }

        CasAdmin adminInfo = casAdminMapper.selectById(id);
        if (adminInfo == null) {
            return Result.error("记录不存在");
        }

        List<Integer> roleIds = param.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            SearchParam roleParam = new SearchParam();
            roleParam.setSearchIds(roleIds);
            List<CasRole> roleList = casRoleMapper.selectLists(roleParam);
            if (roleList == null || roleIds.size() != roleList.size()) {
                return Result.error("所选角色错误，请重新选择");
            }
        }

        String password = "";
        if (param.getPassword() != null && !param.getPassword().equals("")) {
            if (otherConfig.getSuperAdminId().equals(id) && !authJwtData.getAdminId().equals(id)) {
                return Result.error("您无权修改超级管理员密码");
            }
            password = HashServiceUtil.computeHash(param.getPassword());
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            CasAdmin adminData = new CasAdmin();
            adminData.setId(id);
            adminData.setName(param.getName());
            adminData.setStatus(param.getStatus());
            adminData.setRemark(param.getRemark());
            if (!password.equals("")) {
                adminData.setPassword(password);
            }
            casAdminMapper.update(adminData);

            casAdminRoleMapper.deleteByAdminId(id);
            if (roleIds != null && !roleIds.isEmpty()) {
                List<CasAdminRole> data = new ArrayList<>();
                for (Integer roleId : roleIds) {
                    CasAdminRole adminRole = new CasAdminRole();
                    adminRole.setAdminId(id);
                    adminRole.setRoleId(roleId);
                    data.add(adminRole);
                }
                casAdminRoleMapper.insertAll(data);
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //更新用户redis数据
        adminPerAsync.asyncSaveAdminPermissionCache(id);

        if (!password.equals("")) {
            redisTemplate.delete(Constant.LOGIN_ERROR_PREFIX + adminInfo.getAccount());
        }

        return Result.success("操作成功");
    }

    /**
     * 获取管理员列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result getAdminLists(SearchParam param, AuthAttrData authAttrData) {
        Integer searchRoleId = param.getSearchField1();
        if (searchRoleId != null) {
            List<Integer> roleIds = new ArrayList<>();
            roleIds.add(searchRoleId);
            param.setSearchIds(roleIds);
        }

        List<String> orderFieldList = new ArrayList<>();
        orderFieldList.add("status");
        orderFieldList.add("create_time");
        String orderField = param.getOrderField();
        String orderType = param.getOrderType();

        if (orderField != null && orderType != null) {
            if (!orderFieldList.contains(orderField)) {
                return Result.error("未知排序字段");
            }
            orderType = orderType.toUpperCase();
            if (!"DESC".equals(orderType) && !"ASC".equals(orderType)) {
                return Result.error("排序方式错误");
            }
            param.setOrderType(orderType);
        } else {
            param.setOrderField("id");
            param.setOrderType("DESC");
        }
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<AdminListVO> list = casAdminMapper.selectLists(param);
        if (list != null && !list.isEmpty()) {
            List<Integer> adminIds = list.stream().map(AdminListVO::getId).collect(Collectors.toList());
            List<AdminRoleListVO> adminRoles = casAdminRoleMapper.selectByAdminIds(adminIds);
            Map<Integer, List<AdminRoleListVO>> adminRoleMap = new HashMap<>();
            if (adminRoles != null && !adminRoles.isEmpty()) {
                adminRoleMap = adminRoles.stream().collect(Collectors.groupingBy(AdminRoleListVO::getAdminId));
            }

            for (AdminListVO item : list) {
                item.setRoles(adminRoleMap.get(item.getId()));
            }
        }
        PageInfo<AdminListVO> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 编辑管理员状态
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result setStatus(ChangeParam param, AuthAttrData authAttrData) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        casAdminMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    /**
     * 删除管理员
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result deleteAdmin(DeleteParam param, AuthAttrData authAttrData) {
        List<Integer> ids = new ArrayList<>(param.getDeleteIds());

        if (ids.contains(otherConfig.getSuperAdminId())) {
            return Result.error("超级管理员不可删除");
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            casAdminMapper.deleteByIds(ids);

            casAdminRoleMapper.deleteByAdminIds(ids);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //删除用户redis数据
        adminPerAsync.asyncDelAdminPermissionCache(ids);

        return Result.success("操作成功");
    }


}
