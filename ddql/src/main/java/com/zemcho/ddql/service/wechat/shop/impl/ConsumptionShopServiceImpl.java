package com.zemcho.ddql.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.vo.ShopIndustryCategoryListVO;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopDetailParam;
import com.zemcho.ddql.controller.wechat.shop.vo.CoinRuleVO;
import com.zemcho.ddql.controller.wechat.shop.vo.ConsumptionShopVO;
import com.zemcho.ddql.controller.wechat.shop.vo.ShopDetailVO;
import com.zemcho.ddql.entity.business.CoinRule;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.mapper.business.BusinessCircleMapper;
import com.zemcho.ddql.controller.business.vo.ShopCircleListVO;
import com.zemcho.ddql.mapper.business.CoinRuleMapper;
import com.zemcho.ddql.mapper.business.IndustryCategoryMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.service.wechat.shop.ConsumptionShopService;
import com.zemcho.ddql.util.location.DistanceCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class ConsumptionShopServiceImpl implements ConsumptionShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private CoinRuleMapper coinRuleMapper;

    @Autowired
    private IndustryCategoryMapper industryCategoryMapper;

    @Autowired
    private BusinessCircleMapper businessCircleMapper;

    /**
     * 小程序消费门店列表 要求：按距离排序。
     *
     * @param param param keyword searchId 行业类别 输入 0 代表全部
     * @param token token
     * @return result
     */
    @Override
    public Result selectList(SearchParam param, String token) {
        Integer cateId = param.getSearchId();
        if (cateId == null) {
            return Result.error("参数错误");
        }
        if(cateId == 0){
            param.setSearchId(null);
        }
        if (param.getSearchStrField1() == null || param.getSearchStrField2() == null) {
            return Result.error("参数错误");
        }

        // 1. 查询置顶商家（用于轮播）
        List<ConsumptionShopVO> topShops = shopMapper.selectTopConsumptionShops(LocalDateTime.now());

        // 2. 查询所有商家（用于列表展示）
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<ConsumptionShopVO> allShops = shopMapper.selectConsumptionShopList(param);

        // 3. 计算距离并排序
        allShops = allShops.stream()
                .peek(vo -> {
                    try {
                        String[] arr = vo.getLocation().split(",");
                        double lng = Double.parseDouble(arr[0]);
                        double lat = Double.parseDouble(arr[1]);
                        double d = DistanceCalculator.getDistance(Double.parseDouble(param.getSearchStrField1()),
                                Double.parseDouble(param.getSearchStrField2()), lng, lat);
                        vo.setDistance(d);
                    } catch (NumberFormatException e) {
                        log.error("计算距离失败，参数：{}", param, e);
                    }
                })
                .sorted(Comparator.comparingDouble(ConsumptionShopVO::getDistance))
                .toList();

        // 4. 置顶商家也计算距离
        if (topShops != null && !topShops.isEmpty()) {
            topShops.forEach(vo -> {
                try {
                    String[] arr = vo.getLocation().split(",");
                    double lng = Double.parseDouble(arr[0]);
                    double lat = Double.parseDouble(arr[1]);
                    double d = DistanceCalculator.getDistance(Double.parseDouble(param.getSearchStrField1()),
                            Double.parseDouble(param.getSearchStrField2()), lng, lat);
                    vo.setDistance(d);
                } catch (NumberFormatException e) {
                    log.error("计算置顶商家距离失败", e);
                }
            });
        }

        // 5. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("topShops", topShops);
        result.put("shopList", new PageInfo<>(allShops));

        return Result.success("操作成功", result);
    }

    /**
     * 获取商家详情（包含用币规则）
     *
     * @param param 请求参数
     * @return 商家详情
     */
    @Override
    public Result getShopDetail(WechatShopDetailParam param) {
        Integer shopId = param.getShopId();

        // 查询商家基本信息
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.error("商家不存在");
        }

        // 组装商家详情VO
        ShopDetailVO shopDetailVO = new ShopDetailVO();
        shopDetailVO.setId(shop.getId());
        shopDetailVO.setName(shop.getName());
        shopDetailVO.setCoverImageUrl(shop.getCoverImageUrl());
        shopDetailVO.setAddress(shop.getAddress());
        shopDetailVO.setPhone(shop.getPhone());
        shopDetailVO.setStartTime(shop.getStartTime());
        shopDetailVO.setEndTime(shop.getEndTime());
        shopDetailVO.setDescription(shop.getDescription());
        shopDetailVO.setClickCount(shop.getClickCount());
        shopDetailVO.setCreateTime(shop.getCreateTime());

        // 处理轮播图（JSON字符串转列表）
        if (shop.getGalleryImages() != null && !shop.getGalleryImages().isEmpty()) {
            shopDetailVO.setGalleryImages(JSON.parseArray(shop.getGalleryImages(), String.class));
        } else {
            shopDetailVO.setGalleryImages(Collections.emptyList());
        }

        // 查询行业类别名称
        List<ShopIndustryCategoryListVO> categoryList = industryCategoryMapper.selectByShopIds(Collections.singletonList(shopId));
        if (categoryList != null && !categoryList.isEmpty()) {
            shopDetailVO.setIndustryCategoryName(categoryList.get(0).getIndustryCategoryName());
        }

        // 查询所属商圈名称
        List<ShopCircleListVO> circleList = businessCircleMapper.selectByShopIds(Collections.singletonList(shopId));
        if (circleList != null && !circleList.isEmpty()) {
            shopDetailVO.setCircleName(circleList.get(0).getCircleName());
        }

        // 查询用币规则
        CoinRule coinRule = coinRuleMapper.selectByShopId(shopId);
        if (coinRule != null) {
            CoinRuleVO coinRuleVO = new CoinRuleVO();
            coinRuleVO.setBeginAmount(coinRule.getBeginAmount());
            coinRuleVO.setThreshold(coinRule.getThreshold());
            coinRuleVO.setDeduct(coinRule.getDeduct());
            coinRuleVO.setMaxDeduct(coinRule.getMaxDeduct());
            coinRuleVO.setRemark(coinRule.getRemark());
            shopDetailVO.setCoinRule(coinRuleVO);
        }

        return Result.success("操作成功", shopDetailVO);
    }
}
