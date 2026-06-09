package com.zemcho.ddql.controller.appVersion;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.app.AppVersion;
import com.zemcho.ddql.service.appVersion.AppVersionService;
import com.zemcho.ddql.util.Constant;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestAttribute;
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
     * @param authAttrData
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增版本", module = "App版本管理")
    public Result add(@Valid @RequestBody AppVersion data, BindingResult result,
                      @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.add(data);
    }


    /**
     * @param data
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改版本", module = "App版本管理")
    public Result update(@Valid @RequestBody AppVersion data, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.update(data);
    }


    /**
     * pageNum pageSize
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/select")
    public Result select(@Valid @RequestBody SearchParam param, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.select(param);
    }

    /**
     * id
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除版本", module = "App版本管理")
    public Result delete(@Valid @RequestBody AppVersion param, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData){
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return appVersionService.delete(param.getId());
    }

}
