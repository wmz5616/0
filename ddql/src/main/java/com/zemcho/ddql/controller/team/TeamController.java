package com.zemcho.ddql.controller.team;

import com.zemcho.ddql.aspect.log.Log;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.param.TeamVerificationParam;
import com.zemcho.ddql.controller.team.vo.TeamListVo;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import com.zemcho.ddql.service.team.TeamService;
import com.zemcho.ddql.service.team.TeamUserService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamDepartmentService teamDepartmentService;

    @Autowired
    private TeamUserService teamUserService;

    /**
     * 新增团体
     *
     * @param team
     * @param result
     * @return
     */
    @RequestMapping("/add")
    @Log(description = "新增团体", module = "团体管理")
    public Result addTeam(@Valid @RequestBody Team team, BindingResult result,
                          @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.addTeam(team, token, true);
    }

    /**
     * 修改团体
     *
     * @param team   id 必传
     * @param result
     * @return
     */
    @RequestMapping("/update")
    @Log(description = "修改团体", module = "团体管理")
    public Result updateTeam(@Valid @RequestBody Team team, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.updateTeam(team);
    }

    /**
     * 编辑团队状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑团体状态", module = "团体管理")
    @RequestMapping("/status/set")
    public Result setStatus(@Validated @RequestBody ChangeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return teamService.setStatus(param);
    }

    /**
     * 删除团体
     *
     * @param team   id必传
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/delete")
    @Log(description = "删除团体", module = "团体管理")
    public Result deleteTeam(@Valid @RequestBody Team team, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.deleteTeam(team.getId(), token, true);
    }

    /**
     * 查询团队列表
     *
     * @param param        name type isVerified status region createBeginTime createEndTime deleteBeginTime
     *                     deleteEndTime pageNum pageSize
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/select")
    public Result selectTeamList(@Valid @RequestBody TeamSearchParam param, BindingResult result,
                                 @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.selectTeamList(param);
    }

    /**
     * 导出团队列表
     *
     * @param param    id集合
     * @param response
     */
    @PostMapping("/export")
    public void export(@Valid @RequestBody TeamSearchParam param, HttpServletResponse response) {
        List<TeamListVo> list = teamService.selectTeamListToExport(param);
        ExcelUtil.exportToWeb(response, list, "团体信息", "团体信息", TeamListVo.class);
    }

    /**
     * 统计团队充值订单数据
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/recharge/order/count")
    public Result teamRechargeOrderCount(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.teamRechargeOrderCount(param);
    }

    /**
     * 更新团体中的用户信息
     *
     * @param teamUser teamId userId 必传 可修改userName userPhone departmentId
     * @param result
     * @return
     */
    @RequestMapping("/updateTeamUser")
    @Log(description = "更新团体用户", module = "团体管理")
    public Result updateTeamUser(@Valid @RequestBody TeamUser teamUser, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.updateTeamUser(teamUser);
    }

    /**
     * 编辑团体用户状态
     *
     * @param param
     * @param result
     * @return
     */
    @Log(description = "编辑团体用户状态", module = "团体管理")
    @RequestMapping("/user/status/set")
    public Result setUserStatus(@Validated @RequestBody ChangeParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return teamUserService.setUserStatus(param);
    }

    /**
     * 删除团体用户
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/delete")
    @Log(description = "删除团体用户", module = "团体管理")
    public Result deleteTeamUser(@Valid @RequestBody DeleteParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.deleteTeamUserByAdmin(param);
    }

    /**
     * 新增团体用户
     *
     * @param teamUser
     * @param result
     * @return
     */
    @RequestMapping("/user/add")
    @Log(description = "新增团体用户", module = "团体管理")
    public Result addTeamUser(@Valid @RequestBody TeamUser teamUser, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.addTeamUser(teamUser);
    }

    /**
     * 查询团体下用户信息
     *
     * @param param        teamId必传 userName userPhone type status
     * @param result
     * @param authAttrData
     * @return
     */
    @RequestMapping("/selectTeamUser")
    public Result selectTeamUserList(@Valid @RequestBody TeamUserSearchParam param, BindingResult result,
                                     @RequestAttribute(Constant.REQUEST_ATTR_DATA) AuthAttrData authAttrData) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.selectTeamUserList(param);
    }

    /**
     * 查询团体资质审核记录
     *
     * @param param  teamId（searchId) status(searchIntStatus)
     * @param result
     * @return
     */
    @RequestMapping("/selectVerificationRecord")
    public Result selectVerificationRecord(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.selectVerificationRecord(param);
    }

    /**
     * 新增团体资质认证
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/addVerificationRecord")
    @Log(description = "新增团体资质认证", module = "团体管理")
    public Result addVerificationRecord(@Valid @RequestBody TeamVerificationParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.addVerificationRecord(param, token, true);
    }

    /**
     * 编辑团体资质认证
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/updateVerificationRecord")
    @Log(description = "编辑团体资质认证", module = "团体管理")
    public Result updateVerificationRecord(@Valid @RequestBody TeamVerificationParam param, BindingResult result,
                                           @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.updateVerificationRecord(param, token, true);
    }

    /**
     * 人工审核团体验证记录
     *
     * @param param  传id 和 status
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/auditVerificationRecord")
    @Log(description = "人工审核团体资质审核记录", module = "团体管理")
    public Result auditVerificationRecord(@Valid @RequestBody TeamVerificationParam param, BindingResult result,
                                          @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.auditVerificationRecord(param, token);
    }


    @RequestMapping("/get/dept")
    public Result getDepartment(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamDepartmentService.getDepartmentList(param.getSearchId());
    }
}
