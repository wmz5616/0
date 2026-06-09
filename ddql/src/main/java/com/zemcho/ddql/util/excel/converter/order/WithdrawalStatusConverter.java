package com.zemcho.ddql.util.excel.converter.order;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Map;

public class WithdrawalStatusConverter implements Converter<String> {
    // 映射关系
    private final static Map<String, String> STATUS_MAP = Map.ofEntries(
            Map.entry("", "失败"),
            Map.entry("ACCEPTED", "转账已受理"),
            Map.entry("PROCESSING", "转账锁定资金中"),
            Map.entry("WAIT_USER_CONFIRM", "待收款用户确认"),
            Map.entry("TRANSFERING", "转账中"),
            Map.entry("SUCCESS", "转账成功"),
            Map.entry("FAIL", "转账失败"),
            Map.entry("CANCELING", "转账撤销中"),
            Map.entry("CANCELLED", "转账撤销完成")
    );

    @Override
    public Class<String> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(String value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        WriteCellData<String> cellData = new WriteCellData<>(STATUS_MAP.getOrDefault(value, ""));
        return cellData;
    }
}
