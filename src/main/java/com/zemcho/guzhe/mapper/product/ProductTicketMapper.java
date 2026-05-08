package com.zemcho.guzhe.mapper.product;

import com.zemcho.guzhe.entity.product.ProductTicket;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author HXH
 */
public interface ProductTicketMapper {
    /**
     * 根据商品ID查询商品券列表
     *
     * @param productId
     * @param status
     * @return
     */
    List<ProductTicket> selectByProductId(@Param("productId") Integer productId, @Param("status") Integer status);

    /**
     * 批量插入商品券
     *
     * @param cachedDataList
     * @return
     */
    Integer saveBatch(@Param("ticketList") List<ProductTicket> cachedDataList);

    /**
     * 根据商品ID和券码查询商品券列表
     *
     * @param productId
     * @param ticketList
     * @param status
     * @return
     */
    List<ProductTicket> selectByProductIdAndTicket(@Param("productId") Integer productId,
                                                   @Param("ticketList") Collection<String> ticketList,
                                                   @Param("status") Integer status);

    /**
     * 批量更新商品券码状态
     *
     * @param productId
     * @param ticketList
     * @param status
     * @param orderId
     * @return
     */
    Integer updateByProductIdAndTicket(@Param("productId") Integer productId,
                                       @Param("ticketList") Collection<String> ticketList,
                                       @Param("status") Integer status,
                                       @Param("orderId") Integer orderId);

    /**
     * 根据订单ID查询商品券码列表
     *
     * @param orderId
     * @return
     */
    List<ProductTicket> selectByOrderId(@Param("orderId") Integer orderId, @Param("status") Integer status);

    /**
     * 根据券码查询商品券码
     *
     * @param ticket
     * @return
     */
    ProductTicket selectByTicket(@Param("ticket") String ticket);

    /**
     * 更新商品券码状态
     *
     * @param id
     * @param status
     * @return
     */
    Integer updateStatusById(@Param("id") Integer id, @Param("status") Integer status);
}
