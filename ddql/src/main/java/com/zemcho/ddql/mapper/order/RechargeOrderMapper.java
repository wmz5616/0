package com.zemcho.ddql.mapper.order;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderActCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderCountVo;
import com.zemcho.ddql.entity.order.RechargeOrder;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeOrderMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") RechargeOrder data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") RechargeOrder data);

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    RechargeOrder selectById(@Param("id") Integer id);

    /**
     * 根据订单编号查询数据
     *
     * @param orderNo
     * @return
     */
    RechargeOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<RechargeOrder> selectLists(@Param("param") SearchParam param);

    /**
     * 统计数据
     *
     * @param param
     * @return
     */
    RechargeOrderCountVo selectCount(@Param("param") SearchParam param);

    /**
     * 统计活动下的订单数量
     *
     * @param param
     * @return
     */
    List<RechargeOrderActCountVo> selectActCount(@Param("param") SearchParam param);
}
