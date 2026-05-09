package com.zemcho.guzhe.service.wechat.auth.impl;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.entity.sys.CertificationLog;
import com.zemcho.guzhe.mapper.sys.CertificationLogMapper;
import com.zemcho.guzhe.util.aliyun.dto.CertificationIdCardInforDto;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.auth.param.*;
import com.zemcho.guzhe.controller.wechat.auth.vo.UserWechatInfoVo;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.service.wechat.auth.WechatAuthService;
import com.zemcho.guzhe.util.CodeRedisUtil;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.aliyun.CertificationUtil;
import com.zemcho.guzhe.util.wechat.WechatUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import jakarta.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @title: IWechatAuthService
 * @Description:
 * @Date: 2025/7/4 10:21
 */
@Service
public class IWechatAuthService implements WechatAuthService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.profiles.active}")
    private String active;

    @Resource(name = "shiroRedisTemplate")
    RedisTemplate shiroRedisTemplate;

    @Autowired
    CasUserMapper casUserManager;

    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    CertificationLogMapper certificationLogMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 小程序登录
     *
     *
     * @param param
     * @return
     */
    @Override
    public Result login(WechatAuthParam param) {
        // 获取session数据 解密获得openId、unionid、session_key
        Map<String, String> sessionData = WechatUtil.getWechatSession(param.getCode());
        if (sessionData == null) {
            return Result.error("登录异常：code非法");
        }
        // 解密session_key获得用户个人信息
        Map<String, String> wechatUserInfo = WechatUtil.getUserInfo(sessionData.get("sessionKey"),
                param.getEncryptedData(), param.getIv());
        if (wechatUserInfo == null) {
            return Result.error("登录异常");
        }

        String openId = sessionData.get("openId");

        CasUser accountUser = casUserManager.selectByOpenId(openId);
        // 如果是新用户则创建用户
        if (accountUser == null) {
            Map<String, Integer> sexMap = new HashMap<>();
            sexMap.put("男", 1);
            sexMap.put("女", 2);

            LocalDateTime now = LocalDateTime.now();

            accountUser = new CasUser();
            accountUser.setNickname(wechatUserInfo.get("nickName"));
            accountUser.setAvatar(wechatUserInfo.get("avatarUrl"));
            accountUser.setSex(sexMap.getOrDefault(wechatUserInfo.get("gender"), 0));
            accountUser.setOpenId(openId);
            accountUser.setUnionId(sessionData.get("unionId"));
            accountUser.setAdminId(0);
            accountUser.setCreateTime(now);

            // 此处不做组件一键获取手机号
//            //第一次登录默认绑定微信手机号 获取手机号
//            String phone = WechatUtil.getWechatPhone(sessionData.get("sessionKey"), param.getEncryptedData(),
//                    param.getIv());
//            if (phone == null || phone.isEmpty()) {
//                return Result.error("登录异常，获取手机号失败");
//            }
//            CasUser existPhone = casUserManager.selectByPhone(phone);
//            if (existPhone == null) {
//                accountUser.setPhone(phone);
//            }

            casUserManager.insert(accountUser);
        }
        Integer userId = accountUser.getId();

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String token = JWTUtil.miniSign(userId, currentTimeMillis);
        shiroRedisTemplate.opsForValue().set(Constant.PREFIX_MINI_SHIRO_TOKEN + userId, token, 720, TimeUnit.MINUTES);

        logger.info("小程序登录成功:" + userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("phone", accountUser.getPhone());
        result.put("nickname", accountUser.getNickname());
        result.put("avatar", accountUser.getAvatar());
        result.put("sex", accountUser.getSex());
        result.put("token", token);
        result.put("sessionKey", sessionData.get("sessionKey"));

        return Result.success("登录成功", result);
    }

    /**
     * 小程序本地登录-开发用
     *
     * @return
     */
    @Override
    public Result testLogin(WechatTestLoginParam param) {
        if (!active.equals("local") && !active.equals("dev") && !active.equals("test")) {
            return Result.error("非法请求");
        }
        if (!param.getPwd().equals("Auth25!0708Test")) {
            return Result.error("非法请求!");
        }

        CasUser accountUser = casUserManager.selectByPhone(param.getPhone());
        if (accountUser == null) {
            return Result.error("请求失败");
        }
        Integer userId = accountUser.getId();

        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String token = JWTUtil.miniSign(userId, currentTimeMillis);
        shiroRedisTemplate.opsForValue().set(Constant.PREFIX_MINI_SHIRO_TOKEN + userId, token, 720, TimeUnit.MINUTES);

        logger.info("小程序本地登录成功:" + userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("phone", accountUser.getPhone());
        result.put("nickname", accountUser.getNickname());
        result.put("avatar", accountUser.getAvatar());
        result.put("sex", accountUser.getSex());
        result.put("token", token);

        return Result.success("登录成功", result);
    }

    /**
     * 微信一键绑定手机
     *
     * @return
     */
    @Override
    public Result bindWechatPhone(WechatAuthParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser accountUser = casUserManager.selectById(userId);
        if (accountUser == null) {
            return Result.error("用户不存在");
        }
        if (accountUser.getPhone() != null && !accountUser.getPhone().isEmpty()) {
            return Result.error("您已绑定手机，不需重新绑定");
        }

        Map<String, String> sessionData = WechatUtil.getWechatSession(param.getCode());
        if (sessionData == null) {
            return Result.error("绑定异常：code非法");
        }
        String phone = WechatUtil.getWechatPhone(sessionData.get("sessionKey"), param.getEncryptedData(),
                param.getIv());
        if (phone == null || phone.isEmpty()) {
            return Result.error("绑定异常，获取手机号失败");
        }

        CasUser existPhone = casUserManager.selectByPhone(phone);
        if (existPhone != null) {
            return Result.error("该手机号已绑定其他账号");
        }

        CasUser updateUser = new CasUser();
        updateUser.setId(accountUser.getId());
        updateUser.setPhone(phone);


        // 根据phone在管理员表中查询 如果存在就set进去
        CasAdmin casAdmin = casAdminMapper.selectByAccount(phone);
        if (casAdmin != null) {
            updateUser.setAdminId(casAdmin.getId());
        }

        casUserManager.update(updateUser);

        return Result.success("绑定成功");
    }

    /**
     * 验证码绑定手机
     *
     * @return
     */
    @Override
    public Result bindPhone(WechatBindPhoneParam param, String token) {
        // 根据token获取用户id
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser accountUser = casUserManager.selectById(userId);
        if (accountUser == null) {
            return Result.error("用户不存在");
        }
//        if (accountUser.getPhone() != null && !accountUser.getPhone().isEmpty()) {
//            return Result.error("您已绑定手机，不需重新绑定");
//        }

        String phone = param.getPhone();
        if (accountUser.getPhone() != null && phone.equals(accountUser.getPhone())) {
            return Result.error("无需更换");
        }

        if (!CodeRedisUtil.exist(phone, 1)) {
            return Result.error("验证码不存在或已过期");
        }
        String value = CodeRedisUtil.getCode(phone, 1);
        if (!param.getCode().equals(value)) {
            return Result.error("验证码错误，请重新输入");
        }
        CodeRedisUtil.removeCode(phone, 1);

        CasUser existPhone = casUserManager.selectByPhone(phone);
        if (existPhone != null) {
            return Result.error("该手机号已绑定其他账号");
        }

        CasUser updateUser = new CasUser();
        updateUser.setId(accountUser.getId());
        updateUser.setPhone(phone);

        // 根据phone在管理员表中查询 如果存在就set进去
        CasAdmin casAdmin = casAdminMapper.selectByAccount(phone);
        Integer adminId = 0;
        if (casAdmin != null) {
            adminId = casAdmin.getId();
        }
        updateUser.setAdminId(adminId);

        casUserManager.update(updateUser);

        return Result.success("绑定成功");
    }

    /**
     * 注销登录
     *
     * @return
     */
    @Override
    public Result logout(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        if (!shiroRedisTemplate.hasKey(Constant.PREFIX_MINI_SHIRO_TOKEN + userId)) {
            return new Result(10006, "token已过期");
        }

        shiroRedisTemplate.delete(Constant.PREFIX_MINI_SHIRO_TOKEN + userId);
        SecurityUtils.getSubject().logout();

        logger.info("小程序注销成功：" + userId);

        return Result.success("注销成功");
    }

    /**
     * 获取当前登录人信息
     *
     * @return
     */
    @Override
    public Result getUserInfo(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser accountUser = casUserManager.selectById(userId);
        if (accountUser == null) {
            return Result.error("用户不存在");
        }

        UserWechatInfoVo info = UserWechatInfoVo.getInstance(accountUser);

//        if (info.getAdminId() > 0) {
//            List<Integer> productIds = productCheckAdminMapper.selectProductIdByAdminId(info.getAdminId());
//            if (productIds != null && !productIds.isEmpty()) {
//                info.setCanCheckTicket(true);
//            }
//        }

        return Result.success("获取成功", info);
    }

    /**
     * 更新用户信息
     *
     * @return
     */
    @Override
    public Result updateUser(WechatUpdateUserParam param, String token) {
        Integer id = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (id == null) {
            return new Result(10006, "token无效");
        }
        CasUser accountUser = casUserManager.selectById(id);
        if (accountUser == null) {
            return Result.error("用户不存在");
        }

        CasUser updateUser = new CasUser();
        updateUser.setId(id);
        updateUser.setNickname(param.getNickname());
        updateUser.setAvatar(param.getAvatar());
        casUserManager.update(updateUser);

        return Result.success("操作成功");
    }

    /**
     * 用户实名认证
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCertification(CertificationParam param, String token) {
        Integer id = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (id == null) {
            return new Result(10006, "token无效");
        }
        CasUser userInfo = casUserManager.selectById(id);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getHasCertification() == 1) {
            return Result.error("您已实名认证，无需重复认证");
        }

        String cardNum = param.getCardNum();
        String name = param.getName();

        CertificationLog certificationLog = certificationLogMapper.selectByCardNum(cardNum);
        CertificationIdCardInforDto result;
        if (certificationLog != null) {
            result = JSON.parseObject(certificationLog.getResult(), CertificationIdCardInforDto.class);
        } else {
            result = CertificationUtil.check(cardNum, name);
        }
        if (result == null) {
            return Result.error("认证失败，请检查信息是否正确");
        }

        if (certificationLog == null) {
            certificationLog = new CertificationLog();
            certificationLog.setCardNum(cardNum);
            certificationLog.setName(name);
            certificationLog.setBirthDate(result.getBirthday());
            certificationLog.setArea(result.getArea());
            certificationLog.setResult(JSON.toJSONString(result));
            certificationLog.setCreateTime(LocalDateTime.now());
            certificationLogMapper.insert(certificationLog);
        }

        CasUser updateUser = new CasUser();
        updateUser.setId(id);
        updateUser.setHasCertification(1);
        updateUser.setCardNum(cardNum);
        updateUser.setName(name);
        updateUser.setBirthDate(result.getBirthday());
        updateUser.setArea(result.getArea());
        casUserManager.update(updateUser);

        return Result.success("操作成功");
    }

    @Override
    @Transactional
    public Result updateUserPhone(WechatBindPhoneParam param, String token) {
        // 解析 token 获取当前用户
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser accountUser = casUserManager.selectById(userId);
        if (accountUser == null) {
            return Result.error("用户不存在");
        }

        String phone = param.getPhone();

        // 验证码校验
        if (!CodeRedisUtil.exist(phone, 1)) {
            return Result.error("验证码不存在或已过期");
        }
        String value = CodeRedisUtil.getCode(phone, 1);
        if (!param.getCode().equals(value)) {
            return Result.error("验证码错误，请重新输入");
        }

        // 检查新手机号是否已存在
        CasUser existPhone = casUserManager.selectByPhone(phone);
        if (existPhone != null) {
            return Result.error("该手机号已绑定其他账号");
        }

        // 执行换绑更新
        CasUser updateUser = new CasUser();
        updateUser.setId(userId);
        updateUser.setPhone(phone);
        casUserManager.update(updateUser);

        // 换绑成功后清除验证码缓存
        CodeRedisUtil.removeCode(phone, 1);

        return Result.success("手机号换绑成功");
    }
}
