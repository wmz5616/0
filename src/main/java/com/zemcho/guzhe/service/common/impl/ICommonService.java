package com.zemcho.guzhe.service.common.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthAttrData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.config.sms.SmsConfig;
import com.zemcho.guzhe.controller.common.param.SmsCodeParam;
import com.zemcho.guzhe.controller.common.vo.*;
import com.zemcho.guzhe.entity.express.ExpressCompany;
import com.zemcho.guzhe.entity.shop.IndustryCategory;
import com.zemcho.guzhe.entity.sys.Region;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.cas.CasRoleMapper;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.express.ExpressCompanyMapper;
import com.zemcho.guzhe.mapper.shop.IndustryCategoryMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.supermarket.SupermarketMapper;
import com.zemcho.guzhe.mapper.sys.RegionMapper;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.util.CodeRedisUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import com.zemcho.guzhe.util.sms.SmsUtil;
import com.zemcho.guzhe.util.uuid.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
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
    private ExpressCompanyMapper expressCompanyMapper;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

    @Autowired
    private SupermarketMapper supermarketMapper;

    @Autowired
    private ShopMapper shopMapper;

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
     * 根据当前地区id查询下级地区
     *
     * @param param
     * @return
     */
    @Override
    public Result getLowRegionList(SearchParam param) {
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数异常");
        }

        List<Region> regions = regionMapper.selectLowRegions(id);

        return Result.success("获取成功", regions);
    }

    /**
     * 根据地区id查询上级地区
     *
     * @param param
     * @return
     */
    @Override
    public Result getRegionParentInfo(SearchParam param) {
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数异常");
        }

        Region region = regionMapper.selectRegionParent(id);

        return Result.success("获取成功", region);
    }

    /**
     * 根据地区id查询地区信息
     *
     * @param param
     * @return
     */
    @Override
    public Result getRegionInfo(SearchParam param) {
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数异常");
        }

        Region region = regionMapper.selectById(id);

        return Result.success("获取成功", region);
    }

    /**
     * 根据地区id查询对应的地区数据--按等级顺序返回所有对应的上级地区数据
     *
     * @param id
     * @return
     */
    @Override
    public LinkedList<Region> selectRegionDataById(Integer id) {
        LinkedList<Region> data = new LinkedList<>();

        while (true) {
            Region region = regionMapper.selectById(id);
            if (region == null) {
                break;
            }
            data.addFirst(region);
            id = region.getPid();
            if (id == 0 || id == 100000) {
                break;
            }
        }

        return data;
    }

    /**
     * 获取行业分类下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getIndustryCategoryLists(SearchParam param) {
        List<IndustryCategory> list = industryCategoryMapper.selectList();

        return Result.success("获取成功", list);
    }

    /**
     * 获取商超下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getSupermarketLists(SearchParam param) {
        List<SupermarketCommonVo> list = supermarketMapper.selectCommonList(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取商家下拉列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getShopLists(SearchParam param) {
        List<ShopCommonVo> list = shopMapper.selectCommonList(param);

        return Result.success("获取成功", list);
    }
}
