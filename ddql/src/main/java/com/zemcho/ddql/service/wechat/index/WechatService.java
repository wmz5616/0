package com.zemcho.ddql.service.wechat.index;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.wechat.index.param.*;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface WechatService {
    /**
     * 获取用户今日运动信息
     *
     * @param token
     * @return
     */
    Result getUserTodaySportInfo(String token);

    /**
     * 更新用户今日步数
     *
     * @param param
     * @param token
     * @return
     */
    Result userTodayStepNumUpdate(StepNumUpdateParam param, String token);

    /**
     * 获取打卡场地列表
     *
     * @param param
     * @return
     */
    Result getPlaceLists(SearchParam param);

    /**
     * 获取打卡场地信息
     *
     * @param param
     * @return
     */
    Result getPlaceInfo(SearchParam param);

    /**
     * 获取设备信息
     *
     * @param param
     * @return
     */
    Result getEquipmentInfo(SearchParam param);

    /**
     * 获取打卡配置信息
     *
     * @return
     */
    Result getCheckInSetting();

    /**
     * 用户打卡
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckIn(CheckInParam param, String token);

    /**
     * 获取用户打卡中的数据
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckInRunningInfo(SearchParam param, String token);

    /**
     * 取消用户打卡
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckInCancel(SearchParam param, String token);

    /**
     * 获取用户打卡详情
     *
     * @param param
     * @return
     */
    Result userCheckInInfo(SearchParam param);

    /**
     * 获取用户打卡列表
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckInList(SearchParam param, String token);

    /**
     * 获取用户打卡统计信息
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckInCount(SearchParam param, String token);

    /**
     * 用户打卡数据导出到邮箱
     *
     * @param param
     * @param token
     * @param request
     * @return
     */
    Result userCheckInExportToEmail(SearchParam param, String token, HttpServletRequest request);

    /**
     * 用户提现
     *
     * @param param
     * @param token
     * @return
     */
    Result userWithdrawal(UserWithdrawalParam param, String token);

    /**
     * 获取用户提现详情
     *
     * @param param
     * @return
     */
    Result userWithdrawalInfo(SearchParam param);

    /**
     * 用户提现数据导出到邮箱
     *
     * @param param
     * @return
     */
    Result userWithdrawalExportToEmail(UserCoinLogParam param, String token, HttpServletRequest request);

    /**
     * 获取币变更日志列表
     *
     * @param param
     * @param token
     * @return
     */
    Result coinLogLists(UserCoinLogParam param, String token);

    /**
     * 获取用户打卡排行榜列表
     *
     * @param param
     * @param token
     * @return
     */
    Result userCheckInRankLists(SearchParam param, String token);

    /**
     * 获取用户提现排行榜列表
     *
     * @param param
     * @param token
     * @return
     */
    Result userWithdrawalRankLists(SearchParam param, String token);

    /**
     * 导出用户打卡排行榜（月榜）
     *
     * @param param
     * @param token
     * @param response
     */
    void userCheckInRankExport(SearchParam param, String token, HttpServletResponse response);

    /**
     * 商品兑换
     *
     * @param param
     * @param token
     * @return
     */
    Result productExchange(ProductExchangeParam param, String token);

    /**
     * 获取商品订单支付配置信息
     *
     * @param param  订单参数
     * @param token  用户token
     * @return 支付配置信息
     */
    Result productExchangeOrderPayConfig(ProductExchangeOrderPayParam param, String token);

    /**
     * 商品订单支付回调
     *
     * @param param 回调参数
     * @return 回调结果
     */
    String productExchangeOrderPayCallback(WxJsPayCallBackResponse param);

    /**
     * 获取商品兑换列表
     *
     * @param param
     * @param token
     * @return
     */
    Result productExchangeLists(SearchParam param, String token);

    /**
     * 获取商品兑换详情
     *
     * @param param
     * @return
     */
    Result productExchangeInfo(SearchParam param);

    /**
     * 商品兑换退货申请
     *
     * @param param
     * @param token
     * @return
     */
    Result productExchangeRefund(ProductExchangeRefundParam param, String token);

    /**
     * 兑换券码核销
     *
     * @param param
     * @param token
     * @return
     */
    Result productExchangeTicketCheck(SearchParam param, String token);

    /**
     * 获取场所打卡记录列表
     *
     * @param param
     * @return
     */
    Result placeCheckInList(SearchParam param);
}
