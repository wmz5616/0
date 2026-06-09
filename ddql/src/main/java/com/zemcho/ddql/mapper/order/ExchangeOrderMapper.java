package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderCountVo;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExchangeOrderMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") ExchangeOrder data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ExchangeOrder data);

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    ExchangeOrder selectById(@Param("id") Integer id);

    /**
     * 根据订单编号查询数据
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    ExchangeOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ExchangeOrder> selectLists(@Param("param") SearchParam param);

    /**
     * 统计订单数据
     *
     * @param param
     * @return
     */
    ExchangeOrderCountVo selectCount(@Param("param") SearchParam param);

//    ExchangeOrder selectByOrderNo(@Param("orderNo") String orderNo);

    List<ExchangeOrder> selectOrderListForReconciliation(@Param("startTime") java.time.LocalDateTime startTime,
                                                         @Param("endTime") java.time.LocalDateTime endTime);
}
