package com.zemcho.ddql.util.excel.converter.order;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Map;

public class RechargeOrderStatusConverter implements Converter<Integer> {
    // 映射关系
    private final static Map<Integer, String> STATUS_MAP = Map.of(0, "", 1, "待支付", 2, "已支付", 3, "已取消", 4, "已退款");

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
        WriteCellData<String> cellData = new WriteCellData<>(STATUS_MAP.get(value));
        return cellData;
    }
}
