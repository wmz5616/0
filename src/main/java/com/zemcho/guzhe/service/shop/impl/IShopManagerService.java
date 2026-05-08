package com.zemcho.guzhe.service.shop.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.shop.param.ShopManagerParam;
import com.zemcho.guzhe.controller.wechat.shop.param.WechatShopManagerParam;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.shop.ShopManager;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.shop.ShopManagerMapper;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.util.Constant;
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
    public Result addShopManager(ShopManagerParam param) {
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

    /**
     * 删除店铺管理者
     *
     * @param param    删除参数
     * @param token
     * @param isWechat
     * @return
     */
    @Override
    public Result deleteShopManager(DeleteParam param, String token, Boolean isWechat) {
        if (param.getDeleteIds() == null || param.getDeleteIds().isEmpty()) {
            return Result.error("参数错误");
        }

        // 检查是否有店长 店长不能删
        List<ShopManager> shopManagers = shopManagerMapper.selectByIds(param.getDeleteIds());
        if (shopManagers == null || shopManagers.isEmpty()) {
            return Result.error("参数错误");
        }
        for (ShopManager item : shopManagers) {
            if (item.getSort().equals(1) || item.getHeadManager().equals(1)) {
                return Result.error("店长不能删除");
            }
        }

        if (isWechat) {
            Integer shopId = shopManagers.get(0).getShopId();
            Result checkResult = checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }
            ShopManager shopManagerInfo = (ShopManager) checkResult.getData();
            if (!shopManagerInfo.getHeadManager().equals(1)) {
                return Result.error("您不是该商家店长，不可操作");
            }
        }

        shopManagerMapper.deleteByIds(new ArrayList<>(param.getDeleteIds()));

        return Result.success("操作成功");
    }

    /**
     * 根据商家ID查询店铺管理者列表
     *
     * @param param    查询参数(包含shopId)
     * @param token
     * @param isWechat
     * @return
     */
    @Override
    public Result getByShopId(SearchParam param, String token, Boolean isWechat) {
        if (param.getSearchField1() == null) {
            return Result.error("商家ID不能为空");
        }

        if (isWechat) {
            Result checkResult = checkWechatUserIsShopManager(token, param.getSearchField1());
            if (!checkResult.success()) {
                return checkResult;
            }
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

    /**
     * 小程序端--新增商家管理者
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result addShopManagerByWechat(WechatShopManagerParam param, String token) {
        Integer shopId = param.getShopId();

        Result checkResult = checkWechatUserIsShopManager(token, shopId);
        if (!checkResult.success()) {
            return checkResult;
        }
        ShopManager shopManagerInfo = (ShopManager) checkResult.getData();
        if (!shopManagerInfo.getHeadManager().equals(1)) {
            return Result.error("您不是该商家店长，不可操作");
        }

        CasUser userInfo = casUserMapper.selectById(param.getUserId());
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getPhone() == null || userInfo.getPhone().isEmpty()) {
            return Result.error("该用户暂未绑定手机号，不可成为管理员");
        }
        if (!userInfo.getHasCertification().equals(1)) {
            return Result.error("该用户暂未实名认证，不可成为管理员");
        }

        // 获取已存在的管理者信息
        List<ShopManager> existingManagers = shopManagerMapper.selectByShopId(shopId);

        Integer maxSort = 1;
        if (existingManagers != null) {
            // 检查数量
            if (existingManagers.size() >= 4) {
                return Result.error("每个商家最多允许添加4个管理者");
            }

            // 检查手机号是否合法
            for (ShopManager manager : existingManagers) {
                if (manager.getPhone().equals(userInfo.getPhone())) {
                    return Result.error("该手机号已被其他管理者使用");
                }
            }

            maxSort = existingManagers.stream().map(ShopManager::getSort).max(Integer::compareTo).orElse(1);
        }

        ShopManager shopManager = new ShopManager();
        shopManager.setPhone(userInfo.getPhone());
        shopManager.setName(userInfo.getName());
        if (param.getHeadManager().equals(1)) {  //店长
            shopManager.setId(shopManagerInfo.getId());
            shopManagerMapper.update(shopManager);
        } else {  //管理员
            shopManager.setShopId(shopId);
            shopManager.setSort(maxSort + 1);
            shopManager.setHeadManager(0);
            shopManagerMapper.insert(shopManager);
        }

        return Result.success("操作成功");
    }
}