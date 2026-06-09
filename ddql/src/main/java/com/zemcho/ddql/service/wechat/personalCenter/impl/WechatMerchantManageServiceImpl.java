package com.zemcho.ddql.service.wechat.personalCenter.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantCancelParam;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatMerchantManageListParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatMerchantManageListVo;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.ShopManagerMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.wechat.personalCenter.WechatMerchantManageService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序商家管理服务实现
 */
@Service
public class WechatMerchantManageServiceImpl implements WechatMerchantManageService {
    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopManagerMapper shopManagerMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询当前登录用户管理的商家列表
     *
     * @param param 分页参数
     * @param token 小程序 token
     * @return 商家列表
     */
    @Override
    public Result list(WechatMerchantManageListParam param, String token) {
        // 先从 token 中解析当前登录用户。
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 商家管理人信息是按手机号存储在 shop_manager 表中的，因此需要先拿到用户手机号。
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null || userInfo.getPhone() == null || userInfo.getPhone().isEmpty()) {
            return Result.error("用户信息不存在");
        }

        // 查询当前手机号管理的、已审核通过且未注销的商家。
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<WechatMerchantManageListVo> list = shopManagerMapper.selectWechatManagedShopList(userInfo.getPhone());

        return Result.success("获取成功", new PageInfo<>(list));
    }

    /**
     * 注销商家
     *
     * @param param 商家ID
     * @param token 小程序 token
     * @return 操作结果
     */
    @Override
    public Result cancel(WechatMerchantCancelParam param, String token) {
        // 先校验当前登录用户。
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null || userInfo.getPhone() == null || "".equals(userInfo.getPhone())) {
            return Result.error("用户信息不存在");
        }

        Integer shopId = param.getShopId();
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.error("商家不存在");
        }
        if (shop.getShopStatus() != null && shop.getShopStatus().equals(2)) {
            return Result.error("商家已注销");
        }

        // 按你的规则，只有当前商家的店长才能执行注销。
        Integer headManagerCount = shopManagerMapper.countHeadManagerByShopIdAndPhone(shopId, userInfo.getPhone());
        if (headManagerCount == null || headManagerCount <= 0) {
            return Result.error("只有店长才可注销商家");
        }

        // 注销后更新商家状态为已注销，列表查询将不再返回该商家。
        shopMapper.updateShopStatus(shopId, 2);

        return Result.success("注销成功");
    }
}
