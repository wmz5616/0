package com.zemcho.guzhe.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.controller.shop.vo.SettlementApplicationVO;
import com.zemcho.guzhe.controller.wechat.shop.param.AuditShopParam;
import com.zemcho.guzhe.controller.wechat.shop.param.WechatQualificationCertParam;
import com.zemcho.guzhe.entity.audit.SettlementApplication;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.shop.*;
import com.zemcho.guzhe.entity.sys.Config;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderRefundApplyMapper;
import com.zemcho.guzhe.mapper.shop.*;
import com.zemcho.guzhe.mapper.audit.SettlementApplicationMapper;
import com.zemcho.guzhe.mapper.sys.ConfigMapper;
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
    private ShopManagerMapper shopManagerMapper;

    @Autowired
    private ShopAuditManagerMapper shopAuditManagerMapper;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ProductOrderRefundApplyMapper productOrderRefundApplyMapper;
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

        //校验手机号
        if (auditShopParam.getPhone() != null && !"".equals(auditShopParam.getPhone())
                && !auditShopParam.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号码");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        //判断商家名称是否唯一
        if (shopMapper.selectByName(null, auditShopParam.getName()) != null) {
            return Result.error("商家名称重复");
        }
        ShopAudit myAudit = shopAuditMapper.selectByUserIdAndName(userId, auditShopParam.getName());
        // 已驳回的记录可以再次申请提交
        if (myAudit != null && myAudit.getAuditStatus()!=null&&myAudit.getAuditStatus() != 2) {
            return Result.error("您已提交过该商家的入驻申请，请勿重复提交");
        }
        if (auditShopParam.getLocation() == null || auditShopParam.getLocation().trim().isEmpty()) {
            auditShopParam.setLocation("");
        }
        String phone = casUser.getPhone();
        String name = casUser.getName();
        if(name==null|| name.isBlank()){
            name=casUser.getNickname();
        }
        // 提交商家信息,存入到shopAudit表中
        String businessTime = resolveBusinessTime(
                auditShopParam.getBusinessTime(),
                auditShopParam.getStartTime(),
                auditShopParam.getEndTime()
        );
        ShopAudit shopAudit = new ShopAudit();
        shopAudit.setUserId(userId);
        shopAudit.setAuditStatus(0);//待审核状态
        shopAudit.setCoverImageUrl(auditShopParam.getCoverImageUrl());//商家logo
        shopAudit.setName(auditShopParam.getName());//名称
        shopAudit.setLocation(auditShopParam.getLocation());//经纬度
        shopAudit.setAddress(auditShopParam.getAddress());// 地址
        shopAudit.setUserName(auditShopParam.getUserName());//店长
        shopAudit.setPhone(auditShopParam.getPhone());// 电话
        shopAudit.setStartTime(auditShopParam.getStartTime());
        shopAudit.setEndTime(auditShopParam.getEndTime());
        shopAudit.setBusinessTime(businessTime);
        shopAudit.setCustomerPhone(auditShopParam.getCustomerPhone());//客户电话
        shopAudit.setCustomerCodeImg(auditShopParam.getCustomerCodeImg());//客户二维码
        shopAudit.setDescription(auditShopParam.getDescription()!=null?auditShopParam.getDescription():"");//介绍
        if (auditShopParam.getGalleryImages() != null && !auditShopParam.getGalleryImages().isEmpty()) {
            shopAudit.setGalleryImages(JSON.toJSONString(auditShopParam.getGalleryImages()));
        }

        shopAudit.setSubmitTime(LocalDateTime.now());
        shopAudit.setAuditStatus(0);//待审核状态
        shopAudit.setCreateTime(LocalDateTime.now());
        shopAudit.setUpdateTime(LocalDateTime.now());
        shopAudit.setQualificationCert(auditShopParam.getQualificationCert()!=null?1:0);//设置资质认证状态
        shopAuditMapper.insert(shopAudit);

        //设置申请用户作为店长
        ShopAuditManager shopAuditManager = new ShopAuditManager();
        shopAuditManager.setShopAuditId(shopAudit.getId());
        shopAuditManager.setName(name);
        shopAuditManager.setPhone(phone);
        shopAuditManager.setSort(1);
        shopAuditManager.setHeadManager(1);//是店长
        shopAuditManagerMapper.insert(shopAuditManager);

        //保存资质认证信息
        if(auditShopParam.getQualificationCert() != null) {
            QualificationCert qualificationCert = new QualificationCert();
            BeanUtils.copyProperties(auditShopParam.getQualificationCert(), qualificationCert);

            qualificationCert.setCertResult(0);
            qualificationCert.setSubmitTime(LocalDateTime.now());
            qualificationCert.setCreateTime(LocalDateTime.now());
            qualificationCert.setUpdateTime(LocalDateTime.now());
            qualificationCert.setRejectReason("");
            qualificationCert.setUserId(userId);
            qualificationCert.setCertSide(2);
            qualificationCert.setShopAuditId(shopAudit.getId());
            qualificationCert.setShopId(0);
            qualificationCertMapper.insert(qualificationCert);
        }

        //保存商圈
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
    @Transactional(rollbackFor = Exception.class)
    public Result submitQualification(WechatQualificationCertParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        Integer type = param.getType();
        if (type == null || (type != 1 && type != 2)) {
            return Result.error("参数错误");
        }

        Integer searchId = param.getSearchId();
        if(searchId==null){
            return Result.error("参数错误");
        }
        QualificationCert qualificationCert = new QualificationCert();
        BeanUtils.copyProperties(param, qualificationCert);
        if(param.getEmail()==null){
            qualificationCert.setEmail("");
        }
        qualificationCert.setUserId(userId);
        qualificationCert.setCertSide(2);//小程序
        qualificationCert.setCertResult(0);//待审核
        qualificationCert.setRejectReason("");
        qualificationCert.setSubmitTime(LocalDateTime.now());
        qualificationCert.setCreateTime(LocalDateTime.now());
        qualificationCert.setUpdateTime(LocalDateTime.now());
        if(param.getType()==1){
            //申请入驻记录入口
            QualificationCert qualificationCert1 = qualificationCertMapper.selectIfExistByShopAuditIdAndUserId(searchId, userId);
            if(qualificationCert1!=null&&qualificationCert1.getCertResult()!=2){
                return Result.error("请勿重复提交");
            }
            ShopAudit shopAudit = shopAuditMapper.selectById(searchId);
            if (shopAudit == null) {
                return Result.error("商家不存在");
            }
            qualificationCert.setShopAuditId(searchId);
            qualificationCert.setShopId(shopAudit.getShopId());
            //说明该商家已审核通过
            if(shopAudit.getShopId()!=null&&shopAudit.getShopId()>0){
                Shop shop = shopMapper.selectById(shopAudit.getShopId());
                shop.setQualificationCert(1);
                shopMapper.update(shop);
            }
            shopAudit.setQualificationCert(1);
            shopAuditMapper.update(shopAudit);
        }else {
            //商家信息入口
            QualificationCert qualificationCert1 = qualificationCertMapper.selectIfExistByShopIdAndUserId(searchId, userId);
            if(qualificationCert1!=null&&qualificationCert1.getCertResult()!=2){
                return Result.error("请勿重复提交");
            }
            Shop shop = shopMapper.selectById(searchId);
            if (shop == null) {
                return Result.error("商家不存在");
            }
            qualificationCert.setShopId(searchId);
            ShopAudit shopAudit = shopAuditMapper.selectByShopId(searchId);
            if (shopAudit == null) {
                return Result.error("商家审核记录不存在");
            }
            qualificationCert.setShopAuditId(shopAudit.getId());
            shop.setQualificationCert(1);
            shopMapper.update(shop);
            shopAudit.setQualificationCert(1);
            shopAuditMapper.update(shopAudit);
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
        String whereSql = "sa.user_id = " + userId;
        param.setWhereSql(whereSql);
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
        if (settlementApplication == null) {
            return Result.error("申请记录不存在");
        }
        ShopAudit shopAudit = shopAuditMapper.selectById(settlementApplication.getShopAuditId());
        if (shopAudit == null) {
            return Result.error("申请记录不存在");
        }
        List<ShopAuditCircle> circles = shopAuditCircleMapper.selectByShopAuditId(shopAudit.getId());
        List<ShopAuditIndustry> industries = shopAuditIndustryMapper.selectByShopAuditId(shopAudit.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("shopAudit", shopAudit);
        map.put("settlementApplication", settlementApplication);
        map.put("circles", circles);
        map.put("industries", industries);
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
        ArrayList<Map<String, Object>> shops = new ArrayList<>();

        for (Integer shopId : shopIds) {
            //获取启用的商家
            Shop shop = shopMapper.selectOnlineById(shopId);
            if(shop!=null){
                // 查询该商家的待退款审核订单数
                Integer countPending = productOrderRefundApplyMapper.countPendingByShopId(shop.getId());
                Integer refundCount = countPending != null ? countPending : 0;

                // 将商家信息和退款数量一起返回
                Map<String, Object> shopMap = new HashMap<>();
                shopMap.put("shop", shop);
                shopMap.put("refundAuditCount", refundCount);
                shops.add(shopMap);
            }
        }

        // 返回商家列表和总退款审核订单数
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("shops", shops);

        return Result.success("操作成功", resultMap);
    }

    private String resolveBusinessTime(String businessTime, String startTime, String endTime) {
        if (businessTime != null && !businessTime.trim().isEmpty()) {
            return businessTime.trim();
        }
        if ((startTime == null || startTime.trim().isEmpty()) && (endTime == null || endTime.trim().isEmpty())) {
            return null;
        }
        if (startTime == null || startTime.trim().isEmpty()) {
            return endTime == null ? null : endTime.trim();
        }
        if (endTime == null || endTime.trim().isEmpty()) {
            return startTime.trim();
        }
        return startTime.trim() + " - " + endTime.trim();
    }

    @Override
    public Result getQualificationCertDetail(SearchParam param,String token) {
        Integer searchType = param.getSearchType();
        if(searchType==null ||(searchType != 1 && searchType != 2)){
            return Result.error("参数错误");
        }
        if(searchType==1){
            //1申请入驻入口
        Integer shopAuditId = param.getSearchId();
        if (shopAuditId == null) {
            return Result.error("参数错误");
        }
        QualificationCert qualificationCert = qualificationCertMapper.selectByShopAuditIdSimple(shopAuditId);
        return Result.success("获取成功", qualificationCert);
        }
        else
            {
            //2商家列表入口
            Integer shopId = param.getSearchId();
            if (shopId == null) {
                return Result.error("参数错误");
            }
            QualificationCert qualificationCert = qualificationCertMapper.selectByShopId(shopId);
            return Result.success("获取成功", qualificationCert);
            }
    }

    @Override
    public Result getMerchantNotice() {
        Config showConfig = configMapper.selectConfigByKey("show_merchant_notice");
        if (showConfig == null || !"1".equals(showConfig.getValue())) {
            return Result.success("获取成功", "");
        }

        Config noticeConfig = configMapper.selectConfigByKey("merchant_notice");
        if (noticeConfig == null || noticeConfig.getValue() == null) {
            return Result.success("合约内容为空", "");
        }

        return Result.success("获取成功", noticeConfig.getValue());
    }

    @Override
    public Result getCount(String token) {
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
        Integer totalRefundCount = 0;

        for (Integer shopId : shopIds) {
            //获取启用的商家
            Shop shop = shopMapper.selectOnlineById(shopId);
            if(shop!=null){
               // 查询该商家的待退款审核订单数
                Integer countPending = productOrderRefundApplyMapper.countPendingByShopId(shop.getId());
                Integer refundCount = countPending != null ? countPending : 0;

                // 累加总退款订单数
                totalRefundCount += refundCount;
            }
        }
        // 返回商家列表和总退款审核订单数
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalRefundAuditCount", totalRefundCount);

        return Result.success("操作成功", resultMap);

    }

}
