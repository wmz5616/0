package com.zemcho.ddql.controller.cas;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.vo.CasUserVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.service.cas.UserService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 修改用户 根据id修改
     *
     * @param user
     * @param result
     * @return
     */
    @Log(description = "修改用户", module = "用户管理")
    @RequestMapping("/update")
    public Result update(@Validated @RequestBody CasUser user, BindingResult result,
                         @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return userService.updateUser(user);
    }

    /**
     * 查询用户列表
     *
     * @param param  nickName(keyword) phone(param.searchStrField1) lock(param.searchField1) has_certification(searchField2)
     *               createTime(startTime - endTime)  pageNum pageSize
     * @param result
     * @return
     */
    @RequestMapping("/lists")
    public Result selectUserList(@Validated @RequestBody SearchParam param, BindingResult result,
                                 @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return userService.selectUserList(param);
    }

    /**
     * 查询用户详情
     * searchId
     * @param
     * @return
     */
    @RequestMapping("/detail")
    public Result selectUserDetail(@Validated @RequestBody SearchParam param, BindingResult result,
                                   @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return userService.selectUserDetail(param.getSearchId());
    }

    /**
     * 导出用户信息
     * @param param
     * @param response
     * @throws IOException
     */
    @RequestMapping("/export")
    public void export(@Validated @RequestBody SearchParam param, HttpServletResponse response) throws IOException {
        List<CasUserVo> list = userService.selectByIds(param);
        ExcelUtil.exportToWeb(response, list, "用户信息", "用户信息", CasUserVo.class);
    }

}
