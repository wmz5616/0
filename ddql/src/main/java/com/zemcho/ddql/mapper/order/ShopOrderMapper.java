package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.order.param.WechatShopOrderParam;
import com.zemcho.ddql.controller.wechat.order.vo.WechatShopOrderVo;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderDetailVo;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderInfoVo;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderListVo;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderStatVo;
import com.zemcho.ddql.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.ddql.entity.personalCenter.ShopOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopOrderMapper {
    List<WechatShopOrderListVo> selectWechatOrderList(@Param("param") WechatShopOrderListParam param,
                                                      @Param("userId") Integer userId);

    WechatShopOrderStatVo selectWechatOrderStat(@Param("param") WechatShopOrderListParam param,
                                                @Param("userId") Integer userId);

    WechatShopOrderInfoVo selectWechatOrderInfo(@Param("id") Integer id, @Param("userId") Integer userId);

    List<WechatShopOrderDetailVo> selectDetailListByOrderId(@Param("orderId") Integer orderId);

    BusinessDataVO selectBusinessData(@Param("param") SearchParam param);

    List<ShopOrder> select(@Param("param") SearchParam param);

    List<ShopOrder> selectLists(@Param("param") SearchParam param);

    Integer update(@Param("data") ShopOrder data);

    ShopOrder selectById(@Param("searchId") Integer searchId);

    Integer insert(@Param("param") ShopOrder shopOrder);

    List<ShopOrder> selectOrderListForReconciliation(@Param("startTime") java.time.LocalDateTime startTime,
                                                     @Param("endTime") java.time.LocalDateTime endTime);

    ShopOrder selectByOrderNo(@Param("orderNo") String orderNo);

    List<WechatShopOrderVo> selectWechatShopOrderList(@Param("param") WechatShopOrderParam param);

    WechatShopOrderStatVo selectShopOrderStat(@Param("param") WechatShopOrderParam param);
}
