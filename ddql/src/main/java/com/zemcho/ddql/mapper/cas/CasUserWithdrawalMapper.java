package com.zemcho.ddql.mapper.cas;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.wechat.index.vo.UserWithdrawalCountVo;
import com.zemcho.ddql.entity.cas.CasUserWithdrawal;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CasUserWithdrawalMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasUserWithdrawal data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasUserWithdrawal data);

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    CasUserWithdrawal selectById(@Param("id") Integer id);

    /**
     * 查询数据列表
     *
     * @param param
     * @return
     */
    List<CasUserWithdrawal> selectList(@Param("param") SearchParam param);

    /**
     * 统计用户提现数据
     *
     * @param param
     * @return
     */
    List<UserWithdrawalCountVo> selectUserCount(@Param("param") SearchParam param);
}
