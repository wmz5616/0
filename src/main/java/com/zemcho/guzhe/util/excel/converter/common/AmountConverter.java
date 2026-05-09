package com.zemcho.guzhe.util.excel.converter.common;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.zemcho.guzhe.util.BigDecimalUtil;

/**
 * @title: AmountConverter
 * @Description:
 * @Date: 2025/10/24 15:15
 */
public class AmountConverter implements Converter<Integer> {
    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public WriteCellData<Double> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        Double amount = BigDecimalUtil.divisionDouble(value, 100, 2);
        WriteCellData<Double> cellData = new WriteCellData<>(String.valueOf(amount));
        return cellData;
    }
}
