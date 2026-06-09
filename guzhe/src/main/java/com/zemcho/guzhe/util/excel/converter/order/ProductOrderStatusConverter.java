package com.zemcho.guzhe.util.excel.converter.order;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Map;

public class ProductOrderStatusConverter implements Converter<Integer> {
    // 映射关系
    private final static Map<Integer, String> STATUS_MAP = Map.ofEntries(
            Map.entry(0, "待支付"),
            Map.entry(1, "待使用"),
            Map.entry(2, "待发货"),
            Map.entry(3, "已发货"),
            Map.entry(4, "已完成"),
            Map.entry(5, "退款中"),
            Map.entry(6, "已退款"),
            Map.entry(7, "已过期"),
            Map.entry(8, "已取消")
    );

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
