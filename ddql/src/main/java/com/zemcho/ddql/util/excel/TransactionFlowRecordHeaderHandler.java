package com.zemcho.ddql.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.zemcho.ddql.controller.sys.vo.TransactionFlowSummaryVo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class TransactionFlowRecordHeaderHandler implements SheetWriteHandler {

    private final TransactionFlowSummaryVo summary;

    public TransactionFlowRecordHeaderHandler(TransactionFlowSummaryVo summary) {
        this.summary = summary;
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
        titleCell.setCellValue("交易流水");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // Row 1: 交易时间 & 总笔数
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("交易时间");
        row1.createCell(1).setCellValue("共" + summary.getTotalCount() + "笔");

        // Row 2: 收入笔数 & 收入金额
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("收入" + summary.getIncomeCount() + "笔");
        row2.createCell(1).setCellValue("收入金额 ￥" + summary.getIncomeAmount());

        // Row 3: 支出笔数 & 支出金额
        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("支出 " + summary.getExpenseCount() + " 笔");
        row3.createCell(1).setCellValue("支出金额 ￥" + summary.getExpenseAmount());

        // Row 4: 手续费总计
        Row row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("手续费金额 ￥" + summary.getTotalServiceFee());

        sheet.createRow(5); // 空行
    }
}
