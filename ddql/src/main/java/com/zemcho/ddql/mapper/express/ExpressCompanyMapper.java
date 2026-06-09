package com.zemcho.ddql.mapper.express;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.express.ExpressCompany;
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
