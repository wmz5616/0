package com.zemcho.ddql.controller.wechat.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.BatchSetUserDepartmentParam;
import com.zemcho.ddql.controller.team.param.SetUserDepartmentParam;
import com.zemcho.ddql.controller.team.param.TeamDepartmentParam;
import com.zemcho.ddql.controller.team.param.ToggleMultiDepartmentParam;
import com.zemcho.ddql.entity.team.TeamDepartment;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 微信小程序团队部门管理Controller
 */
@RestController
@RequestMapping("/wechat/team/department")
public class WechatTeamDepartmentController {

    @Autowired
    private TeamDepartmentService teamDepartmentService;

    /**
     * 创建部门
     *
     * @param param  部门信息
     * @param result 校验结果
     * @param token  用户token
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result createDepartment(@Valid @RequestBody TeamDepartmentParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        TeamDepartment department = new TeamDepartment();
        department.setTeamId(param.getTeamId());
        department.setName(param.getName());
        department.setSort(param.getSort());
        department.setStatus(param.getStatus());

        return teamDepartmentService.createDepartment(department, token);
    }

    /**
     * 更新部门
     *
     * @param param  部门信息
     * @param result 校验结果
     * @param token  用户token
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result updateDepartment(@Valid @RequestBody TeamDepartmentParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        if (param.getId() == null) {
            return Result.error("部门ID不能为空");
        }

        TeamDepartment department = new TeamDepartment();
        department.setId(param.getId());
        department.setName(param.getName());
        department.setSort(param.getSort());
        department.setStatus(param.getStatus());

        return teamDepartmentService.updateDepartment(department, token);
    }

    /**
     * 删除部门
     *
     * @param param  部门ID（searchId）
     * @param result 校验结果
     * @param token  用户token
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result deleteDepartment(@Valid @RequestBody SearchParam param, BindingResult result,
                                   @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        if (param.getSearchId() == null) {
            return Result.error("部门ID不能为空");
        }

        return teamDepartmentService.deleteDepartment(param.getSearchId(), token);
    }

    /**
     * 获取部门成员
     *
     * @param param  部门ID（searchId）
     * @param result 校验结果
     * @return 部门成员
     */
    @PostMapping("/user/list")
    public Result getDepartmentUserList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        if (param.getSearchId() == null) {
            return Result.error("部门ID不能为空");
        }

        return teamDepartmentService.getDepartmentUserList(param.getSearchId());
    }

    /**
     * 获取团队的部门列表
     *
     * @param param  团队ID（searchId）
     * @param result 校验结果
     * @return 部门列表
     */
    @PostMapping("/list")
    public Result getDepartmentList(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        if (param.getSearchId() == null) {
            return Result.error("团队ID不能为空");
        }

        return teamDepartmentService.getDepartmentList(param.getSearchId());
    }

    /**
     * 设置成员部门
     *
     * @param param  设置参数
     * @param result 校验结果
     * @param token  用户token
     * @return 设置结果
     */
    @PostMapping("/setUserDepartment")
    public Result setUserDepartment(@Valid @RequestBody SetUserDepartmentParam param, BindingResult result,
                                    @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        return teamDepartmentService.setUserDepartment(param.getTeamUserId(), param.getDepartmentId(), token);
    }

    /**
     * 批量编辑成员部门
     *
     * @param param  批量编辑参数
     * @param result 校验结果
     * @param token  用户token
     * @return 设置结果
     */
    @PostMapping("/batchSetUserDepartment")
    public Result batchSetUserDepartment(@Valid @RequestBody BatchSetUserDepartmentParam param, BindingResult result,
                                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }

        return teamDepartmentService.batchSetUserDepartment(param.getTeamId(), param.getTeamUserIds(),
                param.getDepartmentId(), token);
    }

    /**
     * 切换团队多部门管理开关
     *
     * @param param  开关参数
     * @param result 校验结果
     * @param token  用户token
     * @return 操作结果
     */
    @PostMapping("/toggleMultiDepartment")
    public Result toggleMultiDepartment(@Valid @RequestBody ToggleMultiDepartmentParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        return teamDepartmentService.toggleMultiDepartment(param.getTeamId(), param.getIsMultiDepartment(), token);
    }
}
