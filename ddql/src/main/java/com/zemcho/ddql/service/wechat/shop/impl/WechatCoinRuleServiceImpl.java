package com.zemcho.ddql.service.wechat.shop.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.wechat.shop.WechatCoinRuleService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author HXH
 */
@Service
public class WechatCoinRuleServiceImpl implements WechatCoinRuleService {

    @Autowired
    private CasUserMapper casUserMapper;
    @Override
    public Result getUserGoldCoin(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        return Result.success("成功", userInfo.getGoldCoin());
    }
}
