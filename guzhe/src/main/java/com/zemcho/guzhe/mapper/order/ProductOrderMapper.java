package com.zemcho.guzhe.mapper.order;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderCountVo;
import com.zemcho.guzhe.entity.order.ProductOrder;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductOrderMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") ProductOrder data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") ProductOrder data);

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    ProductOrder selectById(@Param("id") Integer id);

    /**
     * 根据订单编号和状态查询订单
     *
     * @param orderNo
     * @param status
     * @return
     */
    ProductOrder selectByOrderNo(@Param("orderNo") String orderNo, @Param("status") Integer status);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ProductOrder> selectLists(@Param("param") SearchParam param);

    /**
     * 统计订单数据
     *
     * @param param
     * @return
     */
    ProductOrderCountVo selectCount(@Param("param") SearchParam param);

}
