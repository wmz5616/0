package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.entity.sys.DailyVisitTrend;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface DailyVisitTrendMapper {
    /**
     * 批量插入数据
     *
     * @param data
     * @return
     */
    Integer insertAll(@Param("data") Collection<DailyVisitTrend> data);

    /**
     * 根据日期删除数据
     *
     * @param date
     * @return
     */
    Integer deleteByDate(@Param("date") LocalDate date);

    /**
     * 统计指定日期的浏览数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Integer countVisitPvByDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
