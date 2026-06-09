package com.zemcho.ddql.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.zemcho.ddql.controller.wechat.shop.param.SettlementRecordSearchParam;
import com.zemcho.ddql.controller.wechat.shop.vo.SettlementRecordSummaryVo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.format.DateTimeFormatter;

/**
 * 结算记录导出表头
 */
public class SettlementRecordHeaderHandler implements SheetWriteHandler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final String shopName;
    private final SettlementRecordSearchParam param;
    private final SettlementRecordSummaryVo summary;

    public SettlementRecordHeaderHandler(String shopName, SettlementRecordSearchParam param,
                                         SettlementRecordSummaryVo summary) {
        this.shopName = shopName;
        this.param = param;
        this.summary = summary;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);

        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(28);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(shopName + "结算记录");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row rangeRow = sheet.createRow(1);
        String startDate = param.getStartDate() == null ? "" : param.getStartDate().format(DATE_FORMATTER);
        String endDate = param.getEndDate() == null ? "" : param.getEndDate().format(DATE_FORMATTER);
        Cell rangeCell = rangeRow.createCell(0);
        rangeCell.setCellValue(startDate + "-" + endDate);
        rangeCell.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        Row summaryRow = sheet.createRow(2);
        Cell summaryCell = summaryRow.createCell(0);
        summaryCell.setCellValue("共" + summary.getTotalCount() + "笔，结算总额：" + summary.getTotalAmount());
        summaryCell.setCellStyle(centerStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
    }
}
