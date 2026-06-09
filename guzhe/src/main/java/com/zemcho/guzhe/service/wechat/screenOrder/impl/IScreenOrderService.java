package com.zemcho.guzhe.service.wechat.screenOrder.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderDisplayUpdateParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderInfoParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderListParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotQueryParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.param.ScreenOrderScreenshotTaskParam;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.EquipmentScreenshotVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderContractVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderInfoVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderListItemVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderListVo;
import com.zemcho.guzhe.controller.wechat.screenOrder.vo.ScreenOrderSummaryVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.equipment.EquipmentScreenshot;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrderLog;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrder;
import com.zemcho.guzhe.entity.sys.Config;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentScreenshotMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderLogMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import com.zemcho.guzhe.mapper.sys.ConfigMapper;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.service.wechat.screenOrder.ScreenOrderService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店位订单
 */
@Service
public class IScreenOrderService implements ScreenOrderService {
    private static final int STATUS_WAIT_EFFECTIVE = 1;
    private static final int STATUS_EFFECTIVE = 2;
    private static final int LOG_EDIT_DISPLAY = 2;
    private static final int SCREENSHOT_STATUS_PENDING = 0;

    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @Autowired
    private ScreenRentalOrderLogMapper screenRentalOrderLogMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ShopManagerService shopManagerService;

    @Autowired
    private EquipmentScreenshotMapper equipmentScreenshotMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result lists(ScreenOrderListParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        // 店位订单按店铺管理者权限校验：当前用户必须是该店铺管理者
        Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, param.getShopId());
        if (!checkResult.success()) {
            return checkResult;
        }

        // 默认本月，因此未传时间时按“本月第一天 ~ 本月最后一天”查询
        LocalDate startDate = param.getStartDate();
        LocalDate endDate = param.getEndDate();
        LocalDate now = LocalDate.now();
        if (startDate == null) {
            startDate = now.withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = now.withDayOfMonth(now.lengthOfMonth());
        }
        if (startDate.isAfter(endDate)) {
            return Result.error("开始时间不能大于结束时间");
        }

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        ScreenOrderSummaryVo summary =
                screenRentalOrderMapper.selectWechatOrderSummary(param, startTime, endTime);
        if (summary == null) {
            summary = new ScreenOrderSummaryVo();
        }
        if (summary.getOrderCount() == null) {
            summary.setOrderCount(0);
        }
        if (summary.getTotalAmount() == null) {
            summary.setTotalAmount(0);
        }

        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<ScreenOrderListItemVo> list =
                screenRentalOrderMapper.selectWechatOrderLists(param, startTime, endTime);
        if (list == null) {
            list = Collections.emptyList();
        }

        for (ScreenOrderListItemVo item : list) {
            item.setStatusText(buildStatusText(item.getStatus()));
            item.setTotalAmountText(formatAmount(item.getTotalAmount()));
            if (item.getStatus() != null && item.getStatus() == 4) {
                item.setRemarkLabel("驳回原因");
            } else if (item.getStatus() != null && item.getStatus() == 5) {
                item.setRemarkLabel("撤销原因");
            }
        }

        ScreenOrderListVo result = new ScreenOrderListVo();
        result.setOrderCount(summary.getOrderCount());
        result.setTotalAmount(summary.getTotalAmount());
        result.setTotalAmountText(formatAmount(summary.getTotalAmount()));
        result.setList(list);
        result.setPageInfo(new PageInfo<>(list));
        return Result.success("获取成功", result);
    }

    @Override
    public Result info(ScreenOrderInfoParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getShopId() == null) {
            return Result.error("订单店铺信息异常");
        }

        // 详情也按店铺管理者权限校验
        Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, order.getShopId());
        if (!checkResult.success()) {
            return checkResult;
        }

        ScreenOrderInfoVo info = screenRentalOrderMapper.selectWechatOrderInfo(param.getOrderId());
        if (info == null) {
            return Result.error("订单不存在");
        }

        info.setStatusText(buildStatusText(info.getStatus()));
        info.setTotalAmountText(formatAmount(info.getTotalAmount()));
        info.setDisplayTypeText(buildDisplayTypeText(info.getDisplayType()));
        info.setOrderUserText(buildOrderUserText(info.getNickName(), info.getPhone()));
        if (info.getStatus() != null && info.getStatus() == 4) {
            info.setRemarkLabel("驳回原因");
        } else if (info.getStatus() != null && info.getStatus() == 5) {
            info.setRemarkLabel("撤销原因");
        }
        return Result.success("获取成功", info);
    }

    @Override
    public Result updateDisplayType(ScreenOrderDisplayUpdateParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (param.getDisplayType() == null || (param.getDisplayType() != 1 && param.getDisplayType() != 2)) {
            return Result.error("展示内容类型错误");
        }

        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getShopId() == null) {
            return Result.error("订单店铺信息异常");
        }

        Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, order.getShopId());
        if (!checkResult.success()) {
            return checkResult;
        }
        if (order.getStatus() == null || (order.getStatus() != STATUS_WAIT_EFFECTIVE && order.getStatus() != STATUS_EFFECTIVE)) {
            return Result.error("当前订单状态不允许修改展示内容");
        }
        if (param.getDisplayType().equals(order.getDisplayType())) {
            return Result.success("修改成功");
        }

        ScreenRentalOrder updateOrder = new ScreenRentalOrder();
        updateOrder.setId(order.getId());
        updateOrder.setDisplayType(param.getDisplayType());
        screenRentalOrderMapper.update(updateOrder);

        ScreenRentalOrderLog log = new ScreenRentalOrderLog();
        log.setOrderId(order.getId());
        log.setOperationType(LOG_EDIT_DISPLAY);
        log.setOperationResult(0);
        log.setOperatorId(userId);
        log.setOperatorName(userInfo.getNickname() == null ? "" : userInfo.getNickname());
        log.setOperatorPhone(userInfo.getPhone() == null ? "" : userInfo.getPhone());
        log.setDisplayType(param.getDisplayType());
        log.setOperationTime(LocalDateTime.now());
        screenRentalOrderLogMapper.insert(log);
        return Result.success("修改成功");
    }

    @Override
    public Result createScreenshotTask(ScreenOrderScreenshotTaskParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        // 校验各种参数
        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getShopId() == null || order.getEquipmentId() == null) {
            return Result.error("订单信息异常");
        }
        Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, order.getShopId());
        if (!checkResult.success()) {
            return checkResult;
        }
        Equipment equipmentInfo = equipmentMapper.selectById(order.getEquipmentId());
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        // 创建截图任务
        EquipmentScreenshot screenshot = new EquipmentScreenshot();
        screenshot.setEquipmentId(order.getEquipmentId());
        screenshot.setSerialNumber(equipmentInfo.getSerialNumber() == null ? "" : equipmentInfo.getSerialNumber());
        screenshot.setScreenshotUrl("");
        screenshot.setScreenshotStatus(SCREENSHOT_STATUS_PENDING); // 0-待截图 1-成功 2-失败
        screenshot.setFailReason("");
        screenshot.setCreateTime(LocalDateTime.now());
        screenshot.setUpdateTime(LocalDateTime.now());
        equipmentScreenshotMapper.insert(screenshot);

        // 创建截图任务进redis
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("needScreenshot", true);
        taskData.put("screenshotId", screenshot.getId());
        redisUtil.set(buildScreenshotTaskKey(order.getEquipmentId()), taskData, 0, null);

        Map<String, Object> result = new HashMap<>();
        result.put("screenshotId", screenshot.getId());
        result.put("equipmentId", order.getEquipmentId());
        return Result.success("操作成功", result);
    }

    @Override
    public Result screenshotInfo(ScreenOrderScreenshotQueryParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getShopId() == null || order.getEquipmentId() == null) {
            return Result.error("订单信息异常");
        }
        Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, order.getShopId());
        if (!checkResult.success()) {
            return checkResult;
        }

        EquipmentScreenshot screenshot;
        if (param.getScreenshotId() != null) {
            screenshot = equipmentScreenshotMapper.selectById(param.getScreenshotId());
            if (screenshot == null || !order.getEquipmentId().equals(screenshot.getEquipmentId())) {
                return Result.error("截图记录不存在");
            }
        } else {
            screenshot = equipmentScreenshotMapper.selectLatestByEquipmentId(order.getEquipmentId());
            if (screenshot == null) {
                return Result.success("获取成功", null);
            }
        }

        // 如果当前截图状态为0(待截图)且URL为空，回退到上一次成功截图
        if (screenshot.getScreenshotStatus() != null && screenshot.getScreenshotStatus().equals(0)
                && (screenshot.getScreenshotUrl() == null || screenshot.getScreenshotUrl().isEmpty())) {
            EquipmentScreenshot latestSuccess = equipmentScreenshotMapper.selectLatestSuccessByEquipmentId(order.getEquipmentId());
            if (latestSuccess != null) {
                screenshot.setScreenshotUrl(latestSuccess.getScreenshotUrl());
                screenshot.setUpdateTime(latestSuccess.getUpdateTime());
            }
        }

        EquipmentScreenshotVo result = new EquipmentScreenshotVo();
        BeanUtils.copyProperties(screenshot, result);
        return Result.success("获取成功", result);
    }

    @Override
    public Result contractStatus() {
        Config config = configMapper.selectConfigByKey("show_rental_contract");
        String value = config == null || config.getValue() == null ? "0" : config.getValue();
        if (!"0".equals(value) && !"1".equals(value)) {
            value = "0";
        }
        return Result.success("获取成功", value);
    }

    @Override
    public Result contractContent() {
        Config config = configMapper.selectConfigByKey("rental_contract");
        ScreenOrderContractVo result = new ScreenOrderContractVo();
        if (config != null) {
            result.setContent(config.getValue() == null ? "" : config.getValue());
            result.setCreateTime(config.getCreateTime());
            result.setUpdateTime(config.getUpdateTime());
        } else {
            result.setContent("");
        }
        return Result.success("获取成功", result);
    }

    /**
     * 订单状态文案按原型输出，5 对外统一显示为“已取消”。
     */
    private String buildStatusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "待确认";
            case 1 -> "待生效";
            case 2 -> "生效中";
            case 3 -> "已完成";
            case 4 -> "已驳回";
            case 5 -> "已取消";
            default -> "";
        };
    }

    /**
     * 分转元，统一保留 2 位小数。
     */
    private String formatAmount(Integer amount) {
        if (amount == null) {
            amount = 0;
        }
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * 展示内容类型文案。
     */
    private String buildDisplayTypeText(Integer displayType) {
        if (displayType == null) {
            return "";
        }
        return switch (displayType) {
            case 1 -> "商品";
            case 2 -> "海报";
            default -> "";
        };
    }

    /**
     * 原型里“下单人”一行展示昵称和手机号，这里直接拼成前端可直接使用的文案。
     */
    private String buildOrderUserText(String nickName, String phone) {
        String safeNickName = nickName == null ? "" : nickName.trim();
        String safePhone = phone == null ? "" : phone.trim();
        if (safeNickName.isEmpty()) {
            return safePhone;
        }
        if (safePhone.isEmpty()) {
            return safeNickName;
        }
        return safeNickName + " " + safePhone;
    }

    private String buildScreenshotTaskKey(Integer equipmentId) {
        return Constant.EQUIPMENT_SCREENSHOT_TASK_PREFIX + equipmentId;
    }
}
