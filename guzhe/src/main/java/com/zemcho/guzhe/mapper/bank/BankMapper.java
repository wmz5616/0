package com.zemcho.guzhe.mapper.bank;

import com.zemcho.guzhe.controller.wechat.common.param.WechatBankQueryParam;
import com.zemcho.guzhe.controller.wechat.common.vo.WechatBankCommonVo;
import com.zemcho.guzhe.controller.wechat.common.vo.WechatBankLinkVo;
import com.zemcho.guzhe.controller.wechat.common.vo.WechatBankProvinceCityVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankMapper {
    /**
     * 查询去重后的银行列表
     */
    List<WechatBankCommonVo> selectBankLists(@Param("param") WechatBankQueryParam param);

    /**
     * 查询省市列表
     */
    List<WechatBankProvinceCityVo> selectProvinceCityLists(@Param("param") WechatBankQueryParam param);

    /**
     * 查询联行号列表
     */
    List<WechatBankLinkVo> selectBankLinkLists(@Param("param") WechatBankQueryParam param);
}
