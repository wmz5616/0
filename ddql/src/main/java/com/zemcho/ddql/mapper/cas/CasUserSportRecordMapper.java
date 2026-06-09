package com.zemcho.ddql.mapper.cas;

import com.zemcho.ddql.entity.cas.CasUserSportRecord;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface CasUserSportRecordMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasUserSportRecord data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasUserSportRecord data);

    /**
     * 根据用户id和日期查询
     *
     * @param userId
     * @param date
     * @return
     */
    CasUserSportRecord selectByUserIdAndDate(@Param("userId") Integer userId,
                                             @Param("date") LocalDate date);
}
