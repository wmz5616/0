package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.vo.RuleTreeVO;
import com.zemcho.guzhe.entity.cas.CasRule;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasRuleMapper {
    /**
     * 批量插入
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<CasRule> data);

    /**
     * 清空表数据并将自增长的主键值重置为初始值--注意该方法不能回滚
     */
    void truncateTableData();

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<RuleTreeVO> selectLists(@Param("param") SearchParam param);

    /**
     * 根据id查询
     *
     * @param ids
     * @return
     */
    List<CasRule> selectByIds(@Param("ids") Collection<Integer> ids);
}
