package com.zemcho.ddql.util.excel.converter.team;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Map;

public class TeamVerificationStatusConverter implements Converter<Integer> {
    // 映射关系
    private final static Map<Integer, String> STATUS_MAP = Map.of(0, "审核中", 1, "审核通过", 2, "审核驳回");


    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        WriteCellData<String> cellData = new WriteCellData<>(STATUS_MAP.getOrDefault(value, ""));
        return cellData;
    }
}
