package com.zemcho.ddql.mapper.personalCenter;

import com.zemcho.ddql.controller.wechat.personalCenter.vo.DeliveryAddressVo;
import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    @Select("select count(*) from delivery_address where id = #{id}")
    Boolean ifExistsById(@Param("id") Integer id);

    int insert(@Param("data") DeliveryAddress data);

    int update(@Param("data") DeliveryAddress data);

    // 将该用户id的默认地址设置为非默认
    int updateIsDefault(@Param("userId") Integer userId);

    List<DeliveryAddressVo> selectByUserId(@Param("userId") Integer userId);

    @Delete("delete from delivery_address where id = #{id}")
    int delete(@Param("id") Integer id);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    DeliveryAddress selectById(@Param("id") Integer id);
}
