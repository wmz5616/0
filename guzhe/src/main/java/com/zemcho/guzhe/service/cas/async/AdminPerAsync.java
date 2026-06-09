package com.zemcho.guzhe.service.cas.async;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.entity.cas.CasRole;
import com.zemcho.guzhe.mapper.cas.CasAdminRoleMapper;
import com.zemcho.guzhe.mapper.cas.CasRoleMapper;
import com.zemcho.guzhe.mapper.cas.CasRoleRuleMapper;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AdminPerAsync {
    @Autowired
    CasAdminRoleMapper casAdminRoleMapper;

    @Autowired
    CasRoleMapper casRoleMapper;

    @Autowired
    CasRoleRuleMapper casRoleRuleMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OtherConfig otherConfig;

    /**
     * 异步保存管理员权限缓存
     *
     * @param adminId
     */
    @Async("customAsyncThreadPool")
    public void asyncSaveAdminPermissionCache(Integer adminId) {
        saveAdminPermissionCache(adminId);
    }

    /**
     * 异步保存角色下的所有管理员权限缓存
     *
     * @param roleIds
     */
    @Async("customAsyncThreadPool")
    public void asyncSaveAdminPermissionCacheByRole(List<Integer> roleIds) {
        List<Integer> adminIds = casAdminRoleMapper.selectAdminIdByRoleIds(roleIds);
        if (adminIds != null && !adminIds.isEmpty()) {
            for (Integer adminId : adminIds) {
                saveAdminPermissionCache(adminId);
            }
        }
    }

    /**
     * 保存管理员权限缓存
     *
     * @param adminId
     */
    public void saveAdminPermissionCache(Integer adminId) {
        List<Integer> roleIds = new ArrayList<>();
        List<String> authRules = new ArrayList<>();

        List<Integer> adminRoleIds = casAdminRoleMapper.selectRoleIdByAdminId(adminId, null);
        if (adminRoleIds != null && !adminRoleIds.isEmpty()) {
            SearchParam roleParam = new SearchParam();
            roleParam.setSearchIds(adminRoleIds);
            roleParam.setSearchIntStatus(1);
            List<CasRole> roleList = casRoleMapper.selectLists(roleParam);
            if (roleList != null && !roleList.isEmpty()) {
                for (CasRole role : roleList) {
                    roleIds.add(role.getId());
                }

                authRules = casRoleRuleMapper.selectRuleApiByRoleIds(roleIds);
                if (authRules == null) {
                    authRules = new ArrayList<>();
                }
            }
        }

        Map<String, Object> adminPermissionData = new HashMap<>();
        adminPermissionData.put("roleIds", roleIds);
        adminPermissionData.put("authRules", authRules);

        redisUtil.hashPutAll(Constant.ADMIN_PERMISSION_DATA_PREFIX + adminId, adminPermissionData);
    }

    /**
     * 异步删除管理员权限缓存
     *
     * @param adminIds
     */
    @Async("customAsyncThreadPool")
    public void asyncDelAdminPermissionCache(List<Integer> adminIds) {
        for (Integer adminId : adminIds) {
            redisUtil.del(Constant.ADMIN_PERMISSION_DATA_PREFIX + adminId);
        }
    }
}
