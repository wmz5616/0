package com.zemcho.guzhe.controller.equipment.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentParam {

    /**
     * 设备ID
     */
    private Integer id;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空")
    private String serialNumber;

    /**
     * 所属商超ID（必填）
     */
    @NotNull(message = "所属商超ID不能为空")
    private Integer supermarketId;

    /**
     * 客服电话（选填）
     */

    private String contactPhone;

    /**
     * 店位每月租金（必填，元，不可输入负数，可输入2位小数）
     */
    @NotNull(message = "设备每月租金不能为空")
    private Integer money;

    /**
     * 启用状态（必填，默认1-开启，0-禁用）
     */
    private Integer onlineStatus;

    /**
     * 启用状态（必填，默认1-开启，0-禁用）
     */
    private Integer status;
    /**
     * 排序（选填，默认0，数值大的排在前面）
     */
    private Integer sort;

    /**
     * 备注（选填）
     */
    private String remark;
}
