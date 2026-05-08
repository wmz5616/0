package com.zemcho.guzhe.controller.product.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.zemcho.guzhe.util.excel.converter.product.ProductTicketStatusConverter;
import lombok.Data;
@Data
@ContentRowHeight(20)

@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER, verticalAlignment = VerticalAlignmentEnum.CENTER)
@ContentFontStyle(fontName = "宋体")
public class TicketDto {

    //序号
    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(10)
    private Integer sort;

    //券码
    @ExcelProperty(value = "券码", index = 1)
    @ColumnWidth(35)
    private String ticket;

    // 状态：1 未下发 2 已下发 3已核销
    @ExcelProperty(value = "状态", index = 2, converter = ProductTicketStatusConverter.class)
    @ColumnWidth(20)
    private Integer status;
}
