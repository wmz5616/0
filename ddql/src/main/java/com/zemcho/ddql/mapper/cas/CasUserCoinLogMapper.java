package com.zemcho.ddql.mapper.cas;

import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.statistic.vo.CoinCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.UserCoinCountVo;
import com.zemcho.ddql.entity.cas.CasUserCoinLog;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasUserCoinLogMapper {
    /**
     * 批量新增数据
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<CasUserCoinLog> data);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<CasUserCoinLog> selectLists(@Param("param") UserCoinLogParam param);

    /**
     * 统计用户数据
     *
     * @param param
     * @return
     */
    List<UserCoinCountVo> selectUserCount(@Param("param") UserCoinLogParam param);

    /**
     * 统计数据
     *
     * @param param
     * @return
     */
    List<CoinCountVo> selectCount(@Param("param") UserCoinLogParam param);

    Integer insert(@Param("coinLog") CasUserCoinLog coinLog);
}
