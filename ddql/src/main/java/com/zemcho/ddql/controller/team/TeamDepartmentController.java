package com.zemcho.ddql.controller.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.SetUserDepartmentParam;
import com.zemcho.ddql.controller.team.param.TeamDepartmentParam;
import com.zemcho.ddql.controller.team.param.ToggleMultiDepartmentParam;
import com.zemcho.ddql.entity.team.TeamDepartment;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * =团队部门管理Controller
 */
@RestController
@RequestMapping("/team/department")
public class TeamDepartmentController {

    @Autowired
    private TeamDepartmentService teamDepartmentService;

    /**
     * 新增部门
     *
     * @param param  部门信息
     * @param result 校验结果
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result addDepartment(@Valid @RequestBody TeamDepartmentParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        TeamDepartment department = new TeamDepartment();
        department.setTeamId(param.getTeamId());
        department.setName(param.getName());
        department.setSort(param.getSort());
        department.setStatus(param.getStatus());

        return teamDepartmentService.sysCreateDepartment(department);
    }

    /**
     * 更新部门
     *
     * @param param  部门信息
     * @param result 校验结果
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result updateDepartment(@Valid @RequestBody TeamDepartmentParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }

        TeamDepartment department = new TeamDepartment();
        department.setId(param.getId());
        department.setName(param.getName());
        department.setSort(param.getSort());
        department.setStatus(param.getStatus());

        return teamDepartmentService.sysUpdateDepartment(department);
    }

    /**
     * 删除部门
     */
    @PostMapping("/delete")
    public Result deleteDepartment(@RequestBody SearchParam searchParam) {
        if (searchParam == null || searchParam.getSearchId() == null) {
            return Result.error("参数错误");
        }
        return teamDepartmentService.sysDeleteDepartment(searchParam.getSearchId());
    }
}
