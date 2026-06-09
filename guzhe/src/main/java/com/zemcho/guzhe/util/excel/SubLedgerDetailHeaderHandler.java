package com.zemcho.guzhe.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.zemcho.guzhe.controller.sys.vo.SubLedgerDetailSummaryVo;
import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.format.DateTimeFormatter;

public class SubLedgerDetailHeaderHandler implements SheetWriteHandler {

    private final SubLedgerDetailSummaryVo summary;
    private final TransactionFlowSearchParam param;

    public SubLedgerDetailHeaderHandler(SubLedgerDetailSummaryVo summary, TransactionFlowSearchParam param) {
        this.summary = summary;
        this.param = param;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);

        // Row 0: 标题
        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(30);
        Cell titleCell = row0.createCell(0);
        titleCell.setCellValue("分账明细");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // Row 1: 分账时间范围
        Row row1 = sheet.createRow(1);
        String startTimeStr = param.getStartTime() != null ? param.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        String endTimeStr = param.getEndTime() != null ? param.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        row1.createCell(0).setCellValue("分账时间：" + startTimeStr + " ~ " + endTimeStr);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

        // Row 2: 汇总数据
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue(String.format("分账总金额：%s，分账总笔数 %d，通莞手续费：%s，平台收费：%s",
                summary.getTotalDivideAmount(),
                summary.getTotalCount(),
                summary.getTotalTongGuanFee(),
                summary.getTotalPlatformFee()));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

        sheet.createRow(3); // 空行
    }
}
