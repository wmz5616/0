package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TeamCheckInSettingsMapper {

    @Select("select count(*) from team_check_in_settings where team_id = #{teamId}")
    Boolean ifExistsByTeamId(@Param("teamId") Integer teamId);

    int insert(@Param("data") TeamCheckInSettings data);

    int updateByTeamId(@Param("data") TeamCheckInSettings data);

    @Select("select * from team_check_in_settings where team_id = #{teamId}")
    TeamCheckInSettings selectByTeamId(@Param("teamId") Integer teamId);

    /**
     * 根据团队ID查询
     *
     * @param teamIds
     * @return
     */
    List<TeamCheckInSettings> selectByTeamIds(@Param("teamIds") Collection<Integer> teamIds);
}
