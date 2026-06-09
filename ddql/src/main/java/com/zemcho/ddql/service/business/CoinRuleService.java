package com.zemcho.ddql.service.business;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.CoinRuleParam;

/**
 * 用币规则服务接口
 */
public interface CoinRuleService {

    /**
     * 新增用币规则
     * @param param 参数对象
     * @return 结果
     */
    Result addCoinRule(CoinRuleParam param,String token,Boolean isWechat);

    /**
     * 更新用币规则
     * @param param 参数对象
     * @return 结果
     */
    Result updateCoinRule(CoinRuleParam param,String token,Boolean isWechat);

    /**
     * 根据商家ID查询用币规则
     * @param param 查询参数(包含shopId)
     * @return 结果
     */
    Result getByShopId(SearchParam param,String token,Boolean isWechat);

}