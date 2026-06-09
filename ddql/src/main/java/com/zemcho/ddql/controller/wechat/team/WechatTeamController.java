package com.zemcho.ddql.controller.wechat.team;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.param.TeamVerificationParam;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamApplicationRecord;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.service.team.TeamService;
import com.zemcho.ddql.service.team.TeamUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/wechat/team")
public class WechatTeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamUserService teamUserService;

    /**
     * 创建团体
     *
     * @param team   name type region address  contactPerson contactPhone contactEmail
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/add")
    public Result addTeam(@Valid @RequestBody Team team, BindingResult result,
                          @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return teamService.addTeam(team, token, false);
    }

    /**
     * 修改团体
     *
     * @param team   id 必传 团队的id
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/update")
    public Result updateTeam(@Valid @RequestBody Team team, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.updateTeam(team, token);
    }

    /**
     * 删除团队
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/delete")
    public Result deleteTeam(@Valid @RequestBody DeleteOneParam param, BindingResult result,
                             @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.deleteTeam(param.getDeleteId(), token, false);
    }

    /**
     * 获取团队详情
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/info")
    public Result getTeamInfo(@RequestBody @Valid SearchParam param, BindingResult result,
                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.getWechatTeamInfo(param, token);
    }

    /**
     * 查询用户所属团队
     *
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/selectUserTeams")
    public Result selectUserTeams(@RequestBody @Valid SearchParam param, BindingResult result,
                                  @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.selectUserTeams(param, token);
    }

    /**
     * 添加团体认证记录
     *
     * @param param  teamId必传 licenseType licenseImageList additionPictureList verificationType type contactPhone
     *               contactEmail
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/addVerificationRecord")
    public Result addVerificationRecord(@Valid @RequestBody TeamVerificationParam param, BindingResult result,
                                        @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.addVerificationRecord(param, token, false);
    }

    /**
     * 更新团体验证记录
     *
     * @param param  id必传
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/updateVerificationRecord")
    public Result updateVerificationRecord(@Valid @RequestBody TeamVerificationParam param, BindingResult result,
                                           @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.updateVerificationRecord(param, token, false);
    }

    /**
     * 查询团体验证记录
     *
     * @param param  teamId（searchId)
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
     * 团队邀请小程序码
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/invitation/qr/code")
    public Result teamUserInvitationQrCode(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamService.teamUserInvitationQrCode(param);
    }


    /**
     * 删除团队成员
     *
     * @param teamUser teamId 和 userId
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/deleteTeamUser")
    public Result deleteTeamUser(@Valid @RequestBody TeamUser teamUser, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.deleteTeamUser(teamUser.getTeamId(), teamUser.getUserId(), token);
    }

    /**
     * 修改团队成员信息
     *
     * @param teamUser
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/user/update")
    public Result updateTeamUser(@Valid @RequestBody TeamUser teamUser, BindingResult result,
                                 @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.wechatUpdateTeamUser(teamUser, token);
    }

    /**
     * 查询团队/部门成员列表
     *
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/user/list")
    public Result selectTeamUserList(@Valid @RequestBody TeamUserSearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        if (param.getTeamId() == null) {
            return Result.error("参数异常");
        }
        return teamUserService.selectTeamUserAndDepartmentList(param);
    }

    /**
     * 新增加入团队审核的记录
     *
     * @param teamApplication userId 和 teamId 必传 userName userPhone
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/saveTeamApplicationRecord")
    public Result saveTeamApplicationRecord(@Valid @RequestBody TeamApplicationRecord teamApplication,
                                            BindingResult result,
                                            @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.saveTeamApplicationRecord(teamApplication);
    }

    /**
     * 查询团队申请记录
     *
     * @param param
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/selectTeamApplicationRecord")
    public Result selectTeamApplicationRecord(@Valid @RequestBody SearchParam param, BindingResult result,
                                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.selectTeamApplicationRecord(param, token);
    }

    /**
     * 审核团体验证记录
     *
     * @param teamApplication id 和 status 必传，departmentId 可选（修改申请部门）
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/updateTeamApplicationRecord")
    public Result updateTeamApplicationRecord(@Valid @RequestBody TeamApplicationRecord teamApplication,
                                              BindingResult result,
                                              @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.updateTeamApplicationRecord(teamApplication, token);
    }

    /**
     * 修改团队用户类型
     *
     * @param teamUser 传teamId userId 和 要将其修改的类型 type
     * @param result
     * @param token
     * @return
     */
    @RequestMapping("/updateTeamUserType")
    public Result updateTeamUserType(@Valid @RequestBody TeamUser teamUser, BindingResult result,
                                     @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return teamUserService.updateTeamUserType(teamUser, token);
    }

    /**
     * 团队是否多部门
     * @param param
     * @param result
     * @return
     */
    @RequestMapping("/isMultiDepartment")
    public Result isMultiDepartment(@Valid @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
        return teamService.isMultiDepartment(param);
    }


}
