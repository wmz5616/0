package com.zemcho.guzhe.controller.equipment;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentRentalDisplayParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentParam;
import com.zemcho.guzhe.service.equipment.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 新增设备
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增设备", module = "设备管理")
    public Result add(@Valid @RequestBody EquipmentParam data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.add(data);
    }

    /**
     * 更新设备
     *
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改设备", module = "设备管理")
    public Result update(@Valid @RequestBody EquipmentParam data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.update(data);
    }

    /**
     * 查询设备
     *
     * @param param  keyword(序列号 serialNumber) searchIntStatus(在线状态 onlineStatus)
     *               searchField1(启用状态enableStatus) startTime(createTime) endTime
     *               (createTime)
     * @param result
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.select(param);
    }

    /**
     * 删除设备
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除设备", module = "设备管理")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.delete(new ArrayList<>(param.getDeleteIds()));
    }

    /**
     * 编辑设备状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑设备启用状态", module = "设备管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return equipmentService.setStatus(param);
    }

    /**
     * 获取设备日志列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/log/lists")
    public Result logLists(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentService.logLists(param);
    }

    /**
     * 设备店位租用展示列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/rental/display/lists")
    public Result rentalDisplayLists(@Validated @RequestBody EquipmentRentalDisplayParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return equipmentService.rentalDisplayLists(param);
    }
}
