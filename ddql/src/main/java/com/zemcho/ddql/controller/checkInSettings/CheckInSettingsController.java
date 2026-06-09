package com.zemcho.ddql.controller.checkInSettings;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;
import com.zemcho.ddql.service.checkInSettings.CheckInSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkInSettings")
public class CheckInSettingsController {

    @Autowired
    private CheckInSettingsService checkInSettingsService;


    /**
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/get")
    public Result get(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInSettingsService.getSettings(param);
    }


    /**
     * @param checkInSettings
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "更新打卡设置", module = "打卡设置管理")
    public Result update(@Valid @RequestBody CheckInSettings checkInSettings, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return checkInSettingsService.updateSettings(checkInSettings);
    }

}
