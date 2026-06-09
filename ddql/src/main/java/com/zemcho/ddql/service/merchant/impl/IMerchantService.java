package com.zemcho.ddql.service.merchant.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.tgy_pay.MerchantConfig;
import com.zemcho.ddql.controller.merchant.param.MerchantSaveParam;
import com.zemcho.ddql.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.ddql.controller.merchant.vo.AddMerchantVO;
import com.zemcho.ddql.controller.merchant.vo.MerchantEnabledVO;
import com.zemcho.ddql.controller.merchant.vo.MerchantListVO;
import com.zemcho.ddql.controller.merchant.vo.ShopMerchantVO;
import com.zemcho.ddql.entity.merchant.*;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.business.ShopManager;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.business.ShopManagerMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.merchant.MerchantMapper;
import com.zemcho.ddql.service.merchant.MerchantService;
import com.zemcho.ddql.service.business.ShopManagerService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.tgy.TgyPayUtil;
import com.zemcho.ddql.util.tgy.dto.MerchantRequestJson;
import com.zemcho.ddql.util.tgy.dto.MerchantResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zemcho.ddql.util.tgy.dto.MerchantRequestJson.MerchantToMerchantRequestJson;

@Service
public class IMerchantService implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopManagerMapper shopManagerMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Autowired
    private MerchantConfig merchantConfig;

    @Autowired
    private ShopManagerService shopManagerService;

    @Override
    public void checkProcess() {
        // 查出所有审核中的商户
        SearchParam param = new SearchParam();
        param.setSearchIntStatus(2);
        List<MerchantListVO> list = merchantMapper.selectList(param);
        if (list == null || list.isEmpty()) {
            return;
        }

        // 开始逐个请求并检查
        try {
            for (MerchantListVO merchant : list) {
                Result queryResult = tgyPayUtil.queryMerchantChannel(merchant.getRequestNo());
                if (!queryResult.success()) {
                    System.out.println(merchant.getRequestNo() + " 查询进件结果失败 : " + queryResult.getMsg());
                    continue;
                }

                MerchantResponseData queryData = (MerchantResponseData) queryResult.getData();

                Merchant merchantUpdate = new Merchant();
                merchantUpdate.setId(merchant.getId());
                if (queryData.getApplicationStatus().equals("COMPLETED")) {
                    merchantUpdate.setStatus(1);
                } else if (queryData.getApplicationStatus().equals("REVIEW_BACK")) {
                    merchantUpdate.setStatus(3);
                }
                // 复制属性
                merchantUpdate.setRequestNo(queryData.getRequestNo());
                merchantUpdate.setApplicationNo(queryData.getApplicationNo());
                merchantUpdate.setApplicationStatus(queryData.getApplicationStatus());
                merchantUpdate.setAuditOpinion(queryData.getAuditOpinion());
                merchantMapper.updateById(merchantUpdate);
            }
        } catch (Exception e) {
            System.out.println("查询进件结果失败");
            e.printStackTrace();
        }
    }

    /**
     * 商户图片上传
     *
     * @param param
     * @return
     */
    @Override
    public Result uploadMerchantImage(UploadMerchantImageParam param) {
        try {
            return tgyPayUtil.uploadMerchantImage(param.getFileStr());
        } catch (Exception e) {
            System.out.println("上传文件失败");
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
    }

    // 添加进件的方法 仅第一次添加用
    @Override
    @Transactional
    public Result add(MerchantSaveParam param, String token, Boolean isWechat) {
        Integer status = param.getStatus();
        if (status == null || (status != 4 && status != 2)) {
            return Result.error("操作类型错误");
        }

        Boolean updateShopManager = false;
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getShopId();
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }

            Shop shopInfo = shopMapper.selectById(shopId);
            if (shopInfo == null) {
                return Result.error("商家不存在");
            }
            if (shopInfo.getMerchantId() == null || shopInfo.getMerchantId() == 0) {
                updateShopManager = true;
            }

            Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
            param.setUserId(userId);
        }

        try {
            Merchant merchant = new Merchant();
            BeanUtils.copyProperties(param, merchant);

            merchant.setLegalLicenceFrontUrl(merchant.getLegalLicenceFrontUrlLocal());
            merchant.setLegalLicenceBackUrl(merchant.getLegalLicenceBackUrlLocal());
            merchant.setLicenceUrl(merchant.getLicenceUrlLocal());
            merchant.setOpenAccountLicenceUrl(merchant.getOpenAccountLicenceUrlLocal());

            // 设置主商户编号(如果配置中存在的话才覆盖，否则使用前端传来的值)
            if (StringUtils.hasText(merchantConfig.getParentChannelMerchantNo())) {
                merchant.setParentChannelMerchantNo(merchantConfig.getParentChannelMerchantNo());
            }

            if (status == 2) { //提交
                // 检查参数
                String checkResult = check(merchant);
                if (!checkResult.equals("")) {
                    return Result.error(checkResult);
                }

                // 将Merchant转为请求需要的MerchantRequestJson
                MerchantRequestJson merchantRequestJson = MerchantToMerchantRequestJson(merchant);

                // 请求添加进件 (为本地测试 mock 掉通莞接口)
                MerchantResponseData mockData = new MerchantResponseData();
                mockData.setMerchantNo("mock_merchant_no");
                mockData.setRequestNo("mock_request_no");
                mockData.setApplicationNo("mock_application_no");
                mockData.setApplicationStatus("REVIEWING");
                mockData.setAuditOpinion("Mocked for local test");
                Result addResult = Result.success("success", mockData);

                if (!addResult.success()) {
                    return addResult;
                }
                MerchantResponseData resultData = (MerchantResponseData) addResult.getData();

                // 复制参数到实体中并插入数据库
                merchant.setMerchantNo(resultData.getMerchantNo());
                merchant.setRequestNo(resultData.getRequestNo());
                merchant.setApplicationNo(resultData.getApplicationNo());
                merchant.setApplicationStatus(resultData.getApplicationStatus());
                merchant.setAuditOpinion(resultData.getAuditOpinion());
            }

            // 设置状态为审核中 后续在确认申请结果前不能修改
            merchant.setStatus(status);

            merchant.setCreateTime(LocalDateTime.now());
            merchantMapper.insert(merchant);

            if (updateShopManager) {
                Shop updateShop = new Shop();
                updateShop.setId(param.getShopId());
                updateShop.setMerchantId(merchant.getId());
                shopMapper.update(updateShop);
            }

            AddMerchantVO addMerchantVO = new AddMerchantVO();
            addMerchantVO.setApplicationNo(merchant.getApplicationNo());
            addMerchantVO.setApplicationStatus(merchant.getApplicationStatus());

            return Result.success("添加成功", addMerchantVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Debug Exception: " + e.toString());
        }
    }

    @Override
    public Result update(MerchantSaveParam param, String token, Boolean isWechat) {
        Integer status = param.getStatus();
        if (status == null || (status != 4 && status != 2)) {
            return Result.error("操作类型错误");
        }

        if (param.getId() == null) {
            return Result.error("商户ID不能为空");
        }
        Merchant oldMerchant = merchantMapper.selectById(param.getId());
        if (oldMerchant == null) {
            return Result.error("商户不存在");
        }
        if (!oldMerchant.getStatus().equals(3) && !oldMerchant.getStatus().equals(4)) {
            return Result.error("该商户当前状态不可修改信息");
        }

        if (isWechat) { //小程序端进入的
            Integer shopId = param.getShopId();
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }

            Shop shopInfo = shopMapper.selectById(shopId);
            if (shopInfo == null) {
                return Result.error("商家不存在");
            }
            if (shopInfo.getMerchantId() == null || !shopInfo.getMerchantId().equals(param.getId())) {
                return Result.error("不可修改该商户信息");
            }
        }

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(param, merchant);

        merchant.setLegalLicenceFrontUrl(merchant.getLegalLicenceFrontUrlLocal());
        merchant.setLegalLicenceBackUrl(merchant.getLegalLicenceBackUrlLocal());
        merchant.setLicenceUrl(merchant.getLicenceUrlLocal());
        merchant.setOpenAccountLicenceUrl(merchant.getOpenAccountLicenceUrlLocal());

        // 设置主商户编号(如果配置中存在的话才覆盖，否则使用前端传来的值)
        if (StringUtils.hasText(merchantConfig.getParentChannelMerchantNo())) {
            merchant.setParentChannelMerchantNo(merchantConfig.getParentChannelMerchantNo());
        }

        if (status == 2) { //提交
            // 检查参数
            String checkResult = check(merchant);
            if (!checkResult.equals("")) {
                return Result.error(checkResult);
            }

            // 将Merchant转为请求需要的MerchantRequestJson
            MerchantRequestJson merchantRequestJson = MerchantToMerchantRequestJson(merchant);

            // 请求添加进件 (为本地测试 mock 掉通莞接口)
            MerchantResponseData mockData = new MerchantResponseData();
            mockData.setMerchantNo("mock_merchant_no");
            mockData.setRequestNo("mock_request_no");
            mockData.setApplicationNo("mock_application_no");
            mockData.setApplicationStatus("REVIEWING");
            mockData.setAuditOpinion("Mocked for local test");
            Result addResult = Result.success("success", mockData);

            if (!addResult.success()) {
                return addResult;
            }
            MerchantResponseData resultData = (MerchantResponseData) addResult.getData();

            // 复制参数到实体中并插入数据库
            merchant.setMerchantNo(resultData.getMerchantNo());
            merchant.setRequestNo(resultData.getRequestNo());
            merchant.setApplicationNo(resultData.getApplicationNo());
            merchant.setApplicationStatus(resultData.getApplicationStatus());
            merchant.setAuditOpinion(resultData.getAuditOpinion());
        }

        // 设置状态为审核中 后续在确认申请结果前不能修改
        merchant.setStatus(status);
        merchant.setCreateTime(LocalDateTime.now());
        merchantMapper.updateById(merchant);

        return Result.success("修改成功");
    }

    @Override
    public Result delete(Integer id) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            return Result.error("商户不存在");
        }
        if (!merchant.getStatus().equals(0)) {
            return Result.error("商户不能删除");
        }

        Merchant merchantDelete = new Merchant();
        merchantDelete.setId(id);
        merchantDelete.setDeleteTime(LocalDateTime.now());
        merchantMapper.updateById(merchantDelete);

        return Result.success("删除成功");
    }

    @Override
    public Result select(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<MerchantListVO> list = merchantMapper.selectList(param);
        if (list == null || list.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>(new ArrayList<>()));
        }
        // 查询商户列表
        List<Integer> merchantIds = list.stream().map(Merchant::getId).toList();
        List<ShopMerchantVO> merchantShopList = shopMapper.selectByMerchantId(merchantIds);
        // 分类
        Map<Integer, List<ShopMerchantVO>> map = merchantShopList.stream().collect(Collectors.groupingBy(ShopMerchantVO::getMerchantId));
        for (MerchantListVO merchant : list) {
            merchant.setShopListMerchantVO(map.getOrDefault(merchant.getId(), new ArrayList<>()));
        }

        PageInfo<MerchantListVO> pageInfo = new PageInfo<>(list);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result selectEnabledList() {
        List<MerchantEnabledVO> list = merchantMapper.selectEnabledList();
        return Result.success("获取成功", list);
    }

    @Override
    public Result selectById(Integer id, String token, Boolean isWechat, Integer shopId) {
        Merchant merchantInfo = merchantMapper.selectById(id);
        if (merchantInfo == null) {
            return Result.error("商户不存在");
        }

        if (isWechat) { //小程序端进入的
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }

            Shop shopInfo = shopMapper.selectById(shopId);
            if (shopInfo == null) {
                return Result.error("商家不存在");
            }
            if (shopInfo.getMerchantId() == null || !shopInfo.getMerchantId().equals(id)) {
                return Result.error("您无权查看该商户信息");
            }
        }

        return Result.success("操作成功", merchantInfo);
    }

    private Result checkWechatUserIsShopManager(String token, Integer shopId) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        List<ShopManager> shopManagers = shopManagerMapper.selectByShopId(shopId);
        ShopManager matchedManager = null;
        if (shopManagers != null) {
            for (ShopManager manager : shopManagers) {
                if (manager.getPhone() != null && manager.getPhone().equals(userInfo.getPhone())) {
                    matchedManager = manager;
                    break;
                }
            }
        }
        if (matchedManager == null) {
            return Result.error("您不是该商家的管理者");
        }

        return Result.success("验证通过", matchedManager);
    }

    private String check(Merchant merchant) {
        if (!StringUtils.hasText(merchant.getOperationType())) {
            return "操作类型不能为空";
        }
        if (!StringUtils.hasText(merchant.getMerchantName()) || !StringUtils.hasText(merchant.getContactPhone()) || !StringUtils.hasText(merchant.getEmail())) {
            return "商户名称、联系人电话、邮箱不能为空";
        }
        if (!StringUtils.hasText(merchant.getCardName()) || !StringUtils.hasText(merchant.getCardNo()) || !StringUtils.hasText(merchant.getCardMobile())
                || !StringUtils.hasText(merchant.getCardBeginDate()) || !StringUtils.hasText(merchant.getCardEndDate())) {
            return "法人身份证名称、身份证号、手机号、有效期开始时间、有效期结束时间不能为空";
        }
        if (!StringUtils.hasText(merchant.getStoreName()) || !StringUtils.hasText(merchant.getStoreProvince()) || !StringUtils.hasText(merchant.getStoreCity())
                || !StringUtils.hasText(merchant.getStoreCounty()) || !StringUtils.hasText(merchant.getStoreAddr())) {
            return "门店名称、门店省、门店市、门店县、门店地址不能为空";
        }
        if (merchant.getOperationType().equals("1") && (!StringUtils.hasText(merchant.getBusinessName())
                || !StringUtils.hasText(merchant.getBusinessNo()) || merchant.getMainType() == null || merchant.getMainType() == 0 || !StringUtils.hasText(merchant.getLegalPerson())
                || !StringUtils.hasText(merchant.getBusinessProvince()) || !StringUtils.hasText(merchant.getBusinessCity())
                || !StringUtils.hasText(merchant.getBusinessCounty()) || !StringUtils.hasText(merchant.getBusinessAddr())
                || !StringUtils.hasText(merchant.getBusinessBeginDate()) || !StringUtils.hasText(merchant.getBusinessEndDate()))) {
            return "操作类型为1时营业信息不能为空";
        }
        if (merchant.getOperationType().equals("1") && (merchant.getAccType() == null || merchant.getAccType() == 0)) {
            return "操作类型为1时账户类型不能为空";
        }
        if (!StringUtils.hasText(merchant.getAccCardNo()) || !StringUtils.hasText(merchant.getAccMobile()) || !StringUtils.hasText(merchant.getAccName())
                || !StringUtils.hasText(merchant.getBank()) || !StringUtils.hasText(merchant.getBankName()) || !StringUtils.hasText(merchant.getBankLinkNo())
                || !StringUtils.hasText(merchant.getBankBranch())) {
            return "账户信息不能为空";
        }
        if (!StringUtils.hasText(merchant.getParentChannelMerchantNo())) {
            return "主商户号不能为空";
        }
        if (!StringUtils.hasText(merchant.getLegalLicenceFrontUrlLocal()) || !StringUtils.hasText(merchant.getLegalLicenceBackUrlLocal())) {
            return "法人证件照片不能为空";
        }
        if (merchant.getOperationType().equals("1") && !StringUtils.hasText(merchant.getLicenceUrlLocal())) {
            return "操作类型为1时营业执照不能为空";
        }
        if (merchant.getOperationType().equals("1") && !StringUtils.hasText(merchant.getOpenAccountLicenceUrlLocal())) {
            return "操作类型为1时开户许可证不能为空";
        }
        if (merchant.getLoginAccount() == null) {
            return "登录账号不能为空";
        }
        return "";
    }

    /**
     * 更改启用状态
     *
     * @param param
     * @return
     */
    @Override
    public Result setStatus(SearchParam param) {
        List<Integer> searchIds = param.getSearchIds();
        Integer status = param.getSearchIntStatus();

        if (searchIds == null || searchIds.isEmpty() || status == null) {
            return Result.error("参数错误");
        }

        SearchParam checkParam = new SearchParam();
        checkParam.setSearchIds(searchIds);
        checkParam.setSearchStatusList(Arrays.asList(0, 1));
        List<MerchantListVO> list = merchantMapper.selectList(checkParam);
        if (list == null || list.size() != searchIds.size()) {
            return Result.error("所选记录不可修改启用状态");
        }

        merchantMapper.updateStatusByIds(searchIds, status);

        return Result.success("操作成功");
    }
}
