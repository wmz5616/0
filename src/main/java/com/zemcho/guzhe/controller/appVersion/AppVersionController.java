package com.zemcho.guzhe.controller.appVersion;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.appVersion.param.AppVersionParam;
import com.zemcho.guzhe.service.appVersion.AppVersionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appVersion")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;


    /** serialNumber fileUrl release isPublish remark
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增版本", module = "App版本管理")
    public Result add(@Valid @RequestBody AppVersionParam data, BindingResult result){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.add(data);
    }


    /**
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改版本", module = "App版本管理")
    public Result update(@Valid @RequestBody AppVersionParam data, BindingResult result){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.update(data);
    }


    /**
     * pageNum pageSize
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.select(param);
    }

    /**
     * id
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除版本", module = "App版本管理")
    public Result delete(@Valid @RequestBody AppVersionParam param, BindingResult result){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.delete(param.getId());
    }

}
