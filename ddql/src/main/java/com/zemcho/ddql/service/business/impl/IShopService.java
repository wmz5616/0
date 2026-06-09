package com.zemcho.ddql.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthJwtData;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.business.param.ShopParam;
import com.zemcho.ddql.controller.business.vo.ShopCircleListVO;
import com.zemcho.ddql.controller.business.vo.ShopIndustryCategoryListVO;
import com.zemcho.ddql.controller.business.vo.ShopVO;
import com.zemcho.ddql.entity.business.BusinessCircleShop;
import com.zemcho.ddql.entity.business.IndustryCategoryShop;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.business.ShopManager;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.*;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.business.ShopManagerService;
import com.zemcho.ddql.service.business.ShopService;
import com.zemcho.ddql.util.wechat.WechatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IShopService implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;

    @Autowired
    private BusinessCircleMapper businessCircleMapper;

    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

    @Autowired
    private IndustryCategoryShopMapper industryCategoryShopMapper;

    @Autowired
    private ShopManagerMapper shopManagerMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopManagerService shopManagerService;

    /**
     * 新增/编辑店铺
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveShop(ShopParam param) {
        Integer id = param.getId();
        String name = param.getName();
        Shop shop = null;
        // 校验电话号码
        if (param.getPhone() != null && !"".equals(param.getPhone())
                && !param.getPhone().matches("^1[3-9]\\d{9}$")) {
            return Result.error("请输入正确的手机号码");
        }
        List<Integer> circleIds = param.getCircleIds();
//        if (circleIds == null || circleIds.isEmpty()) {
//            return Result.error("请选择商圈");
//        }
        if (param.getTopRecommend() != null && param.getTopRecommend().equals(1)) {
            if (param.getTopStartTime() == null || param.getTopEndTime() == null) {
                return Result.error("请选择置顶时间");
            }
            if (param.getTopStartTime().isAfter(param.getTopEndTime())) {
                return Result.error("置顶开始时间不能大于置顶结束时间");
            }
            if (!param.getTopEndTime().isAfter(LocalDateTime.now())) {
                return Result.error("置顶结束时间必须大于当前时间");
            }
        }
        if (param.getTopRecommend().equals(1)) {
            if (param.getTopStartTime() == null || param.getTopEndTime() == null) {
                return Result.error("请选择置顶时间");
            }
            if (param.getTopStartTime().isAfter(param.getTopEndTime())) {
                return Result.error("置顶开始时间不能大于置顶结束时间");
            }
            if (!param.getTopEndTime().isAfter(LocalDateTime.now())) {
                return Result.error("置顶结束时间必须大于当前时间");
            }
        }


        if (id == 0) {
            shop = shopMapper.selectByName(null, name);
            if (shop != null) {
                return Result.error("店铺名称已存在");
            }
            shop = new Shop();
            BeanUtils.copyProperties(param, shop, "galleryImages");
            if (param.getGalleryImages() != null && !param.getGalleryImages().isEmpty()) {
                shop.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
            }
            if(param.getStatus().equals(1)){
                //开启状态
                shop.setShopStatus(0);
                shop.setReceiptStatus(1);
            }else{
                //关闭状态
                shop.setShopStatus(1);
                shop.setReceiptStatus(0);
            }
            shop.setRate(0);
            shop.setCreateTime(LocalDateTime.now());
            shop.setUpdateTime(LocalDateTime.now());
            // 新增店铺
            shopMapper.insert(shop);
        } else {
            shop = shopMapper.selectByName(id, name);
            if (shop != null) {
                return Result.error("店铺名称已存在");
            }
            shop = shopMapper.selectById(id);
            if (shop == null) {
                return Result.error("店铺不存在");
            }
            BeanUtils.copyProperties(param, shop, "galleryImages");
            if (param.getGalleryImages() != null && !param.getGalleryImages().isEmpty()) {
                shop.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
            }
            if(param.getStatus().equals(1)){
                //开启状态
                shop.setShopStatus(0);
                shop.setReceiptStatus(1);
            }else{
                //关闭状态
                shop.setShopStatus(1);
                shop.setReceiptStatus(0);
            }
            // 更新店铺记录
            shopMapper.update(shop);
            // 删除行业类别关联记录
            industryCategoryShopMapper.deleteByShopIds(Collections.singletonList(shop.getId()));
            // 删除商圈店铺关联记录
            businessCircleShopMapper.deleteByShopIds(Collections.singletonList(shop.getId()));
        }

        // 新增商圈店铺关联
        if (circleIds != null && !circleIds.isEmpty()) {
            List<BusinessCircleShop> businessCircleShopList = new ArrayList<>();
            for (Integer circleId : circleIds) {
                BusinessCircleShop businessCircleShop = new BusinessCircleShop();
                businessCircleShop.setCircleId(circleId);
                businessCircleShop.setShopId(shop.getId());
                businessCircleShopList.add(businessCircleShop);
            }
            businessCircleShopMapper.insertBatch(businessCircleShopList);
        }
        // 新增商家 行业类别关联
        if (param.getIndustryCategoryIds() != null && !param.getIndustryCategoryIds().isEmpty()) {
            List<IndustryCategoryShop> industryCategoryShopList = new ArrayList<>();
            for (Integer industryCategoryId : param.getIndustryCategoryIds()) {
                IndustryCategoryShop industryCategoryShop = new IndustryCategoryShop();
                industryCategoryShop.setShopId(shop.getId());
                industryCategoryShop.setIndustryCategoryId(industryCategoryId);
                industryCategoryShopList.add(industryCategoryShop);
            }
            industryCategoryShopMapper.insertBatch(industryCategoryShopList);
        }

        return Result.success("操作成功",shop.getId());
    }

    /**
     * 删除店铺
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteShop(DeleteParam param) {
        shopMapper.deleteByIds(param.getDeleteIds());
        businessCircleShopMapper.deleteByShopIds(param.getDeleteIds());
        industryCategoryShopMapper.deleteByShopIds(new ArrayList<>(param.getDeleteIds()));
        return Result.success("操作成功");
    }

    /**
     * 获取店铺列表
     *
     * @param param
     * @return
     */
    @Override
    public Result selectList(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<ShopVO> shopVOList = shopMapper.selectList(param);
        if (shopVOList == null || shopVOList.isEmpty()) {
            return Result.success("操作成功", new PageInfo<>(shopVOList));
        }

        List<Integer> shopIds = shopVOList.stream().map(ShopVO::getId).toList();
        List<ShopCircleListVO> circleVOList = businessCircleMapper.selectByShopIds(shopIds);
        Map<Object, List<ShopCircleListVO>> circleVOMap =
                circleVOList.stream().collect(Collectors.groupingBy(ShopCircleListVO::getShopId));
        for (ShopVO item : shopVOList) {
            List<ShopCircleListVO> list =
                    Optional.ofNullable(circleVOMap.get(item.getId())).orElse(Collections.emptyList());
            List<String> circleNameList =
                    list.stream().map(ShopCircleListVO::getCircleName).collect(Collectors.toList());
            item.setCircleNameList(circleNameList);
        }

        PageInfo<ShopVO> pageInfo = new PageInfo<>(shopVOList);
        return Result.success("获取成功", pageInfo);
    }


    /**
     * 禁用/启用店铺 status:0禁用 1启用
     *
     * @param param
     * @return
     */
    @Override
    public Result updateStatus(ChangeOneParam param) {
        Shop shop = shopMapper.selectById(param.getChangeId());
        if (shop == null) {
            return Result.error("记录不存在");
        }
        // 启用状态禁用的不在小程序显示，且禁用收款配置，商家状态为禁用
        //禁用
        if (param.getStatus().equals(0)) {
            shop.setStatus(0);
            shop.setShopStatus(1);
            shop.setReceiptStatus(0);
        } else if (param.getStatus().equals(1)) {
            shop.setStatus(1);
            shop.setShopStatus(0);
            shop.setReceiptStatus(1);
        } else {
            return Result.error("参数错误");
        }
        shopMapper.update(shop);

        return Result.success("操作成功");
    }

    /**
     * 获取店铺详情
     *
     * @param param
     * @return
     */
    @Override
    public Result selectById(SearchParam param,Boolean isWechat,String token) {
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,param.getSearchId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("id不能为空");
        }
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            return Result.error("记录不存在");
        }
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO, "galleryImages");
        shopVO.setGalleryImages(JSON.parseArray(shop.getGalleryImages(), String.class));
        // 查询该店的商圈信息
        List<Integer> shopIds = List.of(id);
        List<ShopCircleListVO> circleVoList = businessCircleMapper.selectByShopIds(shopIds);
        // 查询该店的行业类别信息
        List<ShopIndustryCategoryListVO> industryList = industryCategoryMapper.selectByShopIds(shopIds);
        // 查询该商家的管理人员
        List<ShopManager> shopManagerList = shopManagerMapper.selectByShopId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("shop", shopVO);
        data.put("circleList", circleVoList);
        data.put("industryList", industryList);
        data.put("managerList", shopManagerList);
        return Result.success("查询成功", data);
    }

    @Override
    public Result updateTopConsumptionStatus(SearchParam param) {
        if (param.getSearchId() == null || param.getSearchType() == null) {
            return Result.error("参数错误");
        }
        if (param.getSearchType().equals(1) && (param.getStartTime() == null || param.getEndTime() == null)) {
            return Result.error("参数错误");
        }
        if (param.getSearchType().equals(1) && (param.getStartTime().isAfter(param.getEndTime()) || param.getStartTime().equals(param.getEndTime()))) {
            return Result.error("参数错误");
        }
        // 更新状态
        Integer shopId = param.getSearchId();
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.error("操作失败");
        }

        if (param.getSearchType().equals(1)) {
            shop.setTopConsumption(param.getSearchType());
            shop.setTopConsumptionStartTime(param.getStartTime());
            shop.setTopConsumptionEndTime(param.getEndTime());
        } else if (param.getSearchType().equals(0)) {
            shop.setTopConsumption(param.getSearchType());
        }
        shopMapper.update(shop);
        return Result.success("操作成功");
    }

    /**
     * 修改收款相关配置
     *
     * @param param searchId 商家ID searchType 目标状态 searchField1 绑定商户号 searchField2 抽成比例 单位百分之一
     * @return result
     */
    @Override
    public Result updateReceiptConfig(SearchParam param) {
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("参数错误");
        }
        // 如果门店是未开启 那麽收款只能关闭
        if (shop.getStatus().equals(0) && (param.getSearchType() != null && param.getSearchType().equals(1))) {
            return Result.error("请先启用该商家");
        }
        Shop updateShop = new Shop();
        updateShop.setId(param.getSearchId());
        updateShop.setReceiptStatus(param.getSearchType());
        updateShop.setMerchantId(param.getSearchField1());
        updateShop.setRate(param.getSearchField2());
        shopMapper.update(updateShop);

        return Result.success("操作成功");
    }

    @Override
    public Result updateContract(SearchParam param) {
        // 查找商家
        if (param.getSearchId() == null || param.getSearchStrField1() == null) {
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("参数错误");
        }
        shop.setContract(param.getSearchStrField1());
        shopMapper.update(shop);
        return Result.success("操作成功");
    }

    @Override
    public Result updateShopStatus(SearchParam param) {
        // 查找商家
        if (param.getSearchId() == null) {
            return Result.error("参数错误");
        }
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("该商家不存在");
        }
        shop.setShopStatus(0);//恢复正常状态
        shop.setStatus(1);
        shop.setReceiptStatus(1);
        shopMapper.update(shop);
        return Result.success("操作成功");
    }

    @Override
    public Result generateQrCode(SearchParam param,String token,Boolean isWechat) {
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,param.getSearchId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        if (param.getSearchId() == null) {
            return Result.error("店铺ID不能为空");
        }

        // 查询店铺信息
        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("店铺不存在");
        }


        try {
            // 使用微信工具类生成小程序码，以店铺ID作为场景值
            String scene = String.valueOf(shop.getId()); // 场景值，用于传递店铺ID
            String page = "pages/scanToPay/scanToPay"; // 跳转到店铺详情页
            Integer width = 430; // 二维码宽度
            String envVersion = null; // 环境版本，正式版为null

            byte[] qrCodeBytes = com.zemcho.ddql.util.wechat.WechatUtil.createUnlimitedQrCode(scene, page, width, envVersion);

            if (qrCodeBytes == null) {
                return Result.error("生成二维码失败");
            }

            // 将字节数组转换为Base64字符串，便于前端展示或下载
            String base64Image = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(qrCodeBytes);

            Map<String, Object> result = new HashMap<>();
            result.put("qrCode", base64Image);
            result.put("shopName", shop.getName());
            result.put("shopId", shop.getId());

            return Result.success("生成二维码成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("生成二维码异常：" + e.getMessage());
        }
    }

    @Override
    public Result downloadQrCode(SearchParam param,String token,Boolean isWechat) {
        //小程序入口
        if (isWechat) {
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token,param.getSearchId());
            if (!checkResult.success()) {
                return checkResult;
            }
        }
        if (param.getSearchId() == null) {
            return Result.error("店铺ID不能为空");
        }

        Shop shop = shopMapper.selectById(param.getSearchId());
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        try {
            Integer scene =  shop.getId();
            String page = "pages/scanToPay/scanToPay";
            Integer width = 430;
            String envVersion = null;

            byte[] qrCodeBytes = WechatUtil.createUnlimitedQrCode(String.valueOf(scene), page, width, envVersion);

            if (qrCodeBytes == null) {
                return Result.error("生成二维码失败");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("imageBytes", qrCodeBytes);
            result.put("fileName", shop.getName() + "_二维码.png");
            result.put("contentType", "image/png");

            return Result.success("获取二维码成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取二维码异常：" + e.getMessage());
        }

    }

}
