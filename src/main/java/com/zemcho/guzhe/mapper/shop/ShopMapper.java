package com.zemcho.guzhe.mapper.shop;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.app.vo.AppShopListVo;
import com.zemcho.guzhe.controller.shop.vo.ShopVO;
import com.zemcho.guzhe.controller.common.vo.ShopCommonVo;
import com.zemcho.guzhe.controller.merchant.vo.ShopMerchantVO;
import com.zemcho.guzhe.controller.wechat.shop.vo.MerchantManageVO;
import com.zemcho.guzhe.entity.shop.Shop;
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
     * 根据商户ID查询完整商家信息（包含认证状态等）
     *
     * @param merchantId 商户ID
     * @return 商家列表
     */
    List<MerchantManageVO> selectByMerchantIdFull(@Param("merchantId") Integer merchantId);

    /**
     * 根据商家ID查询
     *
     * @param shopIds
     * @return
     */
    List<AppShopListVo> selectAppShopListsByShopIds(@Param("shopIds") Collection<Integer> shopIds);
}
