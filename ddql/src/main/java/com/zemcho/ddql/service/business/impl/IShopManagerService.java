package com.zemcho.ddql.service.business.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.business.param.ShopManagerParam;
import com.zemcho.ddql.entity.business.ShopManager;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.ShopManagerMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.business.ShopManagerService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺管理者服务实现类
 */
@Service
public class IShopManagerService implements ShopManagerService {

    @Autowired
    private ShopManagerMapper shopManagerMapper;
    @Autowired
    private CasUserMapper casUserMapper;

    @Override
    public Result addShopManager(ShopManagerParam param,String token,Boolean isWechat) {
        if (isWechat) {
            Result checkResult = checkWechatUserIsShopManager(token,param.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        // 检查是否已存在相同手机号的管理者
        List<ShopManager> existingManagers = shopManagerMapper.selectByShopId(param.getShopId());

        if (existingManagers != null) {
            // 检查数量
            if (existingManagers.size() >= 4) {
                return Result.error("每个店铺最多允许添加4个管理者");
            }
            // 检查手机号是否合法
            for (ShopManager manager : existingManagers) {
                if (manager.getPhone().equals(param.getPhone()) && !manager.getName().equals(param.getName())) {
                    return Result.error("该手机号已被其他管理者使用");
                }
            }
            // 检查排序值是否正确
            List<Integer> existingManagerSort = null;
            existingManagerSort = existingManagers.stream().map(ShopManager::getSort).sorted().toList();
            if (existingManagerSort.contains(param.getSort())) {
                return Result.error("该排序值已存在");
            }
            // 排序值为一才是店长
            if ((!param.getSort().equals(1) && param.getHeadManager().equals(1)) ||
                    (param.getSort().equals(1) && !param.getHeadManager().equals(1))) {
                return Result.error("参数错误");
            }
        }


        ShopManager shopManager = new ShopManager();
        BeanUtils.copyProperties(param, shopManager);

        int result = shopManagerMapper.insert(shopManager);
        if (result > 0) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @Override
    public Result updateShopManager(ShopManagerParam param) {
        if (param.getId() == null || param.getId() <= 0) {
            return Result.error("参数错误");
        }

        // 校验电话号码
        if (param.getPhone() != null && !"".equals(param.getPhone())
                && !param.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号码");
        }

        ShopManager existingManager = shopManagerMapper.selectById(param.getId());
        if (existingManager == null) {
            return Result.error("参数错误");
        }

        // 如果手机号有变更，检查是否与其他管理者冲突
        if (!existingManager.getPhone().equals(param.getPhone())) {
            List<ShopManager> managers = shopManagerMapper.selectByShopId(param.getShopId());
            for (ShopManager manager : managers) {
                if (!manager.getId().equals(param.getId()) && manager.getPhone().equals(param.getPhone())) {
                    return Result.error("该手机号已被其他管理者使用");
                }
            }
        }

        ShopManager shopManager = new ShopManager();
        BeanUtils.copyProperties(param, shopManager);

        int result = shopManagerMapper.update(shopManager);
        if (result > 0) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @Override
    public Result deleteShopManager(DeleteParam param,String token,Boolean isWechat) {
        if (param.getDeleteIds() == null || param.getDeleteIds().isEmpty()) {
            return Result.error("参数错误");
        }
        // 检查是否有店长 店长不能删
        List<ShopManager> shopManagers = shopManagerMapper.selectByIds(param.getDeleteIds());
        if (isWechat) {
            Result checkResult = checkWechatUserIsShopManager(token,shopManagers.get(0).getShopId() );
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        List<Integer> managerIds = shopManagers.stream().filter(manager -> manager.getSort().equals(1)).map(ShopManager::getId).toList();
        if (!managerIds.isEmpty()) {
            return Result.error("参数错误");
        }
        shopManagerMapper.deleteByIds(new ArrayList<>(param.getDeleteIds()));
        return Result.success("操作成功");
    }


    @Override
    public Result getByShopId(SearchParam param,String token,Boolean isWechat) {
        if (isWechat) {
            Result checkResult = checkWechatUserIsShopManager(token, param.getSearchField1());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        if (param.getSearchField1() == null) {
            return Result.error("商家ID不能为空");
        }

        List<ShopManager> managers = shopManagerMapper.selectByShopId(param.getSearchField1());
        return Result.success("查询成功", managers);
    }

    /**
     * 验证微信用户是否为指定店铺的管理者
     *
     * @param token
     * @param shopId
     * @return
     */
    @Override
    public Result checkWechatUserIsShopManager(String token, Integer shopId) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        ShopManager shopManager = shopManagerMapper.selectByShopIdAndPhone(shopId, userInfo.getPhone());
        if (shopManager == null) {
            return Result.error("您不是该商家的管理者");
        }

        return Result.success("验证通过", shopManager);
    }
}