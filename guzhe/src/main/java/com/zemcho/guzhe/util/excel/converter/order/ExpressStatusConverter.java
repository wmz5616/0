package com.zemcho.guzhe.util.excel.converter.order;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Map;

public class ExpressStatusConverter implements Converter<Integer> {
    // 映射关系
    private final static Map<Integer, String> STATUS_MAP = Map.ofEntries(
            Map.entry(-2, ""),
            Map.entry(-1, "待发货"),
            Map.entry(0, "在途"),
            Map.entry(1, "揽件"),
            Map.entry(2, "疑难"),
            Map.entry(3, "签收"),
            Map.entry(4, "退签"),
            Map.entry(5, "派件"),
            Map.entry(6, "退回"),
            Map.entry(10, "待清关"),
            Map.entry(11, "清关中"),
            Map.entry(12, "已清关"),
            Map.entry(13, "清关异常"),
            Map.entry(14, "收件人拒签")
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
        WriteCellData<String> cellData = new WriteCellData<>(STATUS_MAP.getOrDefault(value, ""));
        return cellData;
    }
}
