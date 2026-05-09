package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowVo;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TransactionFlowMapper {
    List<TransactionFlowVo> selectTransactionFlowList(@Param("data") TransactionFlowSearchParam param);

    TransactionFlowSummaryVo selectTransactionFlowSummary(@Param("data") TransactionFlowSearchParam data);

    long selectTransactionFlowList_COUNT(@Param("data") TransactionFlowSearchParam param);

}