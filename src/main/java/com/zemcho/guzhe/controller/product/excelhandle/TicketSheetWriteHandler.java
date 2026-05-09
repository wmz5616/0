package com.zemcho.guzhe.controller.product.excelhandle;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class TicketSheetWriteHandler implements SheetWriteHandler {

    private String title;

    public TicketSheetWriteHandler(String title) {
        this.title = title;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
 
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        // 获取第一行
        Row row1 = sheet.createRow(0);
        row1.setHeight((short) 400);
        // 创建单元格
        Cell cell = row1.createCell(0);
        //设置标题文本
        cell.setCellValue(title);
        // 创建单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        // 设置垂直居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置水平居中对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 创建字体
        Font font = workbook.createFont();
        // 加粗
        font.setBold(true);
        // 字体大小
        font.setFontHeight((short)200);
        font.setFontName("宋体");
        cellStyle.setFont(font);
        // 将字体应用到样式
        cell.setCellStyle(cellStyle);
        // 合并第0列到第8列
        sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, 1));

    }
}