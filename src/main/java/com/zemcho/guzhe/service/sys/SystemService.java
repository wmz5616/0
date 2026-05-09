package com.zemcho.guzhe.service.sys;


import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.controller.sys.param.ConfigParam;
import jakarta.validation.Valid;

/**
 * @author Ryan
 * @title: SystemService
 * @projectName master
 * @description: ZEMCHO
 * @date 2021/3/26 0026 14:12
 */
public interface SystemService {
    /**
     * 获取系统基础配置信息
     *
     * @return
     */
    Result getBasicConfig();

    /**
     * 修改基础配置信息
     *
     * @param param
     * @return
     */
    Result updateBasicConfig(ConfigParam param);
    /**
     * 获取屏幕租用信息
     *
     * @return
     */
    Result getRentalContractConfig();

    /**
     * 修改屏幕店租用合约配置
     *
     * @param param
     * @return
     */
    Result updateRentalContractConfig(ConfigParam param);

    /**
     * 获取登录页配图
     *
    */
    Result getLoginPic();

    /**
     * 保存登录页配图
     *
     */
    Result saveLoginPicConfig(@Valid ConfigParam param);

    Result getMerchantNoticeConfig();

    Result updateMerchantNoticeConfig(@Valid ConfigParam param);
}
