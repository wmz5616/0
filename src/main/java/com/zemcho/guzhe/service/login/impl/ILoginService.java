package com.zemcho.guzhe.service.login.impl;

import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.controller.cas.param.AdminPhoneParam;
import com.zemcho.guzhe.controller.cas.vo.RuleTreeVO;
import com.zemcho.guzhe.controller.login.param.LoginParam;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.login.param.PwdResetParam;
import com.zemcho.guzhe.controller.login.vo.AdminInfoVO;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.log.LoginLog;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.cas.CasRoleRuleMapper;
import com.zemcho.guzhe.mapper.cas.CasRuleMapper;
import com.zemcho.guzhe.mapper.log.LogMapper;
import com.zemcho.guzhe.service.login.LoginService;
import com.zemcho.guzhe.util.CodeRedisUtil;
import com.zemcho.guzhe.util.CommonUtils;
import com.zemcho.guzhe.util.decode.AesEncode;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.decode.HashServiceUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class ILoginService implements LoginService {
    @Resource(name = "shiroRedisTemplate")
    RedisTemplate shiroRedisTemplate;

    @Resource(name = "redisTemplate")
    RedisTemplate redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    LogMapper logMapper;

    @Autowired
    CasRoleRuleMapper casRoleRuleMapper;

    @Autowired
    CasRuleMapper casRuleMapper;

    @Autowired
    OtherConfig otherConfig;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 登录
     *
     * @return
     */
    @Override
    public Result login(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();

        Result checkResult = accountLoginCheck(account, password);
        if (!checkResult.success()) {
            return checkResult;
        }

        //从checkResult获取用户信息
        CasAdmin adminInfo = (CasAdmin) checkResult.getData();
        AuthJwtData authJwtData = new AuthJwtData();
        authJwtData.setAdminId(adminInfo.getId());
        authJwtData.setAccount(account);
        authJwtData.setName(adminInfo.getName());

        if (!otherConfig.getSuperAdminId().equals(adminInfo.getId())) {
            Map<String, Object> permissionData =
                    redisUtil.hashEntries(Constant.ADMIN_PERMISSION_DATA_PREFIX + authJwtData.getAdminId());
            if (permissionData == null) {
                return Result.error("无权限访问，请联系管理员");
            }
            List<Integer> roleIds = (List<Integer>) permissionData.get("roleIds");

            if (roleIds == null || roleIds.isEmpty()) {
                return Result.error("无权限访问，请联系管理员");
            }
        }

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String token = JWTUtil.sign(authJwtData, currentTimeMillis);
        shiroRedisTemplate.opsForValue().set(Constant.PREFIX_SHIRO_TOKEN + account, token, 120, TimeUnit.MINUTES);

        LoginLog log = new LoginLog();
        log.setAdminId(authJwtData.getAdminId());
        log.setAccount(account);
        log.setName(authJwtData.getName());
        log.setLoginTime(LocalDateTime.now());
        log.setIp(CommonUtils.getUserIP(loginParam.getRequest()));
        logMapper.insertLoginLog(log);

        logger.info(account + "登录成功");

        return Result.success("登录成功", token);
    }

    //修改密码
    @Override
    public Result resetPwd(PwdResetParam param, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }
        Integer adminId = authJwtData.getAdminId();
        String userName = authJwtData.getName();
        String userAccount = authJwtData.getAccount();

        String oldPassword = param.getOldPassword();
        String newPassword = param.getNewPassword();
        String confirmPassword = param.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            return Result.error("新密码与确认密码不一致");
        }

        Result checkResult = accountLoginCheck(userAccount, oldPassword);
        if (!checkResult.success()) {
            return checkResult;
        }

        newPassword = HashServiceUtil.computeHash(newPassword);
        CasAdmin newAdminData = new CasAdmin();
        newAdminData.setId(adminId);
        newAdminData.setPassword(newPassword);
        casAdminMapper.update(newAdminData);

        logger.info(userName + "修改密码成功");

        return Result.success("修改密码成功");
    }

    /**
     * 检验登陆信息
     *
     * @return
     */
    public Result accountLoginCheck(String account, String password) {
        password = AesEncode.DESDecrypt(password);
        if (password.equals("")) {
            return new Result(10005, "参数解密失败");
        }

        int accountInterval = 30;
        password = HashServiceUtil.computeHash(password);

        //检验账号信息
        CasAdmin adminInfo = casAdminMapper.selectByAccount(account);
        if (adminInfo == null || adminInfo.getStatus() == 0) {
            return new Result(10005, "账号错误或禁用");
        }

        int errorCount = 0;
        if (redisTemplate.opsForValue().get(Constant.LOGIN_ERROR_PREFIX + account) != null) {
            errorCount = (int) redisTemplate.opsForValue().get(Constant.LOGIN_ERROR_PREFIX + account);
        }
        if (errorCount >= 3) {
            return new Result(10005, "密码已错误" + errorCount + "次，账号已冻结");
        }
        if (!password.equals(adminInfo.getPassword())) {
            errorCount += 1;
            redisTemplate.opsForValue().set(Constant.LOGIN_ERROR_PREFIX + account, errorCount, accountInterval,
                    TimeUnit.MINUTES);
            if (errorCount >= 3) {
                return new Result(10005, "密码已错误" + errorCount + "次，账号已冻结");
            }
            return new Result(10005, "密码已错误" + errorCount + "次，" + (3 - errorCount) + "次后账号将冻结");
        }

        redisTemplate.delete(Constant.LOGIN_ERROR_PREFIX + account);

        //返回用户信息
        return Result.success("验证通过", adminInfo);
    }

    /**
     * 注销登录
     *
     * @return
     */
    @Override
    public Result logout(String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }
        String account = authJwtData.getAccount();

        if (!shiroRedisTemplate.hasKey(Constant.PREFIX_SHIRO_TOKEN + account)) {
            return new Result(10006, "token已过期");
        }
        shiroRedisTemplate.delete(Constant.PREFIX_SHIRO_TOKEN + account);

        SecurityUtils.getSubject().logout();

        logger.info(account + "注销成功");

        return Result.success("用户注销成功");
    }

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @Override
    public Result getLoginInfo(String token, AuthAttrData authAttrData) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }

        //获取用户信息
        CasAdmin adminInfo = casAdminMapper.selectByAccount(authJwtData.getAccount());
        if (adminInfo == null) {
            return Result.error("用户不存在");
        }

        AdminInfoVO vo = AdminInfoVO.getInstance(adminInfo);
        vo.setRoleIds(authAttrData.getRoleIds());

        List<Integer> ruleIds = new ArrayList<>();
        if (otherConfig.getSuperAdminId().equals(authJwtData.getAdminId())) {
            SearchParam ruleParam = new SearchParam();
            List<RuleTreeVO> ruleList = casRuleMapper.selectLists(ruleParam);
            ruleIds = ruleList.stream().map(RuleTreeVO::getId).collect(Collectors.toList());
        } else {
            ruleIds = casRoleRuleMapper.selectRuleIdByRoleIds(authAttrData.getRoleIds());
        }
        vo.setRuleIds(ruleIds);

        return Result.success("获取成功", vo);
    }

    @Override
    public Result forgetPassword(AdminPhoneParam param) {
        String phone = param.getPhone();
        // 根据手机号查出管理员信息
        CasAdmin adminInfo = casAdminMapper.selectByAccount(phone);
        if (adminInfo == null) {
            return Result.error("手机号不存在");
        }
        // 校验两个密码是否一致
        String newPassWord = HashServiceUtil.computeHash(param.getNewPassword());
        String confirmPassWord = HashServiceUtil.computeHash(param.getConfirmPassword());
        if (!newPassWord.equals(confirmPassWord)) {
            return Result.error("密码不一致");
        }
        // 校验code
        if (!CodeRedisUtil.exist(phone, 2)) {
            return Result.error("验证码不存在或已过期");
        }
        String value = CodeRedisUtil.getCode(phone, 2);
        if (!param.getCode().equals(value)) {
            return Result.error("验证码错误，请重新输入");
        }
        CodeRedisUtil.removeCode(phone, 2);
        // 更新管理员密码
        adminInfo.setPassword(HashServiceUtil.computeHash(param.getNewPassword()));
        casAdminMapper.update(adminInfo);
        return Result.success("修改成功");
    }
}
