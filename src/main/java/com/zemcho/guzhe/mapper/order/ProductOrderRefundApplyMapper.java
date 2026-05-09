package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.order.vo.ProductOrderRefundApplyListVo;
import com.zemcho.guzhe.entity.order.ProductOrderRefundApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductOrderRefundApplyMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    Integer insert(@Param("data") ProductOrderRefundApply data);

    /**
     * 修改数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ProductOrderRefundApply data);

    /**
     * 根据订单ID查询最近一次申请数据
     *
     * @param orderId
     * @return
     */
    ProductOrderRefundApply selectLatestByOrderId(@Param("orderId") Integer orderId);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ProductOrderRefundApplyListVo> selectLists(@Param("param") SearchParam param);

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    ProductOrderRefundApply selectById(@Param("id") Integer id);

    /**
     * 统计商家待审核的退款申请数量
     *
     * @param merchantId 商家ID
     * @return 待审核数量
     */
    Integer countPendingByMerchantId(@Param("merchantId") Integer merchantId);
}
