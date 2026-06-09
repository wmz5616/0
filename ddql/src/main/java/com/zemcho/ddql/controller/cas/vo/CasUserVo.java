package com.zemcho.ddql.controller.cas.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.zemcho.ddql.entity.cas.CasUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasUserVo extends CasUser {
    @ExcelProperty(value = "团队")
    @ColumnWidth(20)
    private String teamNames;
//
//    @ExcelProperty(value = "健康币")
//    @ColumnWidth(10)
//    private String healthyCoin ;
}
