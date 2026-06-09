package com.zemcho.ddql.mapper.team;

import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.vo.TeamUserVo;
import com.zemcho.ddql.entity.team.TeamUser;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TeamUserMapper {
    Boolean ifExist(@Param("teamId") Integer teamId, @Param("userId") Integer userId,
                    @Param("isCheckDeleted") Boolean isCheckDeleted);

    Integer getCount(@Param("teamId") Integer teamId, @Param("status") Integer status, @Param("type") Integer type);

    int insert(@Param("data") TeamUser data);

    // 根据teamId 和 userId 更新
    int update(@Param("data") TeamUser data);

    // 根据teamId 检查是否有健康币不为0的团队用户
    @Select("select count(*) from team_user where team_id = #{teamId} and healthy_coin > 0")
    Integer ifExistHealthyCoin(@Param("teamId") Integer teamId);

    // 检查 不然会误删
    int delete(@Param("teamId") Integer teamId, @Param("userId") Integer userId);

    // 批量删除
    int deleteByIds(@Param("deleteIds") Collection<Integer> deleteIds);

    /**
     * 恢复删除数据
     *
     * @param id
     * @return
     */
    Integer restoreById(@Param("id") Integer id);

    // 必传teamId deptID为空 - 查询全部成员 dept = 0 - 查询直属成员 dept = !0 查询某个部门成员
    List<TeamUserVo> selectList(@Param("param") TeamUserSearchParam param);

    //
    TeamUser selectByTeamIdAndUserId(@Param("teamId") Integer teamId, @Param("userId") Integer userId,
                                     @Param("isCheckDeleted") Boolean isCheckDeleted);

    // 根据teamId删除
    int deleteByTeamId(@Param("teamId") Integer teamId);

    // 查出某个team 的 创建人和管理员的id
    @Select("select user_id from team_user where team_id = #{teamId} and type in (0,1) and delete_time is null")
    List<Integer> selectTeamCreateUserAndManagerIds(@Param("teamId") Integer teamId);

    List<TeamUserVo> selectListByUserId(@Param("userId") Integer userId,
                                        @Param("isCheckDeleted") Boolean isCheckDeleted);

    /**
     * 根据userId和status查询
     *
     * @param userId
     * @param status
     * @return
     */
    List<TeamUser> selectByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") Integer status);

    /**
     * 用户团体健康币自增
     *
     * @param userId
     * @param teamId
     * @param healthCoin
     * @return
     */
    Integer incCoin(@Param("userId") Integer userId, @Param("teamId") Integer teamId,
                    @Param("healthCoin") Integer healthCoin);

    /**
     * 用户团体健康币自减
     *
     * @param userId
     * @param teamId
     * @param healthCoin
     * @return
     */
    Integer decCoin(@Param("userId") Integer userId, @Param("teamId") Integer teamId,
                    @Param("healthCoin") Integer healthCoin);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    TeamUser selectById(@Param("id") Integer id);

    /**
     * 根据ids查询
     *
     * @param ids
     * @return
     */
    List<TeamUser> selectByIds(@Param("ids") Collection<Integer> ids);

    /**
     * 根据团队ID清除所有成员的部门ID
     *
     * @param teamId
     * @return
     */
    int clearDepartmentIdByTeamId(@Param("teamId") Integer teamId);

    int ifExistInDepartment(@Param("departmentId") Integer departmentId);

    /**
     * 批量更新成员部门
     *
     * @param teamId       团队ID
     * @param ids          团队成员记录ID列表
     * @param departmentId 目标部门ID
     * @return 影响行数
     */
    int updateDepartmentByIds(@Param("teamId") Integer teamId, @Param("ids") Collection<Integer> ids,
                              @Param("departmentId") Integer departmentId);

    TeamUser selectByPhone(@Param("phone") String phone);
}
