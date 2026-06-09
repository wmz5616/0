package com.zemcho.ddql.controller.checkInSettings;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInPlaceParam;
import com.zemcho.ddql.service.checkInSettings.CheckInPlaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/checkInPlace")
public class CheckInPlaceController {
    @Autowired
    private CheckInPlaceService checkInPlaceService;

    /**
     * 新增打卡场地
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增打卡地点", module = "打卡设置")
    public Result add(@Valid @RequestBody CheckInPlaceParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.add(param);
    }

    /**
     * 修改打卡场地
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改打卡地点", module = "打卡设置")
    public Result update(@Valid @RequestBody CheckInPlaceParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.update(param);
    }

    /**
     * 查询打卡场地
     *
     * @param param  name(keyword) checkInTypeId(searchId) status(searchIntStatus) startTime endTime pageNum pageSize
     * @param result
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.select(param);
    }

    /**
     * 查询打卡场所的管理员信息
     *
     * @param param  searchId
     * @param result
     * @return
     */
    @RequestMapping("/selectUserByPlaceId")
    public Result selectUserByPlaceId(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.selectUserByPlaceId(param.getSearchId());
    }

    /**
     * 删除打卡场地
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除打卡地点", module = "打卡设置")
    public Result delete(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.delete(new ArrayList<>(param.getDeleteIds()));
    }

    /**
     * 编辑打卡地点状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑打卡地点状态", module = "打卡设置")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return checkInPlaceService.setStatus(param);
    }
}
