package com.zemcho.ddql.service.wechat.shop.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.business.vo.ShopCircleListVO;
import com.zemcho.ddql.controller.business.vo.ShopVO;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopParam;
import com.zemcho.ddql.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.business.BusinessCircleMapper;
import com.zemcho.ddql.mapper.business.ShopManagerMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderRefundApplyMapper;
import com.zemcho.ddql.mapper.order.ShopOrderMapper;
import com.zemcho.ddql.service.business.ShopManagerService;
import com.zemcho.ddql.service.wechat.shop.WechatShopService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.location.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IWechatShopService implements WechatShopService {
    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private BusinessCircleMapper businessCircleMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ExchangeOrderRefundApplyMapper exchangeOrderRefundApplyMapper;

    /**
     * 获取打卡店铺列表
     *
     * @param param
     * @return
     */
    @Override
    public Result selectList(WechatShopParam param) {
        SearchParam searchParam = new SearchParam();
        List<ShopVO> shopVOList = shopMapper.selectList(searchParam);
        if (shopVOList == null || shopVOList.isEmpty()) {
            return Result.success("操作成功", new PageInfo<>(shopVOList));
        }
        List<Integer> shopIds = shopVOList.stream().map(ShopVO::getId).collect(Collectors.toList());
        List<ShopCircleListVO> circleVoList = businessCircleMapper.selectByShopIds(shopIds);
        Map<Object, List<ShopCircleListVO>> circleVoMap =
                circleVoList.stream().collect(Collectors.groupingBy(item -> item.getShopId()));
        for (ShopVO item : shopVOList) {
            List<ShopCircleListVO> list =
                    Optional.ofNullable(circleVoMap.get(item.getId())).orElse(Collections.emptyList());
//            List<String> circleNameList =
//                    list.stream().map(vo -> vo.getCircleName()).collect(Collectors.toList());
//            item.setCircleNameList(circleNameList);
            item.setCircleList(list);

            item.setGalleryImages(JSON.parseArray(item.getGalleryImagesStr(), String.class));
            item.setGalleryImagesStr(null);
        }

        // 获取10km以内的店铺
        // 经纬度
        String location = param.getLocation();
        String[] split1 = location.split(",");
        double lat1 = Double.parseDouble(split1[0]);
        double lon1 = Double.parseDouble(split1[1]);
        List<ShopVO> shopVOList2 = shopVOList.stream().filter(item -> {
                    // 门店经纬度
                    String shopLocation = item.getLocation();
                    String[] split2 = shopLocation.split(",");
                    double lat2 = Double.parseDouble(split2[0]);
                    double lon2 = Double.parseDouble(split2[1]);
                    double distance = DistanceCalculator.getDistance(lat1, lon1, lat2, lon2);
                    item.setDistance(distance);
                    return distance <= 10.00 || item.getRecommendOrder() > 0;
                }).sorted(Comparator.comparing(ShopVO::getRecommendOrder,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ShopVO::getDistance, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // 手动分页
        int pageSize = param.getPageSize();
        int pageNum = param.getPageNum();
        // 计算起始和结束索引
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, shopVOList2.size());
        // 截取当前页数据
        List<ShopVO> pageData = shopVOList2.subList(startIndex, endIndex);
        // 创建PageInfo对象并设置相关属性
        PageInfo<ShopVO> pageInfo = new PageInfo<>(pageData);
        pageInfo.setTotal(shopVOList2.size());
        pageInfo.setSize(pageData.size());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商圈下的店铺列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getCircleShopList(SearchParam param) {
        if (param.getSearchId() == null) {
            return Result.error("参数异常");
        }

        List<ShopVO> list = shopMapper.selectList(param);
        if (list == null || list.isEmpty()) {
            return Result.success("操作成功", new PageInfo<>(list));
        }

        List<Integer> shopIds = list.stream().map(ShopVO::getId).collect(Collectors.toList());
        List<ShopCircleListVO> circleVoList = businessCircleMapper.selectByShopIds(shopIds);
        Map<Object, List<ShopCircleListVO>> circleVoMap =
                circleVoList.stream().collect(Collectors.groupingBy(item -> item.getShopId()));

        // 用户经纬度
        String userLocation = param.getSearchStrField2();
        double lat1 = 0.0;
        double lon1 = 0.0;
        if (userLocation != null && !"".equals(userLocation)) {
            String[] split1 = userLocation.split(",");
            lat1 = Double.parseDouble(split1[0]);
            lon1 = Double.parseDouble(split1[1]);
        }

        for (ShopVO item : list) {
            List<ShopCircleListVO> circleTemp =
                    Optional.ofNullable(circleVoMap.get(item.getId())).orElse(Collections.emptyList());
            item.setCircleList(circleTemp);

            item.setGalleryImages(JSON.parseArray(item.getGalleryImagesStr(), String.class));
            item.setGalleryImagesStr(null);

            if (userLocation != null && !"".equals(userLocation)) {
                // 计算店铺和用户之间的距离
                String shopLocation = item.getLocation();
                String[] split2 = shopLocation.split(",");
                double lat2 = Double.parseDouble(split2[0]);
                double lon2 = Double.parseDouble(split2[1]);
                double distance = DistanceCalculator.getDistance(lat1, lon1, lat2, lon2);
                item.setDistance(distance);
            }
        }

        if (userLocation != null && !"".equals(userLocation)) {
            //根据距离排序
            list = list.stream()
                    .sorted(Comparator.comparing(ShopVO::getDistance))
                    .toList();
        }

        // 手动分页
        int pageSize = param.getPageSize();
        int pageNum = param.getPageNum();
        // 计算起始和结束索引
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, list.size());
        // 截取当前页数据
        List<ShopVO> pageData = list.subList(startIndex, endIndex);
        // 创建PageInfo对象并设置相关属性
        PageInfo<ShopVO> pageInfo = new PageInfo<>(pageData);
        pageInfo.setTotal(list.size());
        pageInfo.setSize(pageData.size());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 店铺点击次数+1
     *
     * @param param
     * @return
     */
    @Override
    public Result shopClickCountInc(SearchParam param) {
        Integer shopId = param.getSearchId();
        if (shopId == null) {
            return Result.error("参数异常");
        }

        shopMapper.incClickCount(shopId, 1);

        return Result.success("操作成功");
    }

    @Override
    public Result updateContract(SearchParam param, String token) {
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
    public Result getBusinessData(SearchParam param, String token) {
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
        BusinessDataVO vo = shopOrderMapper.selectBusinessData(param);
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

        vo.setRevenue(vo.getRevenue());//营业额
        vo.setPendingAmount(vo.getPendingAmount());//待结算金额
        vo.setOrderCount(vo.getOrderCount());//订单数量

        return Result.success("查询成功", vo);

    }
}
