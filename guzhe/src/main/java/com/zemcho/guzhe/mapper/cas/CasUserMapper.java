package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.vo.CasUserVo;
import com.zemcho.guzhe.controller.common.vo.UserCommonVO;
import com.zemcho.guzhe.entity.cas.CasUser;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface CasUserMapper {

    // 根据id判断是否存在
    @Select("select * from cas_user where id = #{id}")
    Boolean existById(@Param("id") Integer id);

    // 根据手机号判断是否存在
    @Select("select * from cas_user where phone = #{phone}")
    Boolean existByPhone(@Param("phone") String phone);

    @Select("select * from cas_user")
    List<CasUser> selectAll();

    /**
     * 新增记录
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasUser data);

    /**
     * 更新记录
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasUser data);

    /**
     * 根据openId查询记录
     *
     * @param openId
     * @return
     */
    CasUser selectByOpenId(@Param("openId") String openId);

    /**
     * 根据id查询记录
     *
     * @param id
     * @return
     */
    CasUser selectById(@Param("id") Integer id);

    /**
     * 根据手机号查询记录
     *
     * @param phone
     * @return
     */
    CasUser selectByPhone(@Param("phone") String phone);


    CasUserVo selectDetailById(@Param("id") Integer id);

    /**
     * 查询列表
     * <p>
     * nickName(keyword) phone(param.searchStrField1) lock(param.searchField1) has_certification(searchField2)
     * createTime(startTime - endTime)
     *
     * @param param
     * @return
     */
    List<CasUserVo> selectLists(@Param("param") SearchParam param);

    List<CasUserVo> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 用户金币/健康币自增
     *
     * @param id
     * @param goldCoin
     * @param healthCoin
     * @return
     */
    Integer incCoin(@Param("id") Integer id, @Param("goldCoin") Integer goldCoin,
                    @Param("healthCoin") Integer healthCoin);

    /**
     * 用户金币/健康币自减
     *
     * @param id
     * @param goldCoin
     * @param healthCoin
     * @return
     */
    Integer decCoin(@Param("id") Integer id, @Param("goldCoin") Integer goldCoin,
                    @Param("healthCoin") Integer healthCoin);

    /**
     * 查询用户下拉列表
     *
     * @param param
     * @return
     */
    List<UserCommonVO> selectCommonLists(@Param("param") SearchParam param);

    /**
     * 根据时间统计
     *
     * @param timeLimit
     * @return
     */
    Integer countByTime(@Param("timeLimit") LocalDateTime timeLimit);
}
