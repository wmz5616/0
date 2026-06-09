package com.zemcho.ddql.service.common;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.common.param.SmsCodeParam;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;

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
     * 获取商圈下拉列表
     *
     * @param param
     * @return
     */
    Result getBusinessCircleLists(SearchParam param);

    /**
     * 获取团体下拉列表
     *
     * @param param
     * @return
     */
    Result getTeamLists(TeamSearchParam param);

    /**
     * 获取店铺下拉列表
     *
     * @param param
     * @return
     */
    Result getShopLists(SearchParam param);

    /**
     * 获取快递公司下拉列表
     *
     * @param param
     * @return
     */
    Result getExpressCompanyLists(SearchParam param);

    /**
     * 获取用户端--文章列表
     *
     * @param param
     * @return
     */
    Result getWechatArticleList(SearchParam param);

    /**
     * 获取用户端--公告列表
     *
     * @param param
     * @return
     */
    Result getWechatNoticeList(SearchParam param);
}
