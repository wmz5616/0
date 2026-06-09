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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * 后台店位订单管理
 */
@Service
public class IScreenOrderManageService implements ScreenOrderManageService {
    private static final int EXPORT_DATA_START_ROW_INDEX = 3;
    private static final DateTimeFormatter EXPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter EXPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter EXPORT_FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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
        Result validateResult = validateListDateRange(param);
        if (!validateResult.success()) {
            return validateResult;
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

        fillListDisplayFields(list);

        ScreenOrderManageListVo result = new ScreenOrderManageListVo();
        result.setOrderCount(summary.getOrderCount());
        result.setTotalAmount(summary.getTotalAmount());
        result.setTotalAmountText(formatAmount(summary.getTotalAmount()));
        result.setPageInfo(new PageInfo<>(list));
        return Result.success("获取成功", result);
    }

    @Override
    public void export(ScreenOrderManageListParam param, String token, HttpServletResponse response) {
        // 开始时间不能大于结束时间
        Result validateResult = validateListDateRange(param);
        if (!validateResult.success()) {
            throw new IllegalArgumentException(validateResult.getMsg());
        }

        // 计算订单总数和总金额
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

        // 获取数据列表
        List<ScreenOrderManageItemVo> list = screenRentalOrderMapper.selectAdminOrderLists(param);
        if (list == null) {
            list = Collections.emptyList();
        }
        fillListDisplayFields(list);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("店位订单");
            setupExportSheetLayout(sheet);
            fillExportSummary(sheet, param, summary);
            fillExportRows(sheet, list);

            String fileName = "店位订单_" + LocalDateTime.now().format(EXPORT_FILE_TIME_FORMATTER) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20"));

            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
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

        ScreenRentalOrder updateOrder = new ScreenRentalOrder();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(param.getResult() == 1 ? STATUS_WAIT_EFFECTIVE : STATUS_REJECTED);
        updateOrder.setRemark(param.getResult() == 2 ? safeTrim(param.getRemark()) : safeTrim(param.getRemark()));
        screenRentalOrderMapper.update(updateOrder);
        if (param.getResult() == 1) {
            syncApprovedStatusByOrderId(order.getId());
        } else {
            screenRentalDetailMapper.updateStatusByOrderId(order.getId(), STATUS_REJECTED);
        }

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
                || (order.getStatus() != STATUS_WAIT_CONFIRM && order.getStatus() != STATUS_WAIT_EFFECTIVE) && order.getStatus() != STATUS_EFFECTIVE) {
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

    private Result validateListDateRange(ScreenOrderManageListParam param) {
        if (param.getStartTime() != null && param.getEndTime() != null
                && param.getStartTime().isAfter(param.getEndTime())) {
            return Result.error("开始时间不能大于结束时间");
        }
        return Result.success("success");
    }

    private void fillListDisplayFields(List<ScreenOrderManageItemVo> list) {
        for (ScreenOrderManageItemVo item : list) {
            item.setStatusText(buildStatusText(item.getStatus()));
            item.setTotalAmountText(formatAmount(item.getTotalAmount()));
            if (item.getStatus() != null && item.getStatus() == STATUS_REJECTED) {
                item.setRemarkLabel("驳回原因");
            } else if (item.getStatus() != null && item.getStatus() == STATUS_CANCELLED) {
                item.setRemarkLabel("撤销原因");
            }
        }
    }

    /**
     * 审核通过后，按当前年月即时同步该订单及明细状态。
     */
    private void syncApprovedStatusByOrderId(Long orderId) {
        LocalDate now = LocalDate.now();
        screenRentalDetailMapper.refreshApprovedStatusByOrderId(orderId, now.getYear(), now.getMonthValue());
        screenRentalOrderMapper.refreshApprovedStatusByOrderId(orderId);
    }

    private void fillExportSummary(Sheet sheet, ScreenOrderManageListParam param, ScreenOrderManageSummaryVo summary) {
        Row summaryRow = sheet.getRow(1);
        Cell summaryCell = summaryRow.getCell(0);

        StringBuilder text = new StringBuilder();
        if (param.getStartTime() != null || param.getEndTime() != null) {
            String startText = param.getStartTime() == null ? "" : param.getStartTime().format(EXPORT_DATE_FORMATTER);
            String endText = param.getEndTime() == null ? "" : param.getEndTime().format(EXPORT_DATE_FORMATTER);
            text.append("支付时间：").append(startText).append("~").append(endText).append("，");
        }
        text.append("订单总数量：").append(summary.getOrderCount())
                .append("，订单总金额：").append(formatAmount(summary.getTotalAmount())).append(" 元");
        summaryCell.setCellValue(text.toString());
    }

    private void fillExportRows(Sheet sheet, List<ScreenOrderManageItemVo> list) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle dateStyle = buildDataRightCellStyle(workbook);
        CellStyle textStyle = buildDataLeftCellStyle(workbook);
        CellStyle amountStyle = buildDataRightCellStyle(workbook);

        int rowIndex = EXPORT_DATA_START_ROW_INDEX;
        for (ScreenOrderManageItemVo item : list) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(15.75F);
            setCellDate(row, 0, item.getOrderTime(), dateStyle);
            setCellString(row, 1, item.getSerialNumber(), textStyle);
            setCellString(row, 2, item.getBusinessCircleName(), textStyle);
            setCellString(row, 3, item.getMerchantName(), textStyle);
            setCellNumber(row, 4, formatAmount(item.getTotalAmount()), amountStyle);
            setCellString(row, 5, item.getRentalMonths(), textStyle);
            setCellString(row, 6, item.getOrderUserText(), textStyle);
            setCellString(row, 7, item.getOrderNo(), textStyle);
            setCellString(row, 8, item.getStatusText(), textStyle);
        }
    }

    private void setupExportSheetLayout(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        CellStyle titleStyle = buildTitleCellStyle(workbook);
        CellStyle summaryStyle = buildSummaryCellStyle(workbook);
        CellStyle headerStyle = buildHeaderCellStyle(workbook);

        // 支付时间列加宽，避免出现 ####
        sheet.setColumnWidth(0, 22 * 256);
        sheet.setColumnWidth(1, 19 * 256);
        sheet.setColumnWidth(2, 25 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 20 * 256 + 128);
        sheet.setColumnWidth(5, 34 * 256);
        sheet.setColumnWidth(6, 17 * 256 + 96);
        sheet.setColumnWidth(7, 23 * 256 + 128);
        sheet.setColumnWidth(8, 13 * 256);

        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(29F);
        for (int i = 0; i < 9; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellStyle(titleStyle);
        }
        titleRow.getCell(0).setCellValue("店位订单");
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

        Row summaryRow = sheet.createRow(1);
        summaryRow.setHeightInPoints(29F);
        for (int i = 0; i < 9; i++) {
            Cell cell = summaryRow.createCell(i);
            cell.setCellStyle(summaryStyle);
        }
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 8));

        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(18.75F);
        String[] headers = new String[]{"支付时间", "设备编号", "所属商超", "下单商家", "订单金额（元）", "租用月份", "下单人", "订单号", "订单状态"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }
    }

    // 标题展示
    private CellStyle buildTitleCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    // 汇总行展示
    private CellStyle buildSummaryCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    // 表头展示
    private CellStyle buildHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    // 数据居左展示
    private CellStyle buildDataLeftCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }

    // 数据居右扎实
    private CellStyle buildDataRightCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }


    private CellStyle buildDataDateCellStyle(Workbook workbook) {
        return buildDataRightCellStyle(workbook);
    }

    private CellStyle buildDataAmountCellStyle(Workbook workbook) {
        CellStyle style = buildDataRightCellStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        return style;
    }

    private void setCellDate(Row row, int columnIndex, LocalDateTime value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            // 直接写字符串，避免 Excel 列宽不足时显示 ####
            cell.setCellValue(value.format(EXPORT_DATETIME_FORMATTER));
        } else {
            cell.setCellValue("");
        }
    }

    private void setCellString(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (style != null) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(value == null ? "" : value);
    }

    private void setCellNumber(Row row, int columnIndex, String amountText, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (style != null) {
            cell.setCellStyle(style);
        }
        try {
            cell.setCellValue(new BigDecimal(amountText).doubleValue());
        } catch (Exception e) {
            cell.setCellValue(amountText == null ? "" : amountText);
        }
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
