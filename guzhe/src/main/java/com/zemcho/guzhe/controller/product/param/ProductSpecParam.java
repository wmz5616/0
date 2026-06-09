package com.zemcho.guzhe.controller.product.param;

import lombok.Data;

import java.util.List;

/**
 * 商品规格参数（嵌套结构，规格类型包含其下的规格值）
 */
@Data
public class ProductSpecParam {

    /**
     * 规格类型ID
     * 新增时不传或传0，修改时传真实数据库ID
     */
    private Integer id;

    /**
     * 规格类型名称（如：颜色、尺码）
     */
    private String typeName;

    /**
     * 规格类型排序
     */
    private Integer sort;

    /**
     * 该规格类型下的规格值列表
     */
    private List<SpecValueItem> specValues;

    /**
     * 规格值项
     */
    @Data
    public static class SpecValueItem {
        /**
         * 规格值ID
         * 新增时不传或传0，修改时传真实数据库ID
         */
        private Integer id;

        /**
         * 规格值名称（如：黄色、36码）
         */
        private String valueName;

        /**
         * 排序
         */
        private Integer sort;
    }
}