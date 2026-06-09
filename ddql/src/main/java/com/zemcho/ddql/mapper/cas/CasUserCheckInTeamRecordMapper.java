package com.zemcho.ddql.mapper.cas;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInRankCountVo;
import com.zemcho.ddql.entity.cas.CasUserCheckInTeamRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasUserCheckInTeamRecordMapper {
    /**
     * 批量新增数据
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<CasUserCheckInTeamRecord> data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasUserCheckInTeamRecord data);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<CasUserCheckInTeamRecord> selectLists(@Param("param") SearchParam param);

    /**
     * 统计用户打卡数据
     *
     * @param param
     * @return
     */
    List<UserCheckInRankCountVo> selectUserCount(@Param("param") SearchParam param);
}
