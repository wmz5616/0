package com.zemcho.guzhe.mapper.supermarket;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.common.vo.SupermarketCommonVo;
import com.zemcho.guzhe.controller.shop.vo.ShopCircleListVO;
import com.zemcho.guzhe.controller.supermarket.param.SupermarketParam;
import com.zemcho.guzhe.controller.supermarket.vo.SupermarketInfoVo;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Ryan
 */
@Mapper
public interface SupermarketMapper {

    List<SupermarketInfoVo> select(@Param("param") SearchParam param);

    boolean ifExistsByName(@Param("name") String name, @Param("id") Integer id);

    int insert(@Param("data") SupermarketParam data);

    int delete(@Param("deleteIds") List<Integer> deleteIds);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    int update(@Param("data") SupermarketParam data);

    /**
     * 根据店铺ID列表查询商超
     *
     * @param shopIds 店铺ID列表
     * @return 商圈列表
     */
    List<ShopCircleListVO> selectByShopIds(@Param("shopIds") List<Integer> shopIds);

    List<SupermarketInfoVo> selectSuperList();

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    SupermarketInfo selectById(@Param("id") Integer id);

    /**
     * 查询商超公共下拉列表
     *
     * @param param
     * @return
     */
    List<SupermarketCommonVo> selectCommonList(@Param("param") SearchParam param);
}
