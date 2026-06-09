package com.zemcho.ddql.controller.checkInSettings;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInTypeParam;
import com.zemcho.ddql.service.checkInSettings.CheckInTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/checkInType")
public class CheckInTypeController {

    @Autowired
    private CheckInTypeService checkInTypeService;

    /**
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增打卡类型", module = "打卡设置")
    public Result get(@Valid @RequestBody CheckInTypeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.add(param);
    }

    /**
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改打卡类型", module = "打卡设置")
    public Result update(@Valid @RequestBody CheckInTypeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.update(param);
    }

    /**
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/getCheckInTypeList")
    public Result getCheckInTypeList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.getCheckInTypeList(param);
    }

    /**
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除打卡类型", module = "打卡设置")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.delete(new ArrayList<>(param.getDeleteIds()));
    }

    /**
     * 修改打卡类型顺序
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "修改打卡类型顺序", module = "打卡设置")
    @RequestMapping("/sort/set")
    public Result setSort(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return checkInTypeService.setSort(param);
    }
}
