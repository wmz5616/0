package com.zemcho.guzhe.controller.cas;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.CasUserParam;
import com.zemcho.guzhe.controller.cas.vo.CasUserVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.service.cas.UserService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.excel.ExcelUtil;
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
     * 编辑用户状态
     * searchId
     * @param
     * @return
     */
    @RequestMapping("/update")
    public Result updateStatus(@Validated @RequestBody CasUserParam param, BindingResult result,
                               @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return userService.updateStatus(param);
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
