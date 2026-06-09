package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderRefundApplyListVo;
import com.zemcho.ddql.entity.order.ExchangeOrderRefundApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExchangeOrderRefundApplyMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ExchangeOrderRefundApply data);

    /**
     * 修改数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ExchangeOrderRefundApply data);

    /**
     * 根据订单ID查询最近一次申请数据
     *
     * @param orderId
     * @return
     */
    ExchangeOrderRefundApply selectLatestByOrderId(@Param("orderId") Integer orderId);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ExchangeOrderRefundApplyListVo> selectLists(@Param("param") SearchParam param);

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    ExchangeOrderRefundApply selectById(@Param("id") Integer id);
}
