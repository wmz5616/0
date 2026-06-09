package com.zemcho.ddql.service.team.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.other.OtherConfig;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.controller.team.param.TeamVerificationParam;
import com.zemcho.ddql.controller.team.vo.TeamListVo;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderCountVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.team.*;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.order.RechargeOrderMapper;
import com.zemcho.ddql.mapper.team.*;
import com.zemcho.ddql.service.personalCenter.MessageAnnouncementService;
import com.zemcho.ddql.service.team.TeamDepartmentService;
import com.zemcho.ddql.service.team.TeamService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.decode.Md5Util;
import com.zemcho.ddql.util.wechat.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ITeamService implements TeamService {

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Autowired
    private TeamVerificationMapper teamVerificationMapper;

    @Autowired
    private TeamCheckInSettingsMapper teamCheckInSettingsMapper;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private MessageAnnouncementService messageAnnouncementService;

    @Autowired
    private TeamDepartmentMapper teamDepartmentMapper;

    @Autowired
    OtherConfig otherConfig;

    /**
     * 文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 上传文件存储在本地的路径
     */
    @Value("${file.upload-path}")
    private String uploadFilePath;

    /**
     * 新增团体
     *
     * @param team
     * @param token
     * @param isAdmin
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addTeam(Team team, String token, Boolean isAdmin) {
        // 先检查参数
        if (team.getName() == null || team.getType() == null || team.getRegion() == null
                || team.getContactPerson() == null || team.getContactPhone() == null) {
            return Result.error("请填写完整信息");
        }

        if (team.getIsUserAuth() == null) {
            return Result.error("请选择进团是否审核");
        }

        if (team.getCheckInNumLimit() == null) {
            team.setCheckInNumLimit(0);
        }

        // 多部门管理默认关闭
        if (team.getIsMultiDepartment() == null) {
            team.setIsMultiDepartment(0);
        }

        Integer userId = 0;
        CasUser user = null;
        if (!isAdmin) {
            // 这里是小程序创建团队
            // 解析token获得管理员id
            userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            user = casUserMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
        }

        // 设置初值
        team.setPeopleNumber(1);
        team.setStatus(0);
        team.setHealthyCoin(0);
        team.setIsVerified(0);
        team.setCreateTime(LocalDateTime.now());
        // 插入团队表
        teamMapper.insert(team);
        Integer teamId = team.getId();

        // 获取团队id
        if (!isAdmin) {
            TeamUser teamUser = new TeamUser();
            teamUser.setTeamId(teamId);
            teamUser.setUserId(userId);
            teamUser.setType(0);
            teamUser.setUserName(user.getNickname());
            teamUser.setUserPhone(user.getPhone());
            teamUser.setHealthyCoin(0);
            teamUser.setJoinType(0);
            teamUser.setStatus(0);
            teamUser.setCreateTime(LocalDateTime.now());
            teamUserMapper.insert(teamUser);
        }

        // 插入团队提现设置
        TeamCheckInSettings teamCheckInSettings = new TeamCheckInSettings();
        teamCheckInSettings.setTeamId(teamId);
        teamCheckInSettings.setScanCodeTime(90);
        teamCheckInSettings.setScanCodeHealthyCoin(0);
        teamCheckInSettings.setStepsOpen(1);
        teamCheckInSettings.setTargetSteps(10000);
        teamCheckInSettings.setStepsHealthyCoin(0);
        teamCheckInSettings.setLowestWithdrawalMoney(0);
        teamCheckInSettingsMapper.insert(teamCheckInSettings);

        // 如果开启了多部门管理，初始化默认部门
        if (team.getIsMultiDepartment() == 1) {
            initDefaultDepartment(teamId);
        }

        return Result.success("操作成功", teamId);
    }

    @Override
    public Result updateTeam(Team team) {
        // 检查团队是否存在
        Team existTeam = teamMapper.selectById(team.getId());
        if (existTeam == null) {
            return Result.error("团队不存在");
        }

        Team existNameTeam = teamMapper.selectByNameLimit1(team.getName());
        if (existNameTeam != null && !existNameTeam.getId().equals(team.getId())) {
            return Result.error("团队名称已存在");
        }

        if (team.getIsMultiDepartment() != null && !team.getIsMultiDepartment().equals(existTeam.getIsMultiDepartment())) {
            if (team.getIsMultiDepartment() == 1) {
                initDefaultDepartment(team.getId());
            } else {
                teamDepartmentMapper.deleteByTeamId(team.getId());
                teamUserMapper.clearDepartmentIdByTeamId(team.getId());
            }
        }

        // 部分数据不允许修改
        team.setHealthyCoin(null);
        team.setIsVerified(null);
        teamMapper.update(team);
        return Result.success("操作成功");
    }

    @Override
    public Result setStatus(ChangeParam param) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        teamMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    @Override
    public Result updateTeam(Team team, String token) {
        // 检查团队是否存在
        Team existTeam = teamMapper.selectById(team.getId());
        if (existTeam == null) {
            return Result.error("团队不存在");
        }

        Team existNameTeam = teamMapper.selectByNameLimit1(team.getName());
        if (existNameTeam != null && !existNameTeam.getId().equals(team.getId())) {
            return Result.error("团队名称已存在");
        }
        // 获取当前操作用户信息
        Integer managerId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        // 根据teamId 和 manageId 获取团队用户信息
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(team.getId(), managerId, true);
        if (teamUser == null || (teamUser.getType() != 0 && teamUser.getType() != 1)) {
            return Result.error("您没有权限修改此团队信息");
        }

        if (team.getIsMultiDepartment() != null && !team.getIsMultiDepartment().equals(existTeam.getIsMultiDepartment())) {
            if (team.getIsMultiDepartment() == 1) {
                initDefaultDepartment(team.getId());
            } else {
                teamDepartmentMapper.deleteByTeamId(team.getId());
                teamUserMapper.clearDepartmentIdByTeamId(team.getId());
            }
        }

        // 部分数据不允许修改
        team.setHealthyCoin(null);
        team.setIsVerified(null);
        teamMapper.update(team);
        return Result.success("操作成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteTeam(Integer deleteId, String token, Boolean isAdmin) {
        if (deleteId == null) {
            return Result.error("参数异常");
        }

        if (!isAdmin) {
            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            if (userId == null) {
                return new Result(10006, "token无效");
            }

            TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(deleteId, userId, true);
            if (teamUser == null || (teamUser.getType() != 0 && teamUser.getType() != 1)) {
                return Result.error("您没有权限删除此团队信息");
            }
        }

        // 检查是否能删除
        Team team = teamMapper.selectById(deleteId);
        if (team == null) {
            return Result.error("团队不存在");
        }
        if (team.getHealthyCoin() != 0) {
            return Result.error("团队健康币不为0，请先清空健康币");
        }
        if (teamUserMapper.ifExistHealthyCoin(deleteId) > 0) {
            return Result.error("团队成员健康币不为0，请先清空健康币");
        }

        teamMapper.delete(deleteId, LocalDateTime.now());
        // 删除关联
        teamUserMapper.deleteByTeamId(deleteId);

        return Result.success("删除成功");
    }

    @Override
    public Result selectTeamList(TeamSearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<TeamListVo> list = teamMapper.selectList(param);
        PageInfo<TeamListVo> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public List<TeamListVo> selectTeamListToExport(TeamSearchParam param) {
        return teamMapper.selectList(param);
    }

    /**
     * 统计团队充值订单数据
     *
     * @param param
     * @return
     */
    @Override
    public Result teamRechargeOrderCount(SearchParam param) {
        Integer teamId = param.getSearchId();
        if (teamId == null) {
            return Result.error("参数异常");
        }

        Team teamInfo = teamMapper.selectById(teamId);
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }

        SearchParam orderCountParam = new SearchParam();
        orderCountParam.setSearchField2(teamId);
        orderCountParam.setSearchStatusList(Arrays.asList(2, 4));
        RechargeOrderCountVo rechargeOrderCountVo = rechargeOrderMapper.selectCount(orderCountParam);

        Map<String, Object> result = new HashMap<>();
        result.put("teamHealthyCoin", teamInfo.getHealthyCoin());
        result.put("rechargeOrderCountData", rechargeOrderCountVo);

        return Result.success("获取成功", result);
    }

    // 团队的资质验证申请
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addVerificationRecord(TeamVerificationParam param, String token, Boolean isAdmin) {
        Integer status = 1;
        Integer isVerified = 1;
        if (!isAdmin) {
            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            if (userId == null) {
                return new Result(10006, "token无效");
            }

            TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(param.getTeamId(), userId, true);
            if (teamUser == null || (teamUser.getType() != 0 && teamUser.getType() != 1)) {
                return Result.error("您没有权限修改此团队信息");
            }

            status = 0;
            isVerified = 0;
        }

        // 检查是否已有团队资质验证申请
        if (teamVerificationMapper.ifExistByTeamId(param.getTeamId())) {
            return Result.error("已有团队资质验证申请");
        }
        // 检查参数
        Team team = teamMapper.selectById(param.getTeamId());
        if (team == null) {
            return Result.error("团队不存在");
        }

        // 将List转为String以；分隔
        String licenseImage = param.getLicenseImageList() != null ? String.join(";", param.getLicenseImageList()) : "";
        String additionPicture = param.getAdditionPictureList() != null ? String.join(";", param.getAdditionPictureList()) : "";

        TeamVerification verification = new TeamVerification();
        verification.setTeamId(team.getId());
        verification.setLicenseType(param.getLicenseType());
        verification.setLicenseImage(licenseImage);
        verification.setAdditionPicture(additionPicture);
        verification.setVerificationType(team.getType());
        verification.setType(param.getType());
        verification.setContactPhone(param.getContactPhone());
        verification.setContactEmail(param.getContactEmail());
        verification.setStatus(status);
        verification.setCreateTime(LocalDateTime.now());
        teamVerificationMapper.insert(verification);

        // 更改团队状态
        Team teamUpdate = new Team();
        teamUpdate.setId(team.getId());
        teamUpdate.setIsVerified(isVerified);
        teamMapper.update(teamUpdate);

        return Result.success("提交成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateVerificationRecord(TeamVerificationParam param, String token, Boolean isAdmin) {
        Integer status = 1;
        Integer isVerified = 1;
        if (!isAdmin) {
            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            if (userId == null) {
                return new Result(10006, "token无效");
            }

            TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(param.getTeamId(), userId, true);
            if (teamUser == null || (teamUser.getType() != 0 && teamUser.getType() != 1)) {
                return Result.error("您没有权限修改此团队信息");
            }

            status = 0;
            isVerified = 0;
        }

        TeamVerification verification = teamVerificationMapper.selectById(param.getId());
        if (verification == null) {
            return Result.error("该申请不存在");
        }
        if (!isAdmin && verification.getStatus() == 0) {
            return Result.error("该申请正在审核中，不可编辑");
        }
        verification.setLicenseType(param.getLicenseType());
        // 将List转为String以；分隔
        String licenseImage = param.getLicenseImageList() != null ? String.join(";", param.getLicenseImageList()) : "";
        String additionPicture = param.getAdditionPictureList() != null ? String.join(";", param.getAdditionPictureList()) : "";
        verification.setLicenseImage(licenseImage);
        verification.setAdditionPicture(additionPicture);
        Team team = teamMapper.selectById(verification.getTeamId());
        verification.setVerificationType(team != null ? team.getType() : param.getVerificationType());
        verification.setType(param.getType());
        verification.setContactPhone(param.getContactPhone());
        verification.setContactEmail(param.getContactEmail());
        verification.setStatus(status);
        verification.setCreateTime(LocalDateTime.now());
        verification.setUpdateTime(LocalDateTime.now());
        teamVerificationMapper.update(verification);

        // 更改团队状态
        Team teamUpdate = new Team();
        teamUpdate.setId(verification.getTeamId());
        teamUpdate.setIsVerified(isVerified);
        teamMapper.update(teamUpdate);

        return Result.success("操作成功");
    }

    @Override
    public Result selectVerificationRecord(SearchParam param) {
        Integer teamId = param.getSearchId();
        if (teamId == null) {
            return Result.error("参数异常");
        }

        TeamVerification record = teamVerificationMapper.selectByTeamId(teamId);

        return Result.success("获取成功", record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result auditVerificationRecord(TeamVerificationParam param, String token) {
        Integer status = param.getStatus();
        if (status == null || (status != 1 && status != 2)) {
            return Result.error("请选择正确的审核状态");
        }
        // 查出对应的申请记录
        TeamVerification verification = teamVerificationMapper.selectById(param.getId());
        if (verification == null) {
            return Result.error("申请记录不存在");
        }
        if (verification.getStatus() != 0) {
            return Result.error("该申请状态不可审核");
        }
        // 根据teamId查出用户信息
        Team teamInfo = teamMapper.selectById(verification.getTeamId());
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }
        // 更改审核记录
        verification.setStatus(status);
        teamVerificationMapper.update(verification);
        // 审核通过
        if (status == 1) {
            // 更改团队状态
            teamInfo.setIsVerified(1);
            teamMapper.update(teamInfo);
        }
        // 审核结果通知用户 发送消息公告
        List<Integer> userIds = teamUserMapper.selectTeamCreateUserAndManagerIds(teamInfo.getId());
        String title = teamInfo.getName() + "团队资质验证结果通知";
        String content = status == 1 ? teamInfo.getName() + "团队资质验证通过" : teamInfo.getName() + "团队资质验证被驳回";
        for (Integer userId : userIds) {
            messageAnnouncementService.insert(userId, title, content);
        }

        return Result.success("操作成功");
    }

    /**
     * 团队邀请小程序码
     *
     * @param param
     * @return
     */
    @Override
    public Result teamUserInvitationQrCode(SearchParam param) {
        Integer teamId = param.getSearchId();
        if (teamId == null) {
            return Result.error("参数异常");
        }

        Team teamInfo = teamMapper.selectById(teamId);
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }

        //判断小程序码是否已生成，未生成则生成对应的小程序码
        String scene = teamId.toString();
        String fileName = scene + "_" + Md5Util.MD5(scene) + ".png";
        String filePath = localFilePath + uploadFilePath + "/team_invitation_code/";
        WechatUtil.generateAndSaveQrCode(scene, otherConfig.getTeamInvitationQRPage(), 430,
                otherConfig.getAppCheckInQREnv(), filePath, fileName);

        Map<String, Object> result = new HashMap<>();
        result.put("filePath", uploadFilePath + "/team_invitation_code/" + fileName);

        return Result.success("获取成功", result);
    }

    /**
     * 获取团队详情
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result getWechatTeamInfo(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数错误");
        }

//        TeamUser checkTeamUser = teamUserMapper.selectByTeamIdAndUserId(id, userId);
//        if (checkTeamUser == null) {
//            return Result.error("您无权查看该团体信息");
//        }

        Team teamInfo = teamMapper.selectById(id);

        return Result.success("获取成功", teamInfo);
    }

    @Override
    public void initDefaultDepartment(Integer teamId) {
        // 检查是否已存在部门
        int count = teamDepartmentMapper.countByTeamId(teamId);
        if (count > 0) {
            return;
        }

        // 创建默认部门
        TeamDepartment defaultDept = new TeamDepartment();
        defaultDept.setTeamId(teamId);
        defaultDept.setName("默认部门");
        defaultDept.setSort(0);
        defaultDept.setStatus(0);
        teamDepartmentMapper.insert(defaultDept);

        log.info("团队[{}]初始化默认部门完成", teamId);
    }

    /**
     * 团队是否是多部门 0是 1否
     *
     * @param param
     * @return
     */
    @Override
    public Result isMultiDepartment(SearchParam param) {
        Integer teamId = param.getSearchId();
        if (teamId == null) {
            return Result.error("参数错误");
        }
        Integer multiDepartment = teamDepartmentMapper.ifMultiDepartment(teamId);
        if (multiDepartment == null) {
            return Result.error("团队不存在");
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("isMultiDepartment", multiDepartment);
        return Result.success("获取成功", result);
    }
}
