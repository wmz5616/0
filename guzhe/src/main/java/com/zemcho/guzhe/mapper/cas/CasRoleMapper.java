package com.zemcho.guzhe.mapper.cas;

import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.common.vo.RoleCommonVO;
import com.zemcho.guzhe.entity.cas.CasRole;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface CasRoleMapper {
    /**
     * 新增数据
     *
     * @param data
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param("data") CasRole data);

    /**
     * 更新数据
     *
     * @param data
     * @return
     */
    Integer update(@Param("data") CasRole data);

    /**
     * 检查名称是否存在
     *
     * @param id
     * @param name
     * @return
     */
    Boolean existName(@Param("id") Integer id, @Param("name") String name);

    /**
     * 查询列表
     *
     * @param param
     * @return
     */
    List<CasRole> selectLists(@Param("param") SearchParam param);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    CasRole selectById(@Param("id") Integer id);

    /**
     * 根据id删除
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
    List<RoleCommonVO> selectCommonLists(@Param("param") SearchParam param);
}
