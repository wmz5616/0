package com.zemcho.guzhe.util.excel.converter.product;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;

import java.util.List;

public class ProductCategoryConverter implements Converter<List<CategoryVo>> {
    @Override
    public Class<List> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(List<CategoryVo> value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        String nameTemp = "";
        if (value != null && !value.isEmpty()) {
            // 将名称去重用拼接起来
            nameTemp = value.stream().map(CategoryVo::getName).distinct().reduce((a, b) -> a + "、" + b).get();
        }
        WriteCellData<String> cellData = new WriteCellData<>(nameTemp);
        return cellData;
    }
}
