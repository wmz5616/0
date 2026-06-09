package com.zemcho.ddql.service.recharge;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeConfigParam;
import com.zemcho.ddql.entity.recharge.RechargeConfig;


/**
 * 充值配置服务接口
 *
 * @author Ryan
 */
public interface RechargeConfigService {

    /**
     * 新增充值配置
     */
    Result addRechargeConfig(RechargeConfigParam param);

    /**
     * 编辑充值配置
     */
    Result updateRechargeConfig(RechargeConfigParam param);

    /**
     * 获取充值配置详情
     */
    Result getRechargeConfigInfo(SearchParam param);

}
