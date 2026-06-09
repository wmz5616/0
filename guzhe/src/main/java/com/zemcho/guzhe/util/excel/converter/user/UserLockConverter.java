package com.zemcho.guzhe.util.excel.converter.user;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class UserLockConverter implements Converter<Integer> {
    // 映射关系
    private final static String[] LEVEL_MAP = {"正常","暂时锁定","永久锁定"};

    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        WriteCellData<String> cellData = new WriteCellData<>(LEVEL_MAP[value]);
        return cellData;
    }
}
