package com.zemcho.guzhe.service.screen_order.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageAuditParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageCancelParam;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageInfoParam;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageInfoVo;
import com.zemcho.guzhe.controller.screen_order.param.ScreenOrderManageListParam;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageItemVo;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageListVo;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageLogVo;
import com.zemcho.guzhe.controller.screen_order.vo.ScreenOrderManageSummaryVo;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrder;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrderLog;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalDetailMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderLogMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import com.zemcho.guzhe.service.screen_order.ScreenOrderManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * 后台店位订单管理
 */
@Service
public class IScreenOrderManageService implements ScreenOrderManageService {
    private static final int STATUS_WAIT_CONFIRM = 0;
    private static final int STATUS_WAIT_EFFECTIVE = 1;
    private static final int STATUS_EFFECTIVE = 2;
    private static final int STATUS_COMPLETED = 3;
    private static final int STATUS_REJECTED = 4;
    private static final int STATUS_CANCELLED = 5;

    private static final int LOG_CREATE = 1;
    private static final int LOG_EDIT_DISPLAY = 2;
    private static final int LOG_AUDIT = 3;
    private static final int LOG_CANCEL = 4;

    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @Autowired
    private ScreenRentalDetailMapper screenRentalDetailMapper;

    @Autowired
    private ScreenRentalOrderLogMapper screenRentalOrderLogMapper;

    @Autowired
    private CasAdminMapper casAdminMapper;

    @Override
    public Result lists(ScreenOrderManageListParam param, String token) {
        if (param.getStartTime() != null && param.getEndTime() != null
                && param.getStartTime().isAfter(param.getEndTime())) {
            return Result.error("开始时间不能大于结束时间");
        }

        ScreenOrderManageSummaryVo summary = screenRentalOrderMapper.selectAdminOrderSummary(param);
        if (summary == null) {
            summary = new ScreenOrderManageSummaryVo();
        }
        if (summary.getOrderCount() == null) {
            summary.setOrderCount(0);
        }
        if (summary.getTotalAmount() == null) {
            summary.setTotalAmount(0);
        }

        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<ScreenOrderManageItemVo> list = screenRentalOrderMapper.selectAdminOrderLists(param);
        if (list == null) {
            list = Collections.emptyList();
        }

        for (ScreenOrderManageItemVo item : list) {
            item.setStatusText(buildStatusText(item.getStatus()));
            item.setTotalAmountText(formatAmount(item.getTotalAmount()));
            if (item.getStatus() != null && item.getStatus() == 4) {
                item.setRemarkLabel("驳回原因");
            } else if (item.getStatus() != null && item.getStatus() == 5) {
                item.setRemarkLabel("撤销原因");
            }
        }

        ScreenOrderManageListVo result = new ScreenOrderManageListVo();
        result.setOrderCount(summary.getOrderCount());
        result.setTotalAmount(summary.getTotalAmount());
        result.setTotalAmountText(formatAmount(summary.getTotalAmount()));
        result.setPageInfo(new PageInfo<>(list));
        return Result.success("获取成功", result);
    }

    @Override
    public Result info(ScreenOrderManageInfoParam param, String token) {
        ScreenOrderManageInfoVo info = screenRentalOrderMapper.selectAdminOrderInfo(param.getOrderId());
        if (info == null) {
            return Result.error("订单不存在");
        }

        info.setStatusText(buildStatusText(info.getStatus()));
        info.setTotalAmountText(formatAmount(info.getTotalAmount()));
        info.setDisplayTypeText(buildDisplayTypeText(info.getDisplayType()));
        info.setOrderUserText(buildOrderUserText(info.getNickName(), info.getPhone()));
        info.setCanAudit(info.getStatus() != null && info.getStatus() == STATUS_WAIT_CONFIRM);
        info.setCanCancel(info.getStatus() != null
                && (info.getStatus() == STATUS_WAIT_CONFIRM || info.getStatus() == STATUS_WAIT_EFFECTIVE));
        if (info.getStatus() != null && info.getStatus() == STATUS_REJECTED) {
            info.setRemarkLabel("驳回原因");
        } else if (info.getStatus() != null && info.getStatus() == STATUS_CANCELLED) {
            info.setRemarkLabel("撤销原因");
        }

        List<ScreenRentalOrderLog> logList = screenRentalOrderLogMapper.selectByOrderId(param.getOrderId());
        List<ScreenOrderManageLogVo> operationRecords = new ArrayList<>();
        for (ScreenRentalOrderLog item : logList) {
            ScreenOrderManageLogVo logVo = new ScreenOrderManageLogVo();
            logVo.setId(item.getId());
            logVo.setOperationTime(item.getOperationTime());
            logVo.setOperationType(item.getOperationType());
            logVo.setOperationTypeText(buildOperationTypeText(item.getOperationType()));
            logVo.setOperationResult(item.getOperationResult());
            logVo.setOperationResultText(buildOperationResultText(item.getOperationResult()));
            logVo.setOperatorName(item.getOperatorName());
            logVo.setOperatorPhone(item.getOperatorPhone());
            logVo.setOperatorText(buildOrderUserText(item.getOperatorName(), item.getOperatorPhone()));
            logVo.setDisplayType(item.getDisplayType());
            logVo.setDisplayTypeText(buildDisplayTypeText(item.getDisplayType()));
            logVo.setOperationRemark(item.getOperationRemark());
            logVo.setFileUrl(item.getFileUrl());
            operationRecords.add(logVo);
        }
        info.setOperationRecords(operationRecords);
        return Result.success("获取成功", info);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result audit(ScreenOrderManageAuditParam param, String token) {
        AuthResult authResult = checkAdminToken(token);
        if (!authResult.result.success()) {
            return authResult.result;
        }

        if (param.getResult() == null || (param.getResult() != 1 && param.getResult() != 2)) {
            return Result.error("审核结果错误");
        }
        if (param.getResult() == 2 && isBlank(param.getRemark())) {
            return Result.error("驳回原因不能为空");
        }

        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != STATUS_WAIT_CONFIRM) {
            return Result.error("当前订单状态不允许审核");
        }

        Integer targetStatus = param.getResult() == 1 ? buildApprovedStatus() : STATUS_REJECTED;

        ScreenRentalOrder updateOrder = new ScreenRentalOrder();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(targetStatus);
        updateOrder.setRemark(param.getResult() == 2 ? safeTrim(param.getRemark()) : safeTrim(param.getRemark()));
        screenRentalOrderMapper.update(updateOrder);
        screenRentalDetailMapper.updateStatusByOrderId(order.getId(), targetStatus);

        ScreenRentalOrderLog log = new ScreenRentalOrderLog();
        log.setOrderId(order.getId());
        log.setOperationType(LOG_AUDIT);
        log.setOperationResult(param.getResult());
        log.setOperatorId(authResult.admin.getId());
        log.setOperatorName(defaultString(authResult.admin.getName()));
        log.setOperatorPhone(defaultString(authResult.admin.getAccount()));
        log.setOperationRemark(safeTrim(param.getRemark()));
        log.setFileUrl(safeTrim(param.getFileUrl()));
        log.setOperationTime(LocalDateTime.now());
        screenRentalOrderLogMapper.insert(log);

        return Result.success(param.getResult() == 1 ? "审核确认成功" : "审核驳回成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancel(ScreenOrderManageCancelParam param, String token) {
        AuthResult authResult = checkAdminToken(token);
        if (!authResult.result.success()) {
            return authResult.result;
        }

        ScreenRentalOrder order = screenRentalOrderMapper.selectById(param.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() == null
                || (order.getStatus() != STATUS_WAIT_CONFIRM && order.getStatus() != STATUS_WAIT_EFFECTIVE)) {
            return Result.error("当前订单状态不允许撤销");
        }

        ScreenRentalOrder updateOrder = new ScreenRentalOrder();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(STATUS_CANCELLED);
        updateOrder.setRemark(safeTrim(param.getCancelReason()));
        screenRentalOrderMapper.update(updateOrder);
        screenRentalDetailMapper.updateStatusByOrderId(order.getId(), STATUS_CANCELLED);

        ScreenRentalOrderLog log = new ScreenRentalOrderLog();
        log.setOrderId(order.getId());
        log.setOperationType(LOG_CANCEL);
        log.setOperationResult(0);
        log.setOperatorId(authResult.admin.getId());
        log.setOperatorName(defaultString(authResult.admin.getName()));
        log.setOperatorPhone(defaultString(authResult.admin.getAccount()));
        log.setOperationRemark(safeTrim(param.getCancelReason()));
        log.setFileUrl(safeTrim(param.getFileUrl()));
        log.setOperationTime(LocalDateTime.now());
        screenRentalOrderLogMapper.insert(log);

        return Result.success("撤销成功");
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
     * 订单详情和操作记录里统一展示“姓名 手机号”。
     */
    private String buildOrderUserText(String name, String phone) {
        String safeName = name == null ? "" : name.trim();
        String safePhone = phone == null ? "" : phone.trim();
        if (safeName.isEmpty()) {
            return safePhone;
        }
        if (safePhone.isEmpty()) {
            return safeName;
        }
        return safeName + " " + safePhone;
    }

    /**
     * 操作类型文案。
     */
    private String buildOperationTypeText(Integer operationType) {
        if (operationType == null) {
            return "";
        }
        return switch (operationType) {
            case LOG_CREATE -> "创建订单";
            case LOG_EDIT_DISPLAY -> "编辑展示内容";
            case LOG_AUDIT -> "后台审核";
            case LOG_CANCEL -> "后台撤销";
            default -> "";
        };
    }

    /**
     * 操作结果文案。
     */
    private String buildOperationResultText(Integer operationResult) {
        if (operationResult == null) {
            return "";
        }
        return switch (operationResult) {
            case 1 -> "确认";
            case 2 -> "驳回";
            default -> "";
        };
    }

    /**
     * 审核确认后根据当前时间切换成待生效或生效中。
     */
    private Integer buildApprovedStatus() {
        LocalDate now = LocalDate.now();
        return now.getDayOfMonth() == 1 ? STATUS_EFFECTIVE : STATUS_WAIT_EFFECTIVE;
    }

    /**
     * 校验后台管理员 token，并返回当前管理员信息。
     */
    private AuthResult checkAdminToken(String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null || authJwtData.getAdminId() == null) {
            return new AuthResult(Result.error("用户未登录"), null);
        }

        CasAdmin admin = casAdminMapper.selectById(authJwtData.getAdminId());
        if (admin == null) {
            return new AuthResult(Result.error("管理员不存在"), null);
        }
        return new AuthResult(Result.success("success"), admin);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private static class AuthResult {
        private final Result result;
        private final CasAdmin admin;

        private AuthResult(Result result, CasAdmin admin) {
            this.result = result;
            this.admin = admin;
        }
    }
}
