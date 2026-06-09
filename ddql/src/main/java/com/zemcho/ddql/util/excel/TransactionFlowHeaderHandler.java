package com.zemcho.ddql.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.zemcho.ddql.controller.sys.vo.TransactionFlowSummaryVo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionFlowHeaderHandler implements SheetWriteHandler {

    private final TransactionFlowSummaryVo summary;
    private final String billDate;

    public TransactionFlowHeaderHandler(TransactionFlowSummaryVo summary, com.zemcho.ddql.controller.sys.param.TransactionFlowSearchParam param) {
        this.summary = summary;
        if (param != null && (param.getStartTime() != null || param.getEndTime() != null)) {
            String startStr = param.getStartTime() != null ? param.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            String endStr = param.getEndTime() != null ? param.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            if (!startStr.isEmpty() && !endStr.isEmpty()) {
                if (startStr.equals(endStr)) {
                    this.billDate = startStr;
                } else {
                    this.billDate = startStr + "至" + endStr;
                }
            } else if (!startStr.isEmpty()) {
                this.billDate = startStr + "起";
            } else {
                this.billDate = "截至" + endStr;
            }
        } else {
            this.billDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        // 样式准备
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);

        CellStyle labelStyle = workbook.createCellStyle();
        labelStyle.setAlignment(HorizontalAlignment.LEFT);
        Font labelFont = workbook.createFont();
        labelFont.setBold(false);
        labelStyle.setFont(labelFont);

        // Row 0: 标题
        Row row0 = sheet.createRow(0);
        row0.setHeightInPoints(30);
        Cell titleCell = row0.createCell(0);
        titleCell.setCellValue("交易明细");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

        // Row 1: 账单日期
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("账单日期");
        row1.createCell(1).setCellValue(billDate);

        // Row 2: 交易总金额 & 退款总金额
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("交易总金额");
        row2.createCell(1).setCellValue(summary.getIncomeAmount().toString());
        row2.createCell(2).setCellValue("退款总金额");
        row2.createCell(3).setCellValue(summary.getRefundAmount().toString());

        // Row 3: 交易总笔数 & 退款总笔数
        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("交易总笔数");
        row3.createCell(1).setCellValue(summary.getTotalCount());
        row3.createCell(2).setCellValue("退款总笔数");
        row3.createCell(3).setCellValue(summary.getRefundCount());

        // Row 4: 交易手续费金额 & 退款退回手续费金额
        Row row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("交易手续费金额");
        row4.createCell(1).setCellValue(summary.getTotalServiceFee().toString());
        row4.createCell(2).setCellValue("退款退回手续费金额");
        row4.createCell(3).setCellValue(summary.getRefundFeeReturn().toString());

        sheet.createRow(5); // 空行
    }
}
