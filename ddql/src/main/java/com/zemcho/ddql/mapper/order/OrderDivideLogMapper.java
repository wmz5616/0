package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.entity.order.OrderDivideLog;
import org.apache.ibatis.annotations.Param;

/**
 * @author HXH
 */
public interface OrderDivideLogMapper {
    Integer insert(@Param("data") OrderDivideLog data);
}
