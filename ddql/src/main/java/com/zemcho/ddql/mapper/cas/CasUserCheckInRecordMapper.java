package com.zemcho.ddql.mapper.cas;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.statistic.vo.PlaceCheckInCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.UserCheckInVo;
import com.zemcho.ddql.entity.cas.CasUserCheckInRecord;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface CasUserCheckInRecordMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasUserCheckInRecord data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasUserCheckInRecord data);

    /**
     * 根据用户id和状态查询
     *
     * @param userId
     * @param status
     * @return
     */
    CasUserCheckInRecord selectByUserIdAndStatus(@Param("userId") Integer userId,
                                                 @Param("status") Integer status);

    /**
     * 打卡失效处理
     *
     * @param date
     * @return
     */
    Integer invalidCheckIn(@Param("date") LocalDate date);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CasUserCheckInRecord selectById(@Param("id") Integer id);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<UserCheckInVo> selectList(@Param("param") SearchParam param);

    /**
     * 统计数据
     *
     * @param param
     * @return
     */
    UserCheckInCountVo selectCount(@Param("param") SearchParam param);

    /**
     * 统计场地打卡数据
     *
     * @param param
     * @return
     */
    List<PlaceCheckInCountVo> selectCountByPlace(@Param("param") SearchParam param);
}
