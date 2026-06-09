package com.zemcho.ddql.mapper.merchant;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.merchant.vo.MerchantEnabledVO;
import com.zemcho.ddql.controller.merchant.vo.MerchantListVO;
import com.zemcho.ddql.entity.merchant.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MerchantMapper {

    Boolean ifExistByIdOrMerchantNo(@Param("id") Integer id, @Param("merchantNo") String merchantNo);

    int insert(@Param("data") Merchant merchant);

    int updateById(@Param("data") Merchant merchant);

    int updateByMerchantNo(@Param("data") Merchant merchant);

    // keyword(merchant_name contact_phone legal_person)  searchType(main_type) searchField1(acc_type) searchIntStatus(status)
    List<MerchantListVO> selectList(@Param("param") SearchParam param);

    /**
     * 查询启用状态的商户列表
     *
     * @return 启用商户列表
     */
    List<MerchantEnabledVO> selectEnabledList();

    @Select("select * from merchant where id = #{id} AND delete_time IS NULL")
    Merchant selectById(@Param("id") Integer id);

    /**
     * 更新状态
     *
     * @param ids
     * @param status
     * @return
     */
    Integer updateStatusByIds(@Param("ids") Collection<Integer> ids, @Param("status") Integer status);
}
