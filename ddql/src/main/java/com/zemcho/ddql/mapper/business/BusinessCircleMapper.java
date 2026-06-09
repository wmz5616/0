package com.zemcho.ddql.mapper.business;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.vo.BusinessCircleVo;
import com.zemcho.ddql.controller.business.vo.ShopCircleListVO;
import com.zemcho.ddql.controller.common.vo.BusinessCircleCommonVo;
import com.zemcho.ddql.entity.business.BusinessCircle;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface BusinessCircleMapper {

    /**
     * 插入商圈信息
     *
     * @param data 商圈实体对象
     * @return 插入记录数
     */
    int insert(@Param("data") BusinessCircle data);

    /**
     * 更新商圈信息
     *
     * @param data 商圈实体对象
     * @return 更新记录数
     */
    int update(@Param("data") BusinessCircle data);

    /**
     * 查询商圈列表
     *
     * @param param 查询参数
     * @return 商圈列表
     */
    List<BusinessCircleVo> selectList(@Param("param") SearchParam param);

    /**
     * 根据ID查询商圈信息
     *
     * @param id 商圈ID
     * @return 商圈实体对象
     */
    BusinessCircle selectById(@Param("id") Integer id);

    /**
     * 批量删除商圈
     *
     * @param ids 商圈ID列表
     * @return 删除记录数
     */
    int deleteByIds(@Param("ids") Collection<Integer> ids);

    /**
     * 根据名称查询商圈
     */
    BusinessCircle selectByName(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据店铺ID列表查询商圈
     *
     * @param shopIds 店铺ID列表
     * @return 商圈列表
     */
    List<ShopCircleListVO> selectByShopIds(@Param("shopIds") List<Integer> shopIds);

    /**
     * 查询商圈公共下拉列表
     *
     * @param param
     * @return
     */
    List<BusinessCircleCommonVo> selectCommonList(@Param("param") SearchParam param);
}
