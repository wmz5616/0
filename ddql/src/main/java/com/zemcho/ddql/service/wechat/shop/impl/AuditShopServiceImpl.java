package com.zemcho.ddql.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthAttrData;
import com.zemcho.ddql.common.dto.AuthJwtData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.audit.param.ShopAuditHandleParam;
import com.zemcho.ddql.controller.audit.vo.SettlementApplicationVO;
import com.zemcho.ddql.controller.business.param.QualificationCertParam;
import com.zemcho.ddql.controller.business.vo.ShopCircleListVO;
import com.zemcho.ddql.controller.business.vo.ShopIndustryCategoryListVO;
import com.zemcho.ddql.controller.business.vo.ShopVO;
import com.zemcho.ddql.controller.wechat.shop.param.AuditShopParam;
import com.zemcho.ddql.entity.audit.SettlementApplication;
import com.zemcho.ddql.entity.business.*;
import com.zemcho.ddql.entity.cas.CasAdmin;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.*;
import com.zemcho.ddql.mapper.cas.CasAdminMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.wechat.shop.AuditShopService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AuditShopServiceImpl implements AuditShopService {

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;

    @Autowired
    private IndustryCategoryShopMapper industryCategoryShopMapper;

    @Autowired
    private SettlementApplicationMapper settlementApplicationMapper;

    @Autowired
    private QualificationCertMapper qualificationCertMapper;
    @Autowired
    private ShopManagerMapper shopManagerMapper;
    @Autowired
    private CasAdminMapper casAdminMapper;
    @Autowired
    private BusinessCircleMapper businessCircleMapper;
    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

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
        // 校验电话号码
        if (auditShopParam.getPhone() != null && !"".equals(auditShopParam.getPhone())
                && !auditShopParam.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号码");
        }
        String phone = casUser.getPhone();
        String name = casUser.getName();
        String nickname = casUser.getNickname();
        // 提交商家信息
        Shop shop = new Shop();
        // 提交商家信息,存入到shopAudit表中
        String businessTime = resolveBusinessTime(
                auditShopParam.getBusinessTime(),
                auditShopParam.getStartTime(),
                auditShopParam.getEndTime()
        );
        shop.setName(auditShopParam.getName());
        shop.setRate(0);
        shop.setDescription(auditShopParam.getDescription());
        shop.setPhone(auditShopParam.getPhone());
        shop.setUserName(auditShopParam.getUserName());
        shop.setLocation(auditShopParam.getLocation());
        shop.setAddress(auditShopParam.getAddress());
        shop.setCreateTime(LocalDateTime.now());
        shop.setCoverImageUrl(auditShopParam.getCoverImageUrl());
        shop.setStartTime(auditShopParam.getStartTime());
        shop.setEndTime(auditShopParam.getEndTime());
        shop.setBusinessTime(businessTime);
        // 1. 将逗号分隔的字符串转成 List<String>
        String galleryImagesStr = auditShopParam.getGalleryImages();
        List<String> galleryList = Arrays.stream(galleryImagesStr.split(","))
                .map(String::trim)           // 去除首尾空格
                .filter(s -> !s.isEmpty())   // 过滤空字符串
                .collect(Collectors.toList());

        // 2. 将 List 转成 JSON 字符串存到数据库
        shop.setGalleryImages(JSON.toJSONString(galleryList));
        // 未认证
        shop.setQualificationCert(0);
        // 不置顶
        shop.setTopRecommend(0);
        shop.setStatus(0);
        shop.setShopStatus(1);
        shop.setReceiptStatus(0);
        shop.setCreateTime(LocalDateTime.now());
        shop.setUpdateTime(LocalDateTime.now());
        shop.setDeleteTime(LocalDateTime.now());//审核不通过
        shopMapper.insert(shop);
        // 提交关联信息
        if (auditShopParam.getCircleIds() != null && !auditShopParam.getCircleIds().isEmpty()) {
            List<BusinessCircleShop> businessCircleShops = new ArrayList<>();
            for (Integer circleId : auditShopParam.getCircleIds()) {
                BusinessCircleShop businessCircleShop = new BusinessCircleShop();
                businessCircleShop.setShopId(shop.getId());
                businessCircleShop.setCircleId(circleId);
                businessCircleShops.add(businessCircleShop);
            }
            businessCircleShopMapper.insertBatch(businessCircleShops);
        }
        if (auditShopParam.getIndustryCategoryIds() != null && !auditShopParam.getIndustryCategoryIds().isEmpty()) {
            List<IndustryCategoryShop> industryCategoryShops = new ArrayList<>();
            for (Integer industryCategoryId : auditShopParam.getIndustryCategoryIds()) {
                IndustryCategoryShop industryCategoryShop = new IndustryCategoryShop();
                industryCategoryShop.setShopId(shop.getId());
                industryCategoryShop.setIndustryCategoryId(industryCategoryId);
                industryCategoryShops.add(industryCategoryShop);
            }
            industryCategoryShopMapper.insertBatch(industryCategoryShops);
        }
        //设置提交信息的用户为店长
        ShopManager shopManager = new ShopManager();
        shopManager.setShopId(shop.getId());
        shopManager.setHeadManager(1);
        shopManager.setPhone(phone);
        shopManager.setSort(1);
        if(name.isBlank()){
        shopManager.setName(nickname);
        }else {
            shopManager.setName(name);
        }
        shopManagerMapper.insert(shopManager);
        // 提交商家审核信息
        SettlementApplication settlementApplication = new SettlementApplication();
        settlementApplication.setUserId(userId);
        settlementApplication.setPhone(phone);
        settlementApplication.setApplyResult(0);
        settlementApplication.setSubmitTime(LocalDateTime.now());
        settlementApplication.setShopId(shop.getId());
        // 保存商家审核信息
        settlementApplicationMapper.insert(settlementApplication);

        return Result.success("操作成功");
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

    /**
     * 小程序提交资质认证
     *
     * @param param 参数 资质认证
     * @param token token token
     * @return result
     */
    @Override
    public Result submitQualification(QualificationCertParam param, String token) {
        // 获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        // 检查该商家是否已有资质认证记录 查询待审核和已通过的记录
        QualificationCert existingCert = qualificationCertMapper.selectExistByShopId(param.getShopId());
        if (existingCert != null) {
            return Result.error("该商家已有待审核的资质认证申请");
        }

        QualificationCert qualificationCert = new QualificationCert();
        BeanUtils.copyProperties(param, qualificationCert);

        // 设置默认值
        qualificationCert.setCertResult(1); // 默认待审核
        qualificationCert.setSubmitTime(LocalDateTime.now());
        qualificationCert.setCreateTime(LocalDateTime.now());
        qualificationCert.setUpdateTime(LocalDateTime.now());
        qualificationCert.setRejectReason("");
        qualificationCert.setUserId(userId);
        qualificationCert.setCertSide(2);

        int result = qualificationCertMapper.insert(qualificationCert);
        if (result > 0) {
            // 同步更新商家的资质认证状态为待审核
            Shop shop = shopMapper.selectById(param.getShopId());
            if (shop != null) {
                shop.setQualificationCert(1);
                shopMapper.update(shop);
            }
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    /**
     * 后台获取商家审核列表
     *
     * @param param 参数
     * @param token token
     * @return result
     */
    @Override
    public Result getAuditShopList(SearchParam param, String token) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<SettlementApplicationVO> settlementApplicationVOs = settlementApplicationMapper.selectList(param);
        return Result.success("操作成功", new PageInfo<>(settlementApplicationVOs));
    }

    /**
     * 后台处理商家审核
     *
     * @param param searchId 申请信息id keyword 驳回元婴 searchType 是否通过
     * @param token token
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result handleAuditShop(ShopAuditHandleParam param, String token) {
        // 1. 参数校验
        Integer shopId = param.getId();
        if (shopId == null) {
            return Result.error("参数错误");
        }

        Integer auditResult = param.getAuditStatus();
        if (auditResult == null || (auditResult != 1 && auditResult != 2)) {
            return Result.error("审核结果参数错误");
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

        Shop shop = shopMapper.selectAuditById(shopId);
        if (shop == null) {
            return Result.error("商家审核记录不存在");
        }

        SettlementApplication application = settlementApplicationMapper.selectByShopId(shop.getId());
        if (application == null) {
            return Result.error("入驻申请记录不存在");
        }

        String auditPhone = authJwtData.getAccount();
        // 驳回时必须填写驳回原因
        if (auditResult == 2 && (param.getRejectReason() == null || param.getRejectReason().trim().isEmpty())) {
            return Result.error("驳回时必须填写驳回原因");
        }

        if (param.getLocation() == null || param.getLocation().trim().isEmpty()) {
            param.setLocation("");
        }
        // 2. 更新商家基本信息
        if (param.getName() != null && !param.getName().trim().isEmpty()) {
            if (!param.getName().equals(shop.getName())) {
                if (shopMapper.selectByName(null, param.getName()) != null) {
                    return Result.error("商家名称已存在");
                }
            }
            shop.setName(param.getName());
        }

        if (param.getCoverImageUrl() != null) {
            shop.setCoverImageUrl(param.getCoverImageUrl());
        }

        if (param.getGalleryImages() != null && !param.getGalleryImages().isEmpty()) {
            shop.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
        }

        if (param.getLocation() != null) {
            shop.setLocation(param.getLocation());
        }

        if (param.getAddress() != null) {
            shop.setAddress(param.getAddress());
        }

        if (param.getUserName() != null) {
            shop.setUserName(param.getUserName());
        }

        if (param.getPhone() != null) {
            shop.setPhone(param.getPhone());
        }

        if (param.getStartTime() != null) {
            shop.setStartTime(param.getStartTime());
        }

        if (param.getEndTime() != null) {
            shop.setEndTime(param.getEndTime());
        }
        if(param.getEndTime() != null&&param.getStartTime() != null){
            shop.setBusinessTime(param.getBusinessTime());
        }
        if (param.getDescription() != null) {
            shop.setDescription(param.getDescription());
        }
        shop.setTopConsumption(param.getTopConsumption());
        if(param.getTopConsumption().equals(1)){
            //开启消费置顶
            shop.setTopConsumptionStartTime(param.getTopConsumptionStartTime());
            shop.setTopConsumptionEndTime(param.getTopConsumptionEndTime());
        }
        shop.setTopRecommend(param.getTopRecommend());
        if(param.getTopRecommend().equals(1)){
            //开启推荐置顶
            shop.setTopStartTime(param.getTopStartTime());
            shop.setTopEndTime(param.getTopEndTime());
        }
        // status 和 sort 有 @NotNull 注解，直接赋值
        shop.setStatus(1);
            //启用
        shop.setReceiptStatus(1);//默认开启
        shop.setShopStatus(0);//默认启用

        shop.setRecommendOrder(param.getRecommendOrder());
        //审核通过
        if(param.getAuditStatus().equals(1)){
            shop.setDeleteTime(null);
        }
        shop.setUpdateTime(LocalDateTime.now());
        shopMapper.update(shop);

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(shopId);
        // 更新行业类别
        if (param.getIndustryCategoryIds() != null && !param.getIndustryCategoryIds().isEmpty()) {
            industryCategoryShopMapper.deleteByShopIds(integers);
            List<IndustryCategoryShop> newIndustries = new ArrayList<>();
            for (Integer industryId : param.getIndustryCategoryIds()) {
                IndustryCategoryShop industry = new IndustryCategoryShop();
                industry.setShopId(shop.getId());
                industry.setIndustryCategoryId(industryId);
                newIndustries.add(industry);
            }
            industryCategoryShopMapper.insertBatch(newIndustries);
        }

        // 更新商圈
        if (param.getCircleIds() != null && !param.getCircleIds().isEmpty()) {
            businessCircleShopMapper.deleteByShopIds(integers);
            List<BusinessCircleShop> newCircles = new ArrayList<>();
            for (Integer circleId : param.getCircleIds()) {
                BusinessCircleShop circle = new BusinessCircleShop();
                circle.setShopId(shop.getId());
                circle.setCircleId(circleId);
                newCircles.add(circle);
            }
            businessCircleShopMapper.insertBatch(newCircles);
        }

        // 更新商家管理人员（批量插入，第一个为店长）
        if (param.getManagers() != null && !param.getManagers().isEmpty()) {
            shopManagerMapper.deleteByShopId(shop.getId());
            List<ShopManager> newManagers = new ArrayList<>();
            for (int i = 0; i < param.getManagers().size(); i++) {
                ShopManager manager = param.getManagers().get(i);
                //校验手机号
                if (manager.getPhone() != null && !"".equals(manager.getPhone())
                        && !manager.getPhone().matches("^1[3-9]\\d{9}$")) {
                    return Result.error("请输入正确的手机号码");
                }
                manager.setShopId(shop.getId());
                manager.setSort(manager.getSort() == null ? 0 : manager.getSort());
                // 第一个管理人员设置为店长，其他为普通管理人员
                manager.setHeadManager(i == 0 ? 1 : 0);
                manager.setPhone(manager.getPhone());
                newManagers.add(manager);
            }
            shopManagerMapper.insertBatch(newManagers);
        }
            // 更新入驻申请表
            application.setApplyResult(param.getAuditStatus());
            application.setAuditUser(adminId);
            application.setAuditTime(LocalDateTime.now());
            application.setAuditPhone(auditPhone);
            application.setRejectReason(param.getRejectReason());
            settlementApplicationMapper.update(application);

        return Result.success(auditResult == 1 ? "审核通过成功" : "审核驳回成功");
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
        // 获取用户ID
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        param.setSearchField1(userId);
        List<SettlementApplicationVO> settlementApplicationVOs = settlementApplicationMapper.selectList(param);
//        settlementApplicationVOs = settlementApplicationVOs.stream()
//                .filter(vo -> {
//                    // 删除的商家需要过滤
//                    if (vo.getApplyResult()!=null&&vo.getApplyResult().equals(1)&&vo.getDeleteTime() != null) {
//                        return false;
//                    }
//                    return true;
//                }).collect(Collectors.toList());
        return Result.success("操作成功", new PageInfo<>(settlementApplicationVOs));
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
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectUnAuditById(settlementApplication.getShopId());
        //行业类别
        List<Integer> industryIds = industryCategoryShopMapper.selectIndustryIdsByShopId(settlementApplication.getShopId());
        //所属商圈
        List<Integer> circleIds = businessCircleShopMapper.selectCircleIdsByShopId(settlementApplication.getShopId());

        Map<String, Object> map = new HashMap<>();
        map.put("shop", shop);
        map.put("settlementApplication", settlementApplication);
        map.put("industryIds", industryIds);
        map.put("circleIds", circleIds);
        return Result.success("操作成功", map);
    }

    /**
     * 后台查询还未通过审核的商家
     *
     * @param param searchId 申请信息id
     * @param token token
     * @return result
     */
    @Override
    public Result getUnAuditShop(SearchParam param, String token) {
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        SettlementApplication settlementApplication = settlementApplicationMapper.selectById(param.getSearchId());
        if (settlementApplication == null) {
            return Result.error("参数错误");
        }
        // 查询商家
        Shop shop = shopMapper.selectUnAuditById(settlementApplication.getShopId());
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop,shopVO,"galleryImages");
        shopVO.setGalleryImages(JSON.parseArray(shop.getGalleryImages(), String.class));
        // 查询该店的商圈信息
        Integer id = shop.getId();
        List<Integer> shopIds = List.of(id);
        List<ShopCircleListVO> circleVoList = businessCircleMapper.selectByShopIds(shopIds);
        // 查询该店的行业类别信息
        List<ShopIndustryCategoryListVO> industryList = industryCategoryMapper.selectByShopIds(shopIds);
        // 查询该商家的管理人员
        List<ShopManager> shopManagerList = shopManagerMapper.selectByShopId(id);

        HashMap<String, Object> map = new HashMap<>();
        map.put("shop", shopVO);
        map.put("circleList", circleVoList);
        map.put("industryList", industryList);
        map.put("shopManagerList", shopManagerList);
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
                // 将商家信息返回
                Map<String, Object> shopMap = new HashMap<>();
                shopMap.put("shop", shop);
                shops.add(shopMap);
            }
        }
        // 返回商家列表
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("shops", shops);

        return Result.success("操作成功", resultMap);
    }

    @Override
    public Result getQualification(SearchParam param, String token) {
        Integer searchId = param.getSearchId();
        if(searchId==null){
            return Result.error("参数错误");
        }
        QualificationCert qualificationCert = qualificationCertMapper.selectByShopId(searchId);
        return Result.success("操作成功", qualificationCert);
    }

}
