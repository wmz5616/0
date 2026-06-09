package com.zemcho.guzhe.controller.cas;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.AdminSaveParam;
import com.zemcho.guzhe.service.cas.AdminService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @title: AdminController
 * @Description:
 * @Date: 2025/5/9 17:12
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService service;

    /**
     * 新增管理员
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "新增管理员", module = "管理员管理")
    @RequestMapping("/add")
    public Result addAdmin(@Validated @RequestBody AdminSaveParam param, BindingResult result,
                           @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.addAdmin(param, authAttrData);
    }

    /**
     * 编辑管理员
     *
     * @param param
     * @param result
     * @param authAttrData
     * @param token
     * @return
     */
    @Log(description = "编辑管理员", module = "管理员管理")
    @RequestMapping("/update")
    public Result updateAdmin(@Validated @RequestBody AdminSaveParam param, BindingResult result,
                              @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.updateAdmin(param, authAttrData, token);
    }

    /**
     * 获取管理员列表
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/lists")
    public Result getAdminLists(@Validated @RequestBody SearchParam param, BindingResult result,
                                @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.getAdminLists(param, authAttrData);
    }

    /**
     * 编辑管理员状态
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "编辑管理员状态", module = "管理员管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeParam param, BindingResult result,
                            @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.setStatus(param, authAttrData);
    }

    /**
     * 删除管理员
     *
     * @param param
     * @param result
     * @param authAttrData
     * @return
     */
    @Log(description = "删除管理员", module = "管理员管理")
    @RequestMapping("/delete")
    public Result deleteAdmin(@Validated @RequestBody DeleteParam param, BindingResult result,
                              @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return service.deleteAdmin(param, authAttrData);
    }


}
