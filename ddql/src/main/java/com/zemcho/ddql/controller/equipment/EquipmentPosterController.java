package com.zemcho.ddql.controller.equipment;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.equipment.param.EquipmentPosterParam;
import com.zemcho.ddql.service.equipment.EquipmentPosterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipmentPoster")
public class EquipmentPosterController {
    @Autowired
    private EquipmentPosterService equipmentPosterService;

    /**
     * 保存设备海报
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/save")
    @Log(description = "保存设备海报", module = "设备管理")
    public Result save(@Valid @RequestBody EquipmentPosterParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentPosterService.save(param);
    }

    /**
     * 查询设备海报
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentPosterService.select(param);
    }

    /**
     * 修改设备海报顺序
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "修改设备海报顺序", module = "设备管理")
    @RequestMapping("/sort/set")
    public Result setSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return equipmentPosterService.setSort(param);
    }

    /**
     * 删除设备海报
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除设备海报", module = "设备管理")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return equipmentPosterService.delete(param);
    }
}
