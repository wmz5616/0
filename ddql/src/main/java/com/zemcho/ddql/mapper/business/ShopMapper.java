package com.zemcho.ddql.mapper.business;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.vo.ShopVO;
import com.zemcho.ddql.controller.common.vo.ShopCommonVo;
import com.zemcho.ddql.controller.merchant.vo.ShopMerchantVO;
import com.zemcho.ddql.controller.wechat.shop.vo.ConsumptionShopVO;
import com.zemcho.ddql.entity.business.Shop;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ShopMapper {

    /**
     * 插入店铺信息
     *
     * @param data 店铺实体对象
     * @return 插入记录数
     */
    int insert(@Param("data") Shop data);

    /**
     * 更新店铺信息
     *
     * @param data 店铺实体对象
     * @return 更新记录数
     */
    int update(@Param("data") Shop data);

    /**
     * 条件查询店铺列表
     *
     * @param param 查询参数
     * @return 店铺列表
     */
    List<ShopVO> selectList(@Param("param") SearchParam param);

    /**
     * 根据ID查询店铺信息
     *
     * @param id 店铺ID
     * @return 店铺实体对象
     */
    Shop selectById(@Param("id") Integer id);

    /**
     * 批量删除店铺
     *
     * @param ids 店铺ID列表
     * @return 删除记录数
     */
    int deleteByIds(@Param("ids") Collection<Integer> ids);

    /**
     * 根据名称查询店铺信息
     *
     * @param name 店铺名称
     * @return 店铺实体对象
     */
    Shop selectByName(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据商圈ID查询店铺信息
     *
     * @param circleIds 商圈ID列表
     * @return 店铺信息
     */
    List<Map<String, Object>> selectByCircleIds(@Param("circleIds") List<Integer> circleIds);

    /**
     * 查询店铺下拉列表
     *
     * @param param
     * @return
     */
    List<ShopCommonVo> selectCommonList(@Param("param") SearchParam param);

    /**
     * 店铺点击数自增
     *
     * @param id
     * @param clickCount
     * @return
     */
    Integer incClickCount(@Param("id") Integer id, @Param("clickCount") Integer clickCount);

    /**
     * 店铺置顶状态检查修改
     *
     * @param checkTime
     * @return
     */
    int checkUpdateTopStatus(@Param("checkTime") LocalDateTime checkTime);

    /**
     * 根据商户ID查询
     *
     * @param merchantIds ids
     * @return list
     */
    List<ShopMerchantVO> selectByMerchantId(@Param("merchantIds") List<Integer> merchantIds);

    /**
     * 更新删除时间
     *
     * @param shop shop
     * @return int
     */
    int updateDeleteTime(@Param("shop") Shop shop);

    /**
     * 根据ID查询未审核的店铺信息
     *
     * @param id 商家ID
     * @return result
     */
    Shop selectUnAuditById(@Param("id") Integer id);

    /**
     * 查询置顶商家（用于轮播）
     * @param checkTime 检查时间
     * @return 置顶商家列表
     */
    List<ConsumptionShopVO> selectTopConsumptionShops(@Param("checkTime") LocalDateTime checkTime);

    /**
     * 根据条件查询消费店铺列表
     * @param param searchId 经营类别 ID keyword 商家名称
     * @return list
     */
    List<ConsumptionShopVO> selectConsumptionShopList(@Param("param") SearchParam param);

//    Shop selectByAllId(@Param("shopId")Integer shopId);

    /**
     * 更新商家状态
     *
     * @param id         商家ID
     * @param shopStatus 商家状态
     * @return 影响行数
     */
    int updateShopStatus(@Param("id") Integer id, @Param("shopStatus") Integer shopStatus);

    /**
     * 根据ID获取启用的商家
     *
     * @param shopId 商家ID
     * @return 商家信息
     */
    Shop selectOnlineById(@Param("shopId") Integer shopId);

    Shop selectAuditById(@Param("shopId") Integer shopId);
}
