package com.zemcho.guzhe.mapper.express;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.express.ExpressCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExpressCompanyMapper {
    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<ExpressCompany> selectLists(@Param("param") SearchParam param);
}
