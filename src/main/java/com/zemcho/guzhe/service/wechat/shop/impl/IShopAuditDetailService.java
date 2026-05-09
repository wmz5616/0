package com.zemcho.guzhe.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.shop.param.ShopUpdateParam;
import com.zemcho.guzhe.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.guzhe.controller.wechat.shop.vo.ShopAuditDetailVO;
import com.zemcho.guzhe.controller.wechat.shop.vo.ShopDetailVO;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderCountVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.shop.*;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.order.OrderMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.mapper.shop.BusinessCircleShopMapper;
import com.zemcho.guzhe.mapper.shop.IndustryCategoryMapper;
import com.zemcho.guzhe.mapper.shop.IndustryCategoryShopMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.wechat.shop.ShopAuditDetailService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HXH
 */
@Service
public class IShopAuditDetailService implements ShopAuditDetailService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CasUserMapper casUserMapper;
    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;
    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;
    @Autowired
    private IndustryCategoryShopMapper industryCategoryShopMapper;

    @Override
    public Result getBusinessData(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("参数错误");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        Integer searchField4 = param.getSearchField4();
        if(searchField4 == null){
            return new Result(10002, "参数错误");
        }
        if(shopMapper.selectById(searchField4) == null){
            return new Result(10002, "该商家不存在");
        }
        if (param.getStartTime() == null || param.getEndTime() == null) {
            LocalDate today = LocalDate.now();
            param.setStartTime(today.atStartOfDay());
            param.setEndTime(today.atTime(LocalTime.MAX));
        }
        //统计数据
        BusinessDataVO vo = orderMapper.selectBusinessData(param);
        if (vo == null) {
            vo = new BusinessDataVO();
            vo.setOrderCount(0);
            vo.setRevenue(BigDecimal.ZERO);
            vo.setPendingAmount(BigDecimal.ZERO);
        }

        if (vo.getRevenue() == null) {
            vo.setRevenue(BigDecimal.ZERO);
        }
        if (vo.getPendingAmount() == null) {
            vo.setPendingAmount(BigDecimal.ZERO);
        }

        vo.setRevenue(vo.getRevenue().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        vo.setPendingAmount(vo.getPendingAmount().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));

        return Result.success("查询成功", vo);
    }

    @Override
    public Result getShopInfo(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        Integer searchId = param.getSearchId();
        if (searchId == null) {
            return Result.error("参数错误");
        }

        Shop shop = shopMapper.selectById(searchId);
        if (shop == null) {
            return Result.error("商家不存在");
        }

        if (shop.getStatus() == null || shop.getStatus() == 0) {
            return Result.error("商家未启用");
        }

        if (shop.getShopStatus() != null && shop.getShopStatus() == 2) {
            return Result.error("商家已注销");
        }

        ShopDetailVO vo = new ShopDetailVO();
        BeanUtils.copyProperties(shop, vo);

        if (shop.getGalleryImages() != null && !shop.getGalleryImages().isEmpty()) {
            try {
                List<String> images = JSON.parseArray(shop.getGalleryImages(), String.class);
                vo.setGalleryImages(images);
            } catch (Exception e) {
                vo.setGalleryImages(new ArrayList<>());
            }
        }
        //关联行业类别
        List<Integer> industryIds = industryCategoryShopMapper.selectIndustryIdsByShopId(searchId);
        if(industryIds != null &&!industryIds.isEmpty()){
            List<IndustryCategory> industries = industryCategoryMapper.selectByIds(industryIds);
            vo.setIndustryCategories(industries != null ? industries : new ArrayList<>());
        }else{
            vo.setIndustryCategories(new ArrayList<>());
        }

        //关联商超名字
        List<Integer> circleIds = businessCircleShopMapper.selectCircleIdsByShopId(searchId);
        if (circleIds != null && !circleIds.isEmpty()) {
            List<String> supermarketNames = circleIds.stream()
                    .map(circleId -> businessCircleShopMapper.select(circleId))
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            vo.setSupermarketNames(supermarketNames);
        } else {
            vo.setSupermarketNames(new ArrayList<>());
        }
        return Result.success("查询成功", vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(ShopUpdateParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return Result.error("token无效");
        }

        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }

        Integer shopId = param.getId();
        if (shopId == null) {
            return Result.error("参数错误");
        }

        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.error("商家不存在");
        }

        Shop updateShop = new Shop();
        BeanUtils.copyProperties(param, updateShop);
        updateShop.setTopRecommend(0);

        if (param.getGalleryImages() != null && !param.getGalleryImages().isEmpty()) {
            updateShop.setGalleryImages(JSON.toJSONString(param.getGalleryImages()));
        }

        shopMapper.update(updateShop);

        industryCategoryShopMapper.deleteByShopIds(List.of(shopId));
        if (param.getIndustryCategoryIds() != null && !param.getIndustryCategoryIds().isEmpty()) {
            List<IndustryCategoryShop> industryShops = param.getIndustryCategoryIds().stream()
                    .map(industryId -> {
                        IndustryCategoryShop ics = new IndustryCategoryShop();
                        ics.setIndustryCategoryId(industryId);
                        ics.setShopId(shopId);
                        return ics;
                    })
                    .collect(Collectors.toList());
            industryCategoryShopMapper.insertBatch(industryShops);
        }

        businessCircleShopMapper.deleteByShopIds(List.of(shopId));
        if (param.getCircleIds() != null && !param.getCircleIds().isEmpty()) {
            List<BusinessCircleShop> circleShops = param.getCircleIds().stream()
                    .map(circleId -> {
                        BusinessCircleShop bcs = new BusinessCircleShop();
                        bcs.setCircleId(circleId);
                        bcs.setShopId(shopId);
                        return bcs;
                    })
                    .collect(Collectors.toList());
            businessCircleShopMapper.insertBatch(circleShops);
        }

        return Result.success("操作成功");
    }

}
