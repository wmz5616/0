package com.zemcho.guzhe.service.merchant.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.config.tgy_pay.MerchantConfig;
import com.zemcho.guzhe.controller.merchant.param.MerchantSaveParam;
import com.zemcho.guzhe.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.guzhe.controller.merchant.vo.AddMerchantVO;
import com.zemcho.guzhe.controller.merchant.vo.MerchantListVO;
import com.zemcho.guzhe.controller.merchant.vo.ShopMerchantVO;
import com.zemcho.guzhe.entity.merchant.*;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.merchant.MerchantMapper;
import com.zemcho.guzhe.service.merchant.MerchantService;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.tgy.TgyPayUtil;
import com.zemcho.guzhe.util.tgy.dto.MerchantRequestJson;
import com.zemcho.guzhe.util.tgy.dto.MerchantResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zemcho.guzhe.util.tgy.dto.MerchantRequestJson.MerchantToMerchantRequestJson;

@Service
public class IMerchantService implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ShopMapper shopMapper;

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
                Result queryResult = null;
                tgyPayUtil.queryMerchantChannel(merchant.getRequestNo());
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

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
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

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(param, merchant);

        // 设置主商户编号
        merchant.setParentChannelMerchantNo(merchantConfig.getParentChannelMerchantNo());

        if (status == 2) { //提交
            // 检查参数
            String checkResult = check(merchant);
            if (!checkResult.equals("")) {
                return Result.error(checkResult);
            }

            // 将Merchant转为请求需要的MerchantRequestJson
            MerchantRequestJson merchantRequestJson = MerchantToMerchantRequestJson(merchant);

            // 请求添加进件
            Result addResult = null;
            tgyPayUtil.addMerchantChannel(merchantRequestJson);
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

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
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

        // 设置主商户编号
        merchant.setParentChannelMerchantNo(merchantConfig.getParentChannelMerchantNo());

        if (status == 2) { //提交
            // 检查参数
            String checkResult = check(merchant);
            if (!checkResult.equals("")) {
                return Result.error(checkResult);
            }

            // 将Merchant转为请求需要的MerchantRequestJson
            MerchantRequestJson merchantRequestJson = MerchantToMerchantRequestJson(merchant);

            // 请求添加进件
            Result addResult = null;
            tgyPayUtil.addMerchantChannel(merchantRequestJson);
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
    public Result selectById(Integer id, String token, Boolean isWechat, Integer shopId) {
        Merchant merchantInfo = merchantMapper.selectById(id);
        if (merchantInfo == null) {
            return Result.error("商户不存在");
        }

        if (isWechat) { //小程序端进入的
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
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

    private String check(Merchant merchant) {
        if (merchant.getOperationType() == null) {
            return "操作类型不能为空";
        }
        if (merchant.getMerchantName() == null || merchant.getContactPhone() == null || merchant.getEmail() == null) {
            return "商户名称、联系人电话、邮箱不能为空";
        }
        if (merchant.getCardName() == null || merchant.getCardNo() == null || merchant.getCardMobile() == null
                || merchant.getCardBeginDate() == null || merchant.getCardEndDate() == null) {
            return "银行卡名称、银行卡号、银行卡手机、银行卡开始时间、银行卡结束时间不能为空";
        }
        if (merchant.getStoreName() == null || merchant.getStoreProvince() == null || merchant.getStoreCity() == null
                || merchant.getStoreCounty() == null || merchant.getStoreAddr() == null) {
            return "门店名称、门店省、门店市、门店县、门店地址不能为空";
        }
        if (merchant.getOperationType().equals("1") && (merchant.getBusinessName() == null
                || merchant.getBusinessNo() == null || merchant.getMainType() == null || merchant.getLegalPerson() == null
                || merchant.getBusinessProvince() == null || merchant.getBusinessCity() == null
                || merchant.getBusinessCounty() == null || merchant.getBusinessAddr() == null
                || merchant.getBusinessBeginDate() == null || merchant.getBusinessEndDate() == null)) {
            return "操作类型为1时营业信息不能为空";
        }
        if (merchant.getOperationType().equals("1") && merchant.getAccType() == null) {
            return "操作类型为1时账户类型不能为空";
        }
        if (merchant.getAccCardNo() == null || merchant.getAccMobile() == null || merchant.getAccName() == null
                || merchant.getBank() == null || merchant.getBankName() == null || merchant.getBankLinkNo() == null
                || merchant.getBankBranch() == null) {
            return "账户信息不能为空";
        }
        if (merchant.getParentChannelMerchantNo() == null) {
            return "主商户号不能为空";
        }
        if (merchant.getLegalLicenceFrontUrlLocal() == null || merchant.getLegalLicenceBackUrlLocal() == null) {
            return "法人证件照片不能为空";
        }
        if (merchant.getOperationType().equals("1") && merchant.getLicenceUrlLocal() == null) {
            return "操作类型为1时营业执照不能为空";
        }
        if (merchant.getOperationType().equals("1") && merchant.getOpenAccountLicenceUrlLocal() == null) {
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
