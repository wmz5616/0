package com.zemcho.ddql.mapper.business;


import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatMerchantManageListVo;
import com.zemcho.ddql.entity.business.ShopManager;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ShopManagerMapper {
    /**
     * 根据ID查询
     */
    ShopManager selectById(@Param("id") Integer id);


    /**
     * 根据商家ID查询
     */
    List<ShopManager> selectByShopId(@Param("shopId") Integer shopId);

    /**
     * 新增
     */
    int insert(@Param("param") ShopManager shopManager);

    /**
     * 更新
     */
    int update(@Param("param") ShopManager shopManager);

    /**
     * 根据ID删除
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    List<ShopManager> selectByIds(@Param("ids") Set<Integer> ids);

    /**
     * 根据手机号查询其管理的商家列表
     *
     * @param phone 手机号
     * @return 商家列表
     */
    List<WechatMerchantManageListVo> selectWechatManagedShopList(@Param("phone") String phone);

    /**
     * 校验当前手机号是否是指定商家的店长
     *
     * @param shopId 商家ID
     * @param phone  手机号
     * @return 店长数量
     */
    Integer countHeadManagerByShopIdAndPhone(@Param("shopId") Integer shopId, @Param("phone") String phone);

    /**
     * 根据手机号和商家id查询商家管理人员
     *
     * @return 商家列表
     */
    List<ShopManager> selectByPhoneAndShopId(@Param("param") SearchParam param);

    /**
     * 根据手机号查询商家id
     *
     * @param phone 手机号
     * @return 商家id列表
     */
    List<Integer> selectShopIdsByPhone(@Param("phone") String phone);

    ShopManager selectByPhone(@Param("phone") String phone);

    Integer deleteByShopId(@Param("id") Integer id);

    ShopManager selectIfExitByShopIdAndPhone(@Param("id") Integer id, @Param("phone") String phone);

    ShopManager selectByShopIdAndPhone(@Param("shopId") Integer shopId, @Param("phone") String phone);

    /**
     * 批量插入商家管理人员
     *
     * @param managers 管理人员列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ShopManager> managers);
}
