package com.zemcho.ddql.mapper.recharge;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 充值活动 Mapper 接口
 *
 * @author Ryan
 */
@Mapper
public interface RechargeActivityMapper {

    Integer insertBatch(@Param("data") List<RechargeActivity> toInsert);

    Integer updateBatch(@Param("data") List<RechargeActivity> toUpdate);

    List<RechargeActivity> selectList(@Param("param") SearchParam param);

    @Select("SELECT * FROM recharge_activity WHERE id = #{id}")
    RechargeActivity selectById(@Param("id") Integer id);

    Integer deleteById(@Param("id") Integer id);

    Integer updateSortAfterDelete(@Param("deletedSort") Integer deletedSort);
}
