package com.zemcho.guzhe.mapper.audit;



import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.vo.SettlementApplicationVO;
import com.zemcho.guzhe.entity.audit.SettlementApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementApplicationMapper {

    int insert(@Param("data") SettlementApplication data);

    List<SettlementApplicationVO> selectList(@Param("param") SearchParam param);

    SettlementApplication selectById(@Param("id") Integer id);

    int update(@Param("data") SettlementApplication data);

    List<SettlementApplicationVO> selectAll(@Param("param") SearchParam param);

    SettlementApplication selectByShopAuditId(@Param("id") Integer id);
}
