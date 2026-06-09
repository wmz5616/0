package com.zemcho.ddql.service.recharge.impl;

import com.zemcho.ddql.common.Result;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeConfigParam;
import com.zemcho.ddql.entity.recharge.RechargeConfig;
import com.zemcho.ddql.mapper.recharge.RechargeConfigMapper;
import com.zemcho.ddql.service.recharge.RechargeConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 充值配置服务实现类
 *
 * @author Ryan
 */
@Service
public class RechargeConfigServiceImpl implements RechargeConfigService {

    @Autowired
    private RechargeConfigMapper rechargeConfigMapper;

    @Override
    public Result addRechargeConfig(RechargeConfigParam param) {
        if (true) {
            return Result.error("暂不支持新增");
        }
        if (param.getEnableCustomAmount().equals(1)) {
            if (param.getMinAmount() == null || param.getMinAmount() <= 0) {
                return Result.error("最低充值金额错误");
            }
        }
        param.setCreateTime(LocalDateTime.now());
        param.setUpdateTime(LocalDateTime.now());
        RechargeConfig rechargeConfig = new RechargeConfig();
        BeanUtils.copyProperties(param, rechargeConfig);
        rechargeConfigMapper.insert(rechargeConfig);
        return Result.success("操作成功");
    }

    @Override
    public Result updateRechargeConfig(RechargeConfigParam param) {
        Integer id = param.getId();
        if (id == null || id <= 0) {
            return Result.error("id错误");
        }
        if (param.getEnableCustomAmount().equals(1)) {
            if (param.getMinAmount() == null || param.getMinAmount() <= 0) {
                return Result.error("最低充值金额错误");
            }
        }
        RechargeConfig rechargeConfig = rechargeConfigMapper.selectById(id);
        if (rechargeConfig == null) {
            return Result.error("记录不存在");
        }
        BeanUtils.copyProperties(param, rechargeConfig);
        rechargeConfigMapper.update(rechargeConfig);
        return Result.success("操作成功");
    }

    @Override
    public Result getRechargeConfigInfo(SearchParam param) {
//        Integer id = param.getSearchId();
//        if (id == null) {
//            return Result.error("参数异常");
//        }
//        RechargeConfig config = rechargeConfigMapper.selectById(id);
        RechargeConfig config = rechargeConfigMapper.select();
        return Result.success("获取成功", config);
    }

}
