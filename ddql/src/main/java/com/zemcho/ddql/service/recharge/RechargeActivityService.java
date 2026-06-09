package com.zemcho.ddql.service.recharge;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeActivityParam;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import java.util.List;

/**
 * 充值活动服务接口
 *
 * @author Ryan
 */
public interface RechargeActivityService {

    /**
     * 新增充值活动
     */
    Result addRechargeActivity(List<RechargeActivityParam> param);


    /**
     * 获取充值活动列表
     * @param param
     * @return
     */
    Result selectList(SearchParam param);

    /**
     * 删除充值活动
     * @param param
     * @return
     */
    Result deleteRechargeActivity(DeleteOneParam param);

    /**
     * 排序充值活动
     * @param param
     * @return
     */
    Result sortRechargeActivity(SearchParam param);
}
