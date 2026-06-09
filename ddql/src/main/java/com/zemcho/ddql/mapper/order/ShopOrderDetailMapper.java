package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.entity.personalCenter.ShopOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HXH
 */
@Mapper
public interface ShopOrderDetailMapper {
    List<ShopOrderDetail> selectDetailListByOrderId(@Param("orderId") Integer orderId);

    Integer batchInsert(@Param("details") List<ShopOrderDetail> details);
}
