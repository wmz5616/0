package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.team.vo.TeamApplicationRecordVo;
import com.zemcho.ddql.entity.team.TeamApplicationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamApplicationRecordMapper {

    @Select("select count(*) from team_application_record where id = #{id}")
    Boolean ifExistById(@Param("id") Integer id);

    int insert(@Param("data") TeamApplicationRecord data);

    // 根据id更新
    int update(@Param("data") TeamApplicationRecord data);

    // 根据id查询
    @Select("select * from team_application_record where id = #{id}")
    TeamApplicationRecord selectById(@Param("id") Integer id);

    // 获取列表 teamId(searchId) 和 status(searchIntStatus) 查
    List<TeamApplicationRecordVo> selectList(@Param("param") SearchParam param);

    /**
     * 判断用户是否已有申请中、通过的记录
     *
     * @param teamId
     * @param userId
     * @return
     */
    Boolean checkUserCanApply(@Param("teamId") Integer teamId, @Param("userId") Integer userId);
}
