package com.zemcho.ddql.service.business.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.CoinRuleParam;
import com.zemcho.ddql.entity.business.CoinRule;
import com.zemcho.ddql.mapper.business.CoinRuleMapper;
import com.zemcho.ddql.service.business.CoinRuleService;
import com.zemcho.ddql.service.business.ShopManagerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用币规则服务实现类
 */
@Service
public class ICoinRuleService implements CoinRuleService {

    @Autowired
    private CoinRuleMapper coinRuleMapper;
    @Autowired
    private ShopManagerService shopManagerService;

    @Override
    public Result addCoinRule(CoinRuleParam param,String token,Boolean isWechat) {
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,param.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        // 检查是否存在相同的起始金额规则
        CoinRule existingRules = coinRuleMapper.selectByShopId(param.getShopId());
        if(existingRules != null){
            return Result.error("操作失败");
        }

        CoinRule coinRule = new CoinRule();
        BeanUtils.copyProperties(param, coinRule);
        coinRule.setCreateTime(LocalDateTime.now());

        int result = coinRuleMapper.insert(coinRule);
        if (result > 0) {
            return Result.success("操作成功");
        }else {
        return Result.error("操作失败");
        }
    }

    @Override
    public Result updateCoinRule(CoinRuleParam param,String token,Boolean isWechat) {

        if (param.getId() == null || param.getId() <= 0) {
            return Result.error("参数错误");
        }

        CoinRule existingRule = coinRuleMapper.selectById(param.getId());
        if (existingRule == null) {
            return Result.error("参数错误");
        }
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,existingRule.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }

        CoinRule coinRule = new CoinRule();
        BeanUtils.copyProperties(param, coinRule);
        coinRule.setUpdateTime(LocalDateTime.now());

        int result = coinRuleMapper.update(coinRule);
        if (result > 0) {
            return Result.success("操作成功");
        }else {
            return Result.error("操作失败");
        }
    }


    @Override
    public Result getByShopId(SearchParam param,String token,Boolean isWechat) {
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,param.getSearchId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }

        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }

        CoinRule coinRule = coinRuleMapper.selectByShopId(param.getSearchId());
        return Result.success("操作成功", coinRule);
    }

}