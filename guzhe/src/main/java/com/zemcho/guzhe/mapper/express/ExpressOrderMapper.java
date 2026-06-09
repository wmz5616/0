package com.zemcho.guzhe.mapper.express;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.order.vo.ExpressOrderVo;
import com.zemcho.guzhe.entity.express.ExpressOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ExpressOrderMapper {
    /**
     * 批量新增数据
     *
     * @param data
     * @return
     */
    int insertAll(@Param("data") Collection<ExpressOrder> data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    int update(@Param("data") ExpressOrder data);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ExpressOrder> selectLists(@Param("param") SearchParam param);

    /**
     * 查询订单对应的物流信息
     *
     * @param txnType
     * @param txnId
     * @return
     */
    ExpressOrderVo selectByTxn(@Param("txnType") Integer txnType, @Param("txnId") Integer txnId);
}
