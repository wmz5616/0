package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.vo.AdminListVO;
import com.zemcho.guzhe.controller.common.vo.AdminSelectVO;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasAdminMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasAdmin data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasAdmin data);

    /**
     * 根据账号查询
     *
     * @param account
     * @return
     */
    CasAdmin selectByAccount(@Param("account") String account);

    /**
     * 检查账号是否存在
     *
     * @param id
     * @param account
     * @return
     */
    Boolean existAccount(@Param("id") Integer id, @Param("account") String account);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CasAdmin selectById(@Param("id") Integer id);

    /**
     * 查询所有数据
     *
     * @return
     */
    List<CasAdmin> selectAll();

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<AdminListVO> selectLists(@Param("param") SearchParam param);

    /**
     * 批量更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    Integer deleteByIds(@Param("ids") Collection<Integer> ids);

    /**
     * 查询公共下拉列表
     *
     * @param param
     * @return
     */
    List<AdminSelectVO> selectCommonLists(@Param("param") SearchParam param);
}
