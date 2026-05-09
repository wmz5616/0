package com.zemcho.guzhe.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.controller.shop.vo.SettlementApplicationVO;
import com.zemcho.guzhe.controller.wechat.shop.param.AuditShopParam;
import com.zemcho.guzhe.controller.wechat.shop.vo.MerchantManageVO;
import com.zemcho.guzhe.entity.audit.SettlementApplication;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.shop.*;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderRefundApplyMapper;
import com.zemcho.guzhe.mapper.shop.*;
import com.zemcho.guzhe.mapper.audit.SettlementApplicationMapper;
import com.zemcho.guzhe.service.wechat.shop.AuditShopService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class AuditShopServiceImpl implements AuditShopService {

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private SettlementApplicationMapper settlementApplicationMapper;

    @Autowired
    private QualificationCertMapper qualificationCertMapper;

    @Autowired
    private ShopAuditMapper shopAuditMapper;

    @Autowired
    private ShopAuditCircleMapper shopAuditCircleMapper;

    @Autowired
    private ShopAuditIndustryMapper shopAuditIndustryMapper;

    @Autowired
    private ProductOrderRefundApplyMapper productOrderRefundApplyMapper;

    @Autowired
    private ShopManagerMapper shopManagerMapper;
    /**
     * 小程序端提交商家信息审核
     *
     * @param auditShopParam 参数
     * @param token          token
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitAuditShop(AuditShopParam auditShopParam, String token) {
        // 获取用户信息
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        //判断商家名称是否唯一
        if (shopMapper.selectByName(null, auditShopParam.getName()) != null) {
            return Result.error("商家名称重复");
        }
        if (shopAuditMapper.selectByName(auditShopParam.getName()) != null) {
            return Result.error("店铺名称已在审核中");
        }
        String phone = casUser.getPhone();
        // 提交商家信息,存入到shopAudit表中
        ShopAudit shopAudit = new ShopAudit();
        shopAudit.setUserId(userId);
        shopAudit.setCoverImageUrl(auditShopParam.getCoverImageUrl());//商家logo
        shopAudit.setName(auditShopParam.getName());//名称
        shopAudit.setLocation(auditShopParam.getLocation());//经纬度
        shopAudit.setAddress(auditShopParam.getAddress());// 地址
        shopAudit.setUserName(auditShopParam.getUserName());//店长
        shopAudit.setPhone(auditShopParam.getPhone());// 电话
        shopAudit.setStartTime(auditShopParam.getStartTime());
        shopAudit.setEndTime(auditShopParam.getEndTime());
        shopAudit.setCustomerPhone(auditShopParam.getCustomerPhone());//客户电话
        shopAudit.setCustomerCodeImg(auditShopParam.getCustomerCodeImg());//客户二维码
        shopAudit.setDescription(auditShopParam.getDescription());//介绍
        if (auditShopParam.getGalleryImages() != null && !auditShopParam.getGalleryImages().isEmpty()) {
            shopAudit.setGalleryImages(JSON.toJSONString(auditShopParam.getGalleryImages()));
        }

        shopAudit.setSubmitTime(LocalDateTime.now());
        shopAudit.setAuditStatus(0);//待审核状态
        shopAudit.setCreateTime(LocalDateTime.now());
        shopAudit.setUpdateTime(LocalDateTime.now());
        shopAudit.setQualificationCert(0);//未提交状态
        shopAuditMapper.insert(shopAudit);

        if (auditShopParam.getCircleIds() != null && !auditShopParam.getCircleIds().isEmpty()) {
            List<ShopAuditCircle> circles = new ArrayList<>();
            for (Integer circleId : auditShopParam.getCircleIds()) {
                ShopAuditCircle circle = new ShopAuditCircle();
                circle.setShopAuditId(shopAudit.getId());
                circle.setCircleId(circleId);
                circles.add(circle);
            }
            shopAuditCircleMapper.insertBatch(circles);
        }

        if (auditShopParam.getIndustryCategoryIds() != null && !auditShopParam.getIndustryCategoryIds().isEmpty()) {
            List<ShopAuditIndustry> industries = new ArrayList<>();
            for (Integer industryId : auditShopParam.getIndustryCategoryIds()) {
                ShopAuditIndustry industry = new ShopAuditIndustry();
                industry.setShopAuditId(shopAudit.getId());
                industry.setIndustryCategoryId(industryId);
                industries.add(industry);
            }
            shopAuditIndustryMapper.insertBatch(industries);
        }
        //保存入驻申请记录
        SettlementApplication settlementApplication = new SettlementApplication();
        settlementApplication.setUserId(userId);
        settlementApplication.setPhone(phone);
        settlementApplication.setApplyResult(0);
        settlementApplication.setSubmitTime(LocalDateTime.now());
        settlementApplication.setShopAuditId(shopAudit.getId());
        settlementApplicationMapper.insert(settlementApplication);

        return Result.success("操作成功");
    }

    /**
     * 小程序提交资质认证
     *
     * @param param 参数 资质认证
     * @param token token token
     * @return result
     */
    @Override
    public Result submitQualification(QualificationCertParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        Integer shopId = param.getShopId();

        if (shopId != null && shopId > 0) {
            QualificationCert existingCert = qualificationCertMapper.selectExistByShopId(shopId);
            if (existingCert != null) {
                return Result.error("该商家已有待审核的资质认证申请");
            }
        } else {
            QualificationCert existingCert = qualificationCertMapper.selectExistByUserId(userId);
            if (existingCert != null) {
                return Result.error("您已有待审核的资质认证申请");
            }
        }

        QualificationCert qualificationCert = new QualificationCert();
        BeanUtils.copyProperties(param, qualificationCert);

        qualificationCert.setCertResult(0);
        qualificationCert.setSubmitTime(LocalDateTime.now());
        qualificationCert.setCreateTime(LocalDateTime.now());
        qualificationCert.setUpdateTime(LocalDateTime.now());
        qualificationCert.setRejectReason("");
        qualificationCert.setUserId(userId);
        qualificationCert.setCertSide(2);

        if (shopId == null || shopId <= 0) {
            qualificationCert.setShopId(0);
        }

        int result = qualificationCertMapper.insert(qualificationCert);
        if (result > 0) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }


    /**
     * 小程序查询个人申请入驻记录
     *
     * @param param param
     * @param token token
     * @return result
     */
    @Override
    public Result getOwnApplyList(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        if (casUser.getAdminId() != null && casUser.getAdminId() > 0) {
            // 是管理员：查询该管理员管理的所有商家的入驻记录
            param.setSearchField1(casUser.getAdminId());
            param.setSearchId(null); // 清空userId限制
        } else {
            // 不是管理员：只查询自己的入驻记录
            param.setSearchField1(null);
            param.setSearchId(userId);
        }

        List<SettlementApplicationVO> settlementApplicationVOs = settlementApplicationMapper.selectList(param);
        return Result.success("操作成功", settlementApplicationVOs);
    }

    /**
     * 小程序根据id查询申请入驻记录
     *
     * @param param searchId 申请入驻记录ID
     * @param token token
     * @return result
     */
    @Override
    public Result getAuditShopById(String token, SearchParam param) {
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        SettlementApplication settlementApplication = settlementApplicationMapper.selectById(param.getSearchId());
        ShopAudit shopAudit = shopAuditMapper.selectById(settlementApplication.getShopAuditId());

        Map<String, Object> map = new HashMap<>();
        map.put("shopAudit", shopAudit);
        map.put("settlementApplication", settlementApplication);
        return Result.success("操作成功", map);
    }



    @Override
    public Result checkMerchantAdmin(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        String phone = casUser.getPhone();
        List<Integer> shopIds=shopManagerMapper.selectShopIdsByPhone(phone);
        ArrayList<Shop> shops = new ArrayList<>();
        for (Integer shopId : shopIds) {
            Shop shop = shopMapper.selectById(shopId);
            if(shop!=null){
                shops.add(shop);
            }
        }
        return Result.success("操作成功", shops);
    }

}