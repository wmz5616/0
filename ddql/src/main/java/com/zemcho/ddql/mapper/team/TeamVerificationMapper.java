package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TeamVerificationMapper {

    @Select("select count(*) from team_verification where team_id = #{teamId}")
    Boolean ifExistByTeamId(@Param("teamId") Integer teamId);

    int insert(@Param("data") TeamVerification data);

    // 根据teamId更新
    int update(@Param("data") TeamVerification data);

    @Select("select * from team_verification where id = #{id}")
    TeamVerification selectById(@Param("id") Integer id);

    // teamId（searchId) status(searchIntStatus)
    List<TeamVerification> selectList(@Param("param") SearchParam param);

    /**
     * 根据teamId查询
     *
     * @param teamId
     * @return
     */
    TeamVerification selectByTeamId(@Param("teamId") Integer teamId);
}
