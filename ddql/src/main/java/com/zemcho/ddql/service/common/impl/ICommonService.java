package com.zemcho.ddql.service.common.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.other.OtherConfig;
import com.zemcho.ddql.config.sms.SmsConfig;
import com.zemcho.ddql.controller.common.param.SmsCodeParam;
import com.zemcho.ddql.controller.common.vo.*;
import com.zemcho.ddql.controller.team.param.TeamSearchParam;
import com.zemcho.ddql.controller.wechat.common.vo.TeamCommonVo;
import com.zemcho.ddql.entity.express.ExpressCompany;
import com.zemcho.ddql.entity.sys.Article;
import com.zemcho.ddql.entity.sys.Notice;
import com.zemcho.ddql.mapper.business.BusinessCircleMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.cas.CasAdminMapper;
import com.zemcho.ddql.mapper.cas.CasRoleMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.express.ExpressCompanyMapper;
import com.zemcho.ddql.mapper.sys.ArticleMapper;
import com.zemcho.ddql.mapper.sys.NoticeMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.service.common.CommonService;
import com.zemcho.ddql.util.CodeRedisUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import com.zemcho.ddql.util.sms.SmsUtil;
import com.zemcho.ddql.util.uuid.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: ICommonService
 * @Description:
 * @Date: 2025/5/12 19:03
 */
@Service
public class ICommonService implements CommonService {
    @Autowired
    CasRoleMapper casRoleMapper;

    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    CasUserMapper casUserMapper;

    @Autowired
    BusinessCircleMapper businessCircleMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private NoticeMapper noticesMapper;

    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OtherConfig otherConfig;

    /**
     * 获取角色下拉列表
     *
     * @param param
     * @param authAttrData
     * @return
     */
    @Override
    public Result getRoleLists(SearchParam param, AuthAttrData authAttrData, String token) {
        List<RoleCommonVO> list = casRoleMapper.selectCommonLists(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取管理员下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getAdminLists(SearchParam param) {
        List<AdminSelectVO> list = casAdminMapper.selectCommonLists(param);

        return Result.success("获取成功", list);
    }

    /**
     * 发送短信验证码
     *
     * @param param
     * @return
     */
    @Override
    public Result sendSmsCode(SmsCodeParam param) {
        // 先校验图形验证码
//        if (param.getUuid() == null || param.getCaptchaCode() == null) {
//            return Result.error("请输入图形验证码");
//        }
        // 获取图形验证码 检查是否过去
//        String captchaCode = redisTemplate.opsForValue().get(param.getUuid());
//        if (captchaCode == null) {
//            return Result.error("图形验证码已过期");
//        }
//        if (!captchaCode.equals(param.getCaptchaCode())) {
//            return Result.error("图形验证码错误");
//        }
        // 检验完成删除Redis缓存
//        redisTemplate.delete(param.getUuid());

        Integer type = param.getType();
        String phone = param.getPhone();

        String code = CodeRedisUtil.generateCode(4);
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put("code", code);
        String templateParam = JSONObject.toJSONString(templateMap);
        //发送验证码
        Result sendResult = SmsUtil.send(phone, SmsConfig.getCodeTemplateId(), templateParam);
        if (sendResult.getCode() == 10000) {
            //将验证码存入redis
            CodeRedisUtil.addCode(phone, type, code);
            return Result.success("验证码发送成功");
        } else {
            return Result.error("获取验证码失败");
        }
    }

    @Override
    public Result getCaptchaCode() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 150);
        // 获取验证码的标志
        String Code = lineCaptcha.getCode();
        // 获取base64编码
        String imageBase64 = lineCaptcha.getImageBase64();
        // 生产唯一的UUID
        String uuid = SeqUtil.getId();
        // 存入redis
        String key = "captcha:" + uuid;
        // 存入Redis设置过期时间5分钟
        redisTemplate.opsForValue().set(key, Code, 5, java.util.concurrent.TimeUnit.MINUTES);
        CaptchaCodeVo captchaCodeVo = new CaptchaCodeVo(uuid, imageBase64);
        return Result.success("获取成功", captchaCodeVo);
    }

    /**
     * 获取用户下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getUserLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<UserCommonVO> list = casUserMapper.selectCommonLists(param);
        PageInfo<UserCommonVO> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商圈下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getBusinessCircleLists(SearchParam param) {
        List<BusinessCircleCommonVo> list = businessCircleMapper.selectCommonList(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取团体下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getTeamLists(TeamSearchParam param) {
        List<TeamCommonVo> list = teamMapper.selectCommonList(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取店铺下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getShopLists(SearchParam param) {
        List<ShopCommonVo> list = shopMapper.selectCommonList(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取快递公司下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getExpressCompanyLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ExpressCompany> list = expressCompanyMapper.selectLists(param);
        PageInfo<ExpressCompany> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取用户端--文章列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getWechatArticleList(SearchParam param) {
        List<Article> list = articleMapper.selectWechatList();

        return Result.success("获取成功", list);
    }

    /**
     * 获取用户端--公告列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getWechatNoticeList(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        param.setSearchStatus(true);
        param.setSearchIntStatus(2);
        List<Notice> list = noticesMapper.selectList(param);
        PageInfo<Notice> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }
}
