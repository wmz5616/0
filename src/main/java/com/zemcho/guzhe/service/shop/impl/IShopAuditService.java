package com.zemcho.guzhe.service.shop.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.shop.vo.QualificationCertVO;
import com.zemcho.guzhe.controller.shop.vo.SettlementApplicationVO;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopAuditHandleParam;
import com.zemcho.guzhe.controller.wechat.shop.vo.ShopAuditDetailVO;
import com.zemcho.guzhe.entity.audit.SettlementApplication;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.shop.*;
import com.zemcho.guzhe.mapper.audit.SettlementApplicationMapper;
import com.zemcho.guzhe.mapper.cas.CasAdminMapper;
import com.zemcho.guzhe.mapper.shop.*;
import com.zemcho.guzhe.service.shop.ShopAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HXH
 */
@Service
public class IShopAuditService implements ShopAuditService {

    @Autowired
    private SettlementApplicationMapper settlementApplicationMapper;
    @Autowired
    private ShopAuditMapper shopAuditMapper;
    @Autowired
    private ShopAuditIndustryMapper shopAuditIndustryMapper;
    @Autowired
    private ShopManagerMapper shopManagerMapper;
    @Autowired
    private QualificationCertMapper qualificationCertMapper;
    @Autowired
    private CasAdminMapper casAdminMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopAuditCircleMapper shopAuditCircleMapper;
    @Autowired
    private IndustryCategoryShopMapper industryCategoryShopMapper;
    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;
    @Override
    public Result selectList(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<SettlementApplicationVO> list = settlementApplicationMapper.selectAll(param);
        PageInfo<SettlementApplicationVO> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result detail(SearchParam param) {
        if(param.getSearchId() == null){
            return Result.error("参数错误");
        }

        // 1. 查询商家审核基本信息
        ShopAudit shopAudit = shopAuditMapper.selectById(param.getSearchId());
        if (shopAudit == null) {
            return Result.error("商家审核记录不存在");
        }

        // 2. 构建详情VO
        ShopAuditDetailVO detailVO = new ShopAuditDetailVO();
        detailVO.setId(shopAudit.getId());
        detailVO.setUserId(shopAudit.getUserId());
        detailVO.setName(shopAudit.getName());
        detailVO.setLocation(shopAudit.getLocation());
        detailVO.setAddress(shopAudit.getAddress());
        detailVO.setUserName(shopAudit.getUserName());
        detailVO.setPhone(shopAudit.getPhone());
        detailVO.setStartTime(shopAudit.getStartTime());
        detailVO.setEndTime(shopAudit.getEndTime());
        detailVO.setCustomerPhone(shopAudit.getCustomerPhone());
        detailVO.setCustomerCodeImg(shopAudit.getCustomerCodeImg());
        detailVO.setDescription(shopAudit.getDescription());
        detailVO.setSubmitTime(shopAudit.getSubmitTime());
        detailVO.setAuditStatus(shopAudit.getAuditStatus());
        detailVO.setAuditUser(shopAudit.getAuditUser());
        detailVO.setAuditTime(shopAudit.getAuditTime());
        detailVO.setAuditPhone(shopAudit.getAuditPhone());
        detailVO.setRejectReason(shopAudit.getRejectReason());
        detailVO.setShopId(shopAudit.getShopId());
        detailVO.setStatus(shopAudit.getStatus());
        detailVO.setRecommendOrder(shopAudit.getRecommendOrder());

        // 3. 解析轮播图（JSON字符串转List）
        if (shopAudit.getGalleryImages() != null && !shopAudit.getGalleryImages().isEmpty()) {
            detailVO.setGalleryImages(JSON.parseArray(shopAudit.getGalleryImages(), String.class));
        } else {
            detailVO.setGalleryImages(new ArrayList<>());
        }

        //查询所属商超
        List<ShopAuditCircle> circles = shopAuditCircleMapper.selectByShopAuditId(shopAudit.getId());
        if (circles != null && !circles.isEmpty()) {
            List<Integer> circleIds = circles.stream()
                    .map(ShopAuditCircle::getCircleId)
                    .collect(Collectors.toList());
            detailVO.setCircleIds(circleIds);
        } else {
            detailVO.setCircleIds(new ArrayList<>());
        }
        // 4. 查询关联的行业类别
        List<ShopAuditIndustry> industryCategories = shopAuditIndustryMapper.selectByShopAuditId(shopAudit.getId());
        detailVO.setIndustryCategories(industryCategories);

        // 5. 查询关联的商家管理人员（如果有shopId则查询shop的管理人员，否则查询审核记录的管理人员）
        Integer queryShopId = shopAudit.getShopId() != null ? shopAudit.getShopId() : shopAudit.getId();
        List<ShopManager> managers = shopManagerMapper.selectByShopId(queryShopId);
        detailVO.setManagers(managers != null ? managers : new ArrayList<>());

        return Result.success("获取成功", detailVO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result handle(SearchParam param,String token,ShopAuditHandleParam shopAuditHandleParam) {
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }

        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return Result.error("用户未登录");
        }
        Integer adminId = authJwtData.getAdminId();
        CasAdmin admin = casAdminMapper.selectById(adminId);
        if (admin == null) {
            return Result.error("管理员不存在");
        }

        ShopAudit shopAudit = shopAuditMapper.selectById(param.getSearchId());
        if (shopAudit == null) {
            return Result.error("商家审核记录不存在");
        }

        if (shopAudit.getAuditStatus() != 0) {
            return Result.error("该记录已审核，请勿重复操作");
        }
        String auditPhone = authJwtData.getAccount();
        //审核结果(1:通过；2:驳回)
        Integer auditResult = param.getSearchType();
        if (auditResult == null || (auditResult != 1 && auditResult != 2)) {
            return Result.error("审核结果参数错误");
        }
        //驳回原因
        if (auditResult == 2 && (param.getKeyword() == null || param.getKeyword().trim().isEmpty())) {
            return Result.error("驳回时必须填写驳回原因");
        }
        //无论审核是否通过，都需要将修改后的数据重新保存到shopAudit,shopAuditCircle,shopAuditIndustry表数据
        if (shopAuditHandleParam != null) {
            // 更新商家基本信息
            if (shopAuditHandleParam.getName() != null && !shopAuditHandleParam.getName().trim().isEmpty()) {
                if (!shopAuditHandleParam.getName().equals(shopAudit.getName())) {
                    if (shopMapper.selectByName(null, shopAuditHandleParam.getName()) != null) {
                        return Result.error("商家名称已存在");
                    }
                    if (shopAuditMapper.selectByName(shopAuditHandleParam.getName()) != null) {
                        return Result.error("商家名称已在审核中");
                    }
                }
                shopAudit.setName(shopAuditHandleParam.getName());
            }

            if (shopAuditHandleParam.getCoverImageUrl() != null) {
                shopAudit.setCoverImageUrl(shopAuditHandleParam.getCoverImageUrl());
            }

            if (shopAuditHandleParam.getGalleryImages() != null && !shopAuditHandleParam.getGalleryImages().isEmpty()) {
                shopAudit.setGalleryImages(JSON.toJSONString(shopAuditHandleParam.getGalleryImages()));
            }

            if (shopAuditHandleParam.getLocation() != null) {
                shopAudit.setLocation(shopAuditHandleParam.getLocation());
            }

            if (shopAuditHandleParam.getAddress() != null) {
                shopAudit.setAddress(shopAuditHandleParam.getAddress());
            }

            if (shopAuditHandleParam.getUserName() != null) {
                shopAudit.setUserName(shopAuditHandleParam.getUserName());
            }

            if (shopAuditHandleParam.getPhone() != null) {
                shopAudit.setPhone(shopAuditHandleParam.getPhone());
            }

            if (shopAuditHandleParam.getStartTime() != null) {
                shopAudit.setStartTime(shopAuditHandleParam.getStartTime());
            }

            if (shopAuditHandleParam.getEndTime() != null) {
                shopAudit.setEndTime(shopAuditHandleParam.getEndTime());
            }

            if (shopAuditHandleParam.getCustomerPhone() != null) {
                shopAudit.setCustomerPhone(shopAuditHandleParam.getCustomerPhone());
            }

            if (shopAuditHandleParam.getCustomerCodeImg() != null) {
                shopAudit.setCustomerCodeImg(shopAuditHandleParam.getCustomerCodeImg());
            }

            if (shopAuditHandleParam.getDescription() != null) {
                shopAudit.setDescription(shopAuditHandleParam.getDescription());
            }
            shopAudit.setUpdateTime(LocalDateTime.now());
            shopAuditMapper.update(shopAudit);

            // 更新行业类别
            if (shopAuditHandleParam.getIndustryCategoryIds() != null && !shopAuditHandleParam.getIndustryCategoryIds().isEmpty()) {
                shopAuditIndustryMapper.deleteByShopAuditId(shopAudit.getId());
                List<ShopAuditIndustry> newIndustries = new ArrayList<>();
                for (Integer industryId : shopAuditHandleParam.getIndustryCategoryIds()) {
                    ShopAuditIndustry industry = new ShopAuditIndustry();
                    industry.setShopAuditId(shopAudit.getId());
                    industry.setIndustryCategoryId(industryId);
                    newIndustries.add(industry);
                }
                shopAuditIndustryMapper.insertBatch(newIndustries);
            }

            // 更新商圈
            if (shopAuditHandleParam.getCircleIds() != null && !shopAuditHandleParam.getCircleIds().isEmpty()) {
                shopAuditCircleMapper.deleteByShopAuditId(shopAudit.getId());
                List<ShopAuditCircle> newCircles = new ArrayList<>();
                for (Integer circleId : shopAuditHandleParam.getCircleIds()) {
                    ShopAuditCircle circle = new ShopAuditCircle();
                    circle.setShopAuditId(shopAudit.getId());
                    circle.setCircleId(circleId);
                    newCircles.add(circle);
                }
                shopAuditCircleMapper.insertBatch(newCircles);
            }

            // 更新商家管理人员
            if (shopAuditHandleParam.getManagers() != null && !shopAuditHandleParam.getManagers().isEmpty()) {
                shopManagerMapper.deleteByShopId(shopAudit.getId());
                for (ShopManager manager : shopAuditHandleParam.getManagers()) {
                    manager.setShopId(shopAudit.getId());
                    manager.setId(null);
                    shopManagerMapper.insert(manager);
                }
            }
        }
        // 查询入驻申请记录（使用实体类接收）
        SettlementApplication application = settlementApplicationMapper.selectByShopAuditId(shopAudit.getId());
        if (application == null) {
            return Result.error("入驻申请记录不存在");
        }
        //审核通过
        if(auditResult==1){
            //往shop表插入数据,更新shop表数据，更新行业类别表(industryCategory)数据，更新商家管理人员表数据,更新BusinessCircleShop表数据,更新资质认证表数据
            // 往shop表插入数据
            Shop shop = new Shop();
            shop.setCoverImageUrl(shopAudit.getCoverImageUrl());
            shop.setGalleryImages(shopAudit.getGalleryImages());
            shop.setName(shopAudit.getName());
            shop.setLocation(shopAudit.getLocation());
            shop.setAddress(shopAudit.getAddress());
            shop.setUserName(shopAudit.getUserName());
            shop.setPhone(shopAudit.getPhone());
            shop.setStartTime(shopAudit.getStartTime());
            shop.setEndTime(shopAudit.getEndTime());
            shop.setCustomerPhone(shopAudit.getCustomerPhone());
            shop.setCustomerCodeImg(shopAudit.getCustomerCodeImg());
            shop.setDescription(shopAudit.getDescription());
            shop.setStatus(1);
            shop.setShopStatus(0);
            shop.setReceiptStatus(0);
            shop.setQualificationCert(0);
            shop.setCreateTime(LocalDateTime.now());
            shop.setUpdateTime(LocalDateTime.now());
            shop.setTopRecommend(0);//是否推荐置顶
            shopMapper.insert(shop);
            Integer shopId = shop.getId();

            // 更新shopAudit表，记录shopId和审核信息
            shopAudit.setShopId(shopId);
            shopAudit.setAuditStatus(1);
            shopAudit.setAuditUser(adminId);
            shopAudit.setAuditTime(LocalDateTime.now());
            shopAudit.setAuditPhone(auditPhone);
            shopAudit.setUpdateTime(LocalDateTime.now());
            shopAuditMapper.update(shopAudit);

            // 更新入驻申请表
            application.setApplyResult(1);
            application.setShopId(shopId);
            application.setAuditUser(adminId);
            application.setAuditTime(LocalDateTime.now());
            application.setAuditPhone(auditPhone);
            settlementApplicationMapper.update(application);

            // 更新行业类别表（从shop_audit_industry迁移到shop_industry_category）
            List<ShopAuditIndustry> industries = shopAuditIndustryMapper.selectByShopAuditId(shopAudit.getId());
            if (industries != null && !industries.isEmpty()) {
                List<IndustryCategoryShop> shopIndustries = new ArrayList<>();
                for (ShopAuditIndustry industry : industries) {
                    IndustryCategoryShop shopIndustry = new IndustryCategoryShop();
                    shopIndustry.setShopId(shopId);
                    shopIndustry.setIndustryCategoryId(industry.getIndustryCategoryId());
                    shopIndustries.add(shopIndustry);
                }
                industryCategoryShopMapper.insertBatch(shopIndustries);
            }

            // 更新商圈表（从shop_audit_circle迁移到business_circle_shop）
            List<ShopAuditCircle> circles = shopAuditCircleMapper.selectByShopAuditId(shopAudit.getId());
            if (circles != null && !circles.isEmpty()) {
                List<BusinessCircleShop> shopCircles = new ArrayList<>();
                for (ShopAuditCircle circle : circles) {
                    BusinessCircleShop shopCircle = new BusinessCircleShop();
                    shopCircle.setShopId(shopId);
                    shopCircle.setCircleId(circle.getCircleId());
                    shopCircles.add(shopCircle);
                }
                businessCircleShopMapper.insertBatch(shopCircles);
            }

            // 更新商家管理人员表（更新shopId）
            List<ShopManager> managers = shopManagerMapper.selectByShopId(shopAudit.getId());
            if (managers != null && !managers.isEmpty()) {
                for (ShopManager manager : managers) {
                    manager.setShopId(shopId);
                    shopManagerMapper.update(manager);
                }
            }
        } //审核驳回
        else if(auditResult==2){
            // 更新shopAudit表，设置驳回状态和原因
            shopAudit.setAuditStatus(2);
            shopAudit.setAuditUser(adminId);
            shopAudit.setAuditTime(LocalDateTime.now());
            shopAudit.setAuditPhone(auditPhone);
            shopAudit.setRejectReason(param.getKeyword());
            shopAudit.setUpdateTime(LocalDateTime.now());
            shopAuditMapper.update(shopAudit);

            // 更新入驻申请表
            application.setApplyResult(2);
            application.setAuditUser(adminId);
            application.setAuditTime(LocalDateTime.now());
            application.setAuditPhone(auditPhone);
            application.setRejectReason(param.getKeyword());
            settlementApplicationMapper.update(application);
        }
        return Result.success(auditResult == 1 ? "审核通过成功" : "审核驳回成功");
    }


    @Override
    public Result get(SearchParam param, String token) {
        Integer searchId = param.getSearchId();
        if(searchId==null){
           return Result.error("参数错误");
       }
        // 1. 查询商家审核基本信息
        ShopAudit shopAudit = shopAuditMapper.selectById(searchId);
        if (shopAudit == null) {
            return Result.error("商家审核记录不存在");
        }
        QualificationCertVO qualificationCertVO = qualificationCertMapper.selectByShopAuditId(searchId);
        if (qualificationCertVO == null) {
            return Result.error("资质认证记录不存在");
        }
        return Result.success("操作成功", qualificationCertVO);
    }

    @Override
    public Result auditQualificationCert(SearchParam param, String token) {
        // 1. 参数校验
        Integer certId = param.getSearchId();
        if (certId == null) {
            return Result.error("资质认证ID不能为空");
        }

        Integer auditResult = param.getSearchType();
        if (auditResult == null || (auditResult != 1 && auditResult != 2)) {
            return Result.error("审核结果参数错误，1=通过，2=驳回");
        }

        // 2. 验证 token 获取审核人信息
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return Result.error("用户未登录");
        }
        Integer adminId = authJwtData.getAdminId();
        CasAdmin admin = casAdminMapper.selectById(adminId);
        if (admin == null) {
            return Result.error("管理员不存在");
        }

        // 3. 查询资质认证记录
        QualificationCert cert = qualificationCertMapper.selectById(certId);
        if (cert == null) {
            return Result.error("资质认证记录不存在");
        }

        // 4. 检查是否已经审核过
        if (cert.getCertResult() != null && cert.getCertResult() != 0) {
            return Result.error("该资质认证已审核，请勿重复操作");
        }

        // 5. 驳回时必须填写审核意见
        String rejectReason = param.getKeyword();
        if (auditResult == 2 && (rejectReason == null || rejectReason.trim().isEmpty())) {
            return Result.error("驳回时必须填写审核意见");
        }

        // 6. 更新资质认证记录
        cert.setCertResult(auditResult);
        cert.setCertUser(adminId);
        cert.setRejectReason(auditResult == 2 ? rejectReason : null);
        cert.setUpdateTime(LocalDateTime.now());
        qualificationCertMapper.update(cert);

        // 7. 根据关联的 shop_audit_id 或 shop_id 更新对应的表
        if (cert.getShopAuditId() != null && cert.getShopAuditId() > 0) {
            // 情况1：资质认证关联的是 shop_audit 记录（商家还在入驻审核阶段）
            ShopAudit shopAudit = shopAuditMapper.selectById(cert.getShopAuditId());
            if (shopAudit != null) {
                // 更新 shop_audit 表的资质认证状态
                shopAudit.setQualificationCert(auditResult);
                shopAudit.setUpdateTime(LocalDateTime.now());
                shopAuditMapper.update(shopAudit);
            }
        } else if (cert.getShopId() != null && cert.getShopId() > 0) {
            // 情况2：资质认证关联的是 shop 记录（商家已经入驻成功）
            Shop shop = shopMapper.selectById(cert.getShopId());
            if (shop != null) {
                // 更新 shop 表的资质认证状态
                shop.setQualificationCert(auditResult);
                shop.setUpdateTime(LocalDateTime.now());
                shopMapper.update(shop);

                // 同时更新 shop_audit 表（通过 shop_id 反查审核记录）
                ShopAudit shopAudit = shopAuditMapper.selectByShopId(cert.getShopId());
                if (shopAudit != null) {
                    shopAudit.setQualificationCert(auditResult);
                    shopAudit.setUpdateTime(LocalDateTime.now());
                    shopAuditMapper.update(shopAudit);
                }
            }
        }

        return Result.success(auditResult == 1 ? "审核通过成功" : "审核驳回成功");
    }

}


