package com.zemcho.ddql.mapper.business;


import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.audit.vo.SettlementApplicationVO;
import com.zemcho.ddql.entity.audit.SettlementApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementApplicationMapper {

    int insert(@Param("data") SettlementApplication data);

    List<SettlementApplicationVO> selectList(@Param("param") SearchParam param);

    SettlementApplication selectById(@Param("id") Integer id);

    int update(@Param("data") SettlementApplication data);

    SettlementApplication selectByShopId(@Param("id") Integer id);
}
