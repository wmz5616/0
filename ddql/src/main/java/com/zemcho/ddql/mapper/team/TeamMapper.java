package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.controller.team.vo.TeamListVo;
import com.zemcho.ddql.controller.wechat.common.vo.TeamCommonVo;
import com.zemcho.ddql.entity.team.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface TeamMapper {

    @Select("select count(*) from team where id = #{id}")
    Boolean ifExist(Integer id);

    @Select("select * from team where name = #{name} and delete_time is null limit 1")
    Team selectByNameLimit1(@Param("name") String name);

    int insert(@Param("data") Team team);

    int update(@Param("data") Team team);

    int delete(@Param("deleteId") Integer deleteId, @Param("deleteTime") LocalDateTime deleteTime);

    @Select("select * from team where id = #{id} and delete_time is null")
    Team selectById(Integer id);

    List<TeamListVo> selectList(@Param("param") TeamSearchParam param);

    List<Team> selectListToExport(@Param("teamIds") List<Integer> teamIds);

    List<Team> selectByIds(@Param("teamIds") List<Integer> teamIds, @Param("status") Integer status);

    /**
     * 团体健康币自增
     *
     * @param teamId
     * @param healthCoin
     * @return
     */
    Integer incCoin(@Param("teamId") Integer teamId, @Param("healthCoin") Integer healthCoin);

    /**
     * 团体健康币自减
     *
     * @param teamId
     * @param healthCoin
     * @return
     */
    Integer decCoin(@Param("teamId") Integer teamId, @Param("healthCoin") Integer healthCoin);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);

    /**
     * 获取团队公共下拉列表
     *
     * @param param
     * @return
     */
    List<TeamCommonVo> selectCommonList(@Param("param") TeamSearchParam param);

    /**
     * 团体人数自增
     *
     * @param teamId
     * @param peopleNumber
     * @return
     */
    Integer incPeopleNumber(@Param("teamId") Integer teamId, @Param("peopleNumber") Integer peopleNumber);

    /**
     * 团体人数自减
     *
     * @param teamId
     * @param peopleNumber
     * @return
     */
    Integer decPeopleNumber(@Param("teamId") Integer teamId, @Param("peopleNumber") Integer peopleNumber);
}
