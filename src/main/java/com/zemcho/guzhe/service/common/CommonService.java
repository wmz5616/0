package com.zemcho.guzhe.service.common;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.common.param.SmsCodeParam;
import com.zemcho.guzhe.entity.sys.Region;

import java.util.LinkedList;

public interface CommonService {
    /**
     * 获取角色下拉列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    Result getRoleLists(SearchParam param, AuthAttrData authAttrData, String token);

    /**
     * 获取管理员下拉列表
     *
     * @param param
     * @return
     */
    Result getAdminLists(SearchParam param);

    /**
     * 发送短信验证码
     *
     * @param param
     * @return
     */
    Result sendSmsCode(SmsCodeParam param);

    /**
     * 获取图形验证码
     *
     * @return
     */
    Result getCaptchaCode();

    /**
     * 获取用户下拉列表
     *
     * @param param
     * @return
     */
    Result getUserLists(SearchParam param);

    /**
     * 获取快递公司下拉列表
     *
     * @param param
     * @return
     */
    Result getExpressCompanyLists(SearchParam param);

    /**
     * 根据当前地区id查询下级地区
     *
     * @param param
     * @return
     */
    Result getLowRegionList(SearchParam param);

    /**
     * 根据地区id查询上级地区
     *
     * @param param
     * @return
     */
    Result getRegionParentInfo(SearchParam param);

    /**
     * 根据地区id查询地区信息
     *
     * @param param
     * @return
     */
    Result getRegionInfo(SearchParam param);

    /**
     * 根据地区id查询对应的地区数据--按等级顺序返回所有对应的上级地区数据
     *
     * @param id
     * @return
     */
    LinkedList<Region> selectRegionDataById(Integer id);

    /**
     * 获取行业分类下拉列表
     *
     * @param param
     * @return
     */
    Result getIndustryCategoryLists(SearchParam param);

    /**
     * 获取商超下拉列表
     *
     * @param param
     * @return
     */
    Result getSupermarketLists(SearchParam param);

    /**
     * 获取商家下拉列表
     *
     * @param param
     * @return
     */
    Result getShopLists(SearchParam param);
}
