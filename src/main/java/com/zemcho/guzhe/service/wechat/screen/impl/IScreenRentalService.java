package com.zemcho.guzhe.service.wechat.screen.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalAvailableParam;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalRentParam;
import com.zemcho.guzhe.controller.wechat.screen.param.ScreenRentalSelectionParam;
import com.zemcho.guzhe.controller.wechat.screen.vo.ScreenRentalAvailableVo;
import com.zemcho.guzhe.controller.wechat.screen.vo.ScreenRentalMonthVo;
import com.zemcho.guzhe.controller.wechat.screen.vo.ScreenRentalRentItemVo;
import com.zemcho.guzhe.controller.wechat.screen.vo.ScreenRentalRentVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.screen.ScreenRentalDetail;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrderLog;
import com.zemcho.guzhe.entity.screen.ScreenRentalOrder;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalDetailMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderLogMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.supermarket.SupermarketMapper;
import com.zemcho.guzhe.service.wechat.screen.ScreenRentalService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisLockUtil;
import com.zemcho.guzhe.util.uuid.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IScreenRentalService implements ScreenRentalService {
    /**
     * 可租月份展示范围。
     * 当前按产品原型返回“从本月开始的 24 个月”。
     */
    private static final int DEFAULT_MONTH_SIZE = 24;

    /**
     * 单个设备每月默认可租用 6 个店位。
     */
    private static final int DEFAULT_SLOT_TOTAL = 6;

    /**
     * 月份统一格式：yyyy-MM。
     * 前端传参、占用比对、结果返回都复用这个格式。
     */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private SupermarketMapper supermarketMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @Autowired
    private ScreenRentalDetailMapper screenRentalDetailMapper;

    @Autowired
    private ScreenRentalOrderLogMapper screenRentalOrderLogMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Override
    public Result availableLists(ScreenRentalAvailableParam param) {
        // 该接口有两种查询模式：
        // 1. 传 businessCircleId：只查单个商超下的设备
        // 2. 不传 businessCircleId：按地区/地址关键词查多个商超，再汇总这些商超下的全部设备
        Integer businessCircleId = param.getBusinessCircleId();

        // 先生成默认展示月份，后面所有设备都复用同一批月份数据
        List<YearMonth> yearMonths = buildDefaultMonths();
        List<String> monthKeys = yearMonths.stream().map(this::formatMonth).collect(Collectors.toList());

        List<Equipment> equipmentList;
        // 多商超查询时，需要把商超 id -> 商超名称 预先缓存起来，组装返回值时直接使用
        Map<Integer, String> businessCircleNameMap = new HashMap<>();
        // 保存“已被占用”的租用明细，用于计算每台设备每个月已经用了多少店位
        List<ScreenRentalDetail> occupiedList;

        if (businessCircleId != null) {
            // 单商超模式：先校验商超有效性
            SupermarketInfo businessCircleInfo = supermarketMapper.selectById(businessCircleId);
            if (businessCircleInfo == null) {
                return Result.error("商超不存在");
            }
            if (businessCircleInfo.getStatus() != null && businessCircleInfo.getStatus() != 1) {
                return Result.error("商超已禁用");
            }

            // 查询该商超下所有启用设备
            equipmentList = equipmentMapper.selectEnableBySupermarketId(businessCircleId);
            if (equipmentList == null || equipmentList.isEmpty()) {
                return Result.success("获取成功", new ArrayList<>());
            }
            businessCircleNameMap.put(businessCircleId, businessCircleInfo.getName());

            // 单商超模式下，直接按商超查未来月份里已占用的店位明细
            occupiedList = screenRentalDetailMapper.selectActiveByBusinessCircleIdAndMonthKeys(businessCircleId, monthKeys);
        } else {
            // 多商超模式：按地址关键词查询启用中的商超
            List<SupermarketInfo> businessCircleList =
                    supermarketMapper.selectEnableByAddressKeyword(param.getScreenAddress());
            if (businessCircleList == null || businessCircleList.isEmpty()) {
                return Result.success("获取成功", new ArrayList<>());
            }

            List<Integer> businessCircleIds = new ArrayList<>();
            for (SupermarketInfo item : businessCircleList) {
                businessCircleIds.add(item.getId());
                businessCircleNameMap.put(item.getId(), item.getName());
            }

            // 汇总查询这些商超下的全部启用设备
            equipmentList = equipmentMapper.selectEnableBySupermarketIds(businessCircleIds);
            if (equipmentList == null || equipmentList.isEmpty()) {
                return Result.success("获取成功", new ArrayList<>());
            }

            // 多商超模式下，按设备 id 批量查询占用情况，避免逐台设备单独查数据库
            List<Integer> equipmentIds = equipmentList.stream().map(Equipment::getId).collect(Collectors.toList());
            occupiedList = screenRentalDetailMapper.selectActiveByEquipmentIdsAndMonthKeys(equipmentIds, monthKeys);
        }

        // 转成 equipmentId -> monthKey -> 已租用店位数，后面就能直接算“每月剩余店位数”
        Map<Integer, Map<String, Integer>> occupiedMap = new HashMap<>();
        for (ScreenRentalDetail detail : occupiedList) {
            Integer equipmentId = detail.getEquipmentId();
            String monthKey = buildMonthKey(detail.getRentalYear(), detail.getRentalMonth());
            Integer slotCount = detail.getSlotCount() == null ? 1 : detail.getSlotCount();
            occupiedMap.computeIfAbsent(equipmentId, item -> new HashMap<>())
                    .merge(monthKey, slotCount, Integer::sum);
        }

        List<ScreenRentalAvailableVo> result = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            ScreenRentalAvailableVo vo = new ScreenRentalAvailableVo();
            vo.setEquipmentId(equipment.getId());
            vo.setSerialNumber(equipment.getSerialNumber());
            vo.setMoney(equipment.getMoney());
            vo.setBusinessCircleId(equipment.getSupermarketId());
            vo.setBusinessCircleName(businessCircleNameMap.getOrDefault(equipment.getSupermarketId(), ""));

            // 逐月构建返回数据：
            // usedCount = 该月已租用的店位数
            // remainingCount = 6 - usedCount
            // status = 剩余店位是否已经为 0
            Map<String, Integer> occupiedMonthMap = occupiedMap.getOrDefault(equipment.getId(), Collections.emptyMap());
            List<ScreenRentalMonthVo> monthList = new ArrayList<>();
            for (YearMonth yearMonth : yearMonths) {
                ScreenRentalMonthVo monthVo = new ScreenRentalMonthVo();
                monthVo.setYear(yearMonth.getYear());
                monthVo.setMonth(yearMonth.getMonthValue());
                monthVo.setMonthKey(formatMonth(yearMonth));
                monthVo.setMonthLabel(String.valueOf(yearMonth.getMonthValue()));
                Integer usedCount = occupiedMonthMap.getOrDefault(monthVo.getMonthKey(), 0);
                Integer remainingCount = Math.max(DEFAULT_SLOT_TOTAL - usedCount, 0);
                monthVo.setUsedCount(usedCount);
                monthVo.setRemainingCount(remainingCount);
                monthVo.setStatus(remainingCount > 0 ? 0 : 1);
                monthList.add(monthVo);
            }
            vo.setMonthList(monthList);
            result.add(vo);
        }

        return Result.success("获取成功", result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result rent(ScreenRentalRentParam param, String token) {
        // 小程序侧 token 中保存的是 MINI_USER_ID，这里先取出当前登录用户
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 1. 校验当前操作人必须是商家管理员
        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getAdminId() == null || userInfo.getAdminId() <= 0) {
            return Result.error("当前用户不是商家管理员");
        }

        // 2. 校验商超、商家关系
        SupermarketInfo businessCircleInfo = supermarketMapper.selectById(param.getBusinessCircleId());
        if (businessCircleInfo == null) {
            return Result.error("商超不存在");
        }

        Shop shop = shopMapper.selectById(param.getShopId());
        if (shop == null) {
            return Result.error("商家不存在");
        }
        if (shop.getMerchantId() == null || !shop.getMerchantId().equals(userInfo.getAdminId())) {
            return Result.error("您无权租用该商家店位");
        }

        // 3. 校验展示内容类型。
        Integer displayType = param.getDisplayType();
        if (displayType == null || (displayType != 1 && displayType != 2)) {
            return Result.error("展示内容类型错误");
        }

        List<ScreenRentalSelectionParam> selections = param.getSelections();
        if (selections == null || selections.isEmpty()) {
            return Result.error("租用设备为空");
        }

        // 同一次提交里不允许同一设备重复出现，避免前端重复勾选导致重复拆单
        Set<Integer> selectionEquipmentIdSet = new HashSet<>();
        for (ScreenRentalSelectionParam item : selections) {
            if (item == null || item.getEquipmentId() == null) {
                return Result.error("设备id为空");
            }
            if (!selectionEquipmentIdSet.add(item.getEquipmentId())) {
                return Result.error("存在重复设备，请重新选择");
            }
        }

        // 4. 按设备排序后依次加锁，避免并发下单导致部分设备先成功、部分设备失败
        List<Integer> equipmentIds = new ArrayList<>(selectionEquipmentIdSet);
        equipmentIds.sort(Comparator.naturalOrder());
        List<String> lockedKeys = new ArrayList<>();
        try {
            for (Integer equipmentId : equipmentIds) {
                String lockKey = Constant.OPERATION_LOCK_PREFIX + "screenRentalRent:" + equipmentId;
                Boolean lockFlag = redisLockUtil.tryLock(lockKey, 20, java.util.concurrent.TimeUnit.SECONDS);
                if (!lockFlag) {
                    return Result.error("当前店位正在处理中，请稍后再试");
                }
                lockedKeys.add(lockKey);
            }

            LocalDateTime now = LocalDateTime.now();
            ScreenRentalRentVo result = new ScreenRentalRentVo();
            List<ScreenRentalRentItemVo> orderList = new ArrayList<>();
            int totalAmount = 0;

            for (ScreenRentalSelectionParam selection : selections) {
                Equipment equipment = equipmentMapper.selectById(selection.getEquipmentId());
                if (equipment == null) {
                    return Result.error("店位不存在");
                }
                if (equipment.getStatus() == null || equipment.getStatus() != 1) {
                    return Result.error("当前店位不可租用");
                }
                if (!param.getBusinessCircleId().equals(equipment.getSupermarketId())) {
                    return Result.error("店位与商超信息不匹配");
                }
                if (equipment.getMoney() == null || equipment.getMoney() < 0) {
                    return Result.error("店位租金配置异常");
                }

                List<YearMonth> rentalMonths;
                try {
                    rentalMonths = parseRentalMonths(selection.getRentalMonths());
                } catch (IllegalArgumentException e) {
                    return Result.error(e.getMessage());
                }
                if (rentalMonths.isEmpty()) {
                    return Result.error("租用月份为空");
                }

                List<String> monthKeys = rentalMonths.stream().map(this::formatMonth).collect(Collectors.toList());
                List<ScreenRentalDetail> conflictList =
                        screenRentalDetailMapper.selectActiveByEquipmentAndMonthKeys(selection.getEquipmentId(), monthKeys);

                Map<String, Integer> usedCountMap = new HashMap<>();
                for (ScreenRentalDetail detail : conflictList) {
                    String monthKey = buildMonthKey(detail.getRentalYear(), detail.getRentalMonth());
                    Integer slotCount = detail.getSlotCount() == null ? 1 : detail.getSlotCount();
                    usedCountMap.merge(monthKey, slotCount, Integer::sum);
                }

                List<String> fullMonths = new ArrayList<>();
                for (String monthKey : monthKeys) {
                    Integer usedCount = usedCountMap.getOrDefault(monthKey, 0);
                    if (usedCount >= DEFAULT_SLOT_TOTAL) {
                        fullMonths.add(monthKey);
                    }
                }
                if (!fullMonths.isEmpty()) {
                    return Result.error("设备" + equipment.getSerialNumber() + "所选月份店位已租满：" + String.join("、", fullMonths));
                }

                ScreenRentalOrder order = new ScreenRentalOrder();
                order.setOrderNo(OrderNoUtil.generateNo(userId));
                order.setEquipmentId(selection.getEquipmentId());
                order.setShopId(param.getShopId());
                order.setShopName(shop.getName());
                order.setBusinessCircleId(param.getBusinessCircleId());
                order.setBusinessCircleName(businessCircleInfo.getName());
                order.setTotalAmount(equipment.getMoney() * rentalMonths.size());
                order.setOrderTime(now);
                order.setUserId(userId);
                order.setPhone(userInfo.getPhone() == null ? "" : userInfo.getPhone());
                order.setNickName(userInfo.getNickname() == null ? "" : userInfo.getNickname());
                order.setDisplayType(displayType);
                order.setStatus(0);
                order.setRemark(param.getRemark());
                order.setCreateTime(now);
                order.setUpdateTime(now);
                screenRentalOrderMapper.insert(order);

                ScreenRentalOrderLog orderLog = new ScreenRentalOrderLog();
                orderLog.setOrderId(order.getId());
                orderLog.setOperationType(1);
                orderLog.setOperationResult(0);
                orderLog.setOperatorId(userId);
                orderLog.setOperatorName(userInfo.getNickname() == null ? "" : userInfo.getNickname());
                orderLog.setOperatorPhone(userInfo.getPhone() == null ? "" : userInfo.getPhone());
                orderLog.setDisplayType(displayType);
                orderLog.setOperationRemark(param.getRemark());
                orderLog.setOperationTime(now);
                screenRentalOrderLogMapper.insert(orderLog);

                List<ScreenRentalDetail> details = new ArrayList<>();
                for (YearMonth rentalMonth : rentalMonths) {
                    ScreenRentalDetail detail = new ScreenRentalDetail();
                    detail.setOrderId(order.getId());
                    detail.setEquipmentId(selection.getEquipmentId());
                    detail.setShopId(param.getShopId());
                    detail.setBusinessCircleId(param.getBusinessCircleId());
                    detail.setRentalYear(rentalMonth.getYear());
                    detail.setRentalMonth(rentalMonth.getMonthValue());
                    detail.setSlotCount(1);
                    detail.setStatus(0);
                    detail.setCreateTime(now);
                    detail.setUpdateTime(now);
                    details.add(detail);
                }
                screenRentalDetailMapper.insertBatch(details);

                ScreenRentalRentItemVo itemVo = buildScreenRentalRentItemVo(order, monthKeys);
                orderList.add(itemVo);
                totalAmount += order.getTotalAmount() == null ? 0 : order.getTotalAmount();
            }

            result.setOrderCount(orderList.size());
            result.setTotalAmount(totalAmount);
            result.setOrderList(orderList);
            return Result.success("操作成功", result);
        } finally {
            for (String lockKey : lockedKeys) {
                redisLockUtil.unlock(lockKey);
            }
        }
    }

    private static ScreenRentalRentItemVo buildScreenRentalRentItemVo(ScreenRentalOrder order, List<String> monthKeys) {
        ScreenRentalRentItemVo result = new ScreenRentalRentItemVo();
        result.setOrderId(order.getId());
        result.setOrderNo(order.getOrderNo());
        result.setTotalAmount(order.getTotalAmount());
        result.setEquipmentId(order.getEquipmentId());
        result.setShopId(order.getShopId());
        result.setBusinessCircleId(order.getBusinessCircleId());
        result.setDisplayType(order.getDisplayType());
        result.setRentalMonths(monthKeys);
        return result;
    }

    /**
     * 生成默认展示月份。
     */
    private List<YearMonth> buildDefaultMonths() {
        List<YearMonth> result = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 0; i < DEFAULT_MONTH_SIZE; i++) {
            result.add(now.plusMonths(i));
        }
        return result;
    }

    /**
     * 解析前端传入的租用月份列表，并完成基础校验。
     */
    private List<YearMonth> parseRentalMonths(List<String> monthList) {
        LinkedHashSet<YearMonth> monthSet = new LinkedHashSet<>();
        YearMonth currentMonth = YearMonth.now();
        for (String item : monthList) {
            if (item == null || item.trim().isEmpty()) {
                throw new IllegalArgumentException("租用月份格式错误");
            }
            try {
                YearMonth yearMonth = YearMonth.parse(item.trim(), MONTH_FORMATTER);
                if (yearMonth.isBefore(currentMonth)) {
                    throw new IllegalArgumentException("不能选择历史月份");
                }
                monthSet.add(yearMonth);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("租用月份格式错误");
            }
        }
        List<YearMonth> result = new ArrayList<>(monthSet);
        result.sort(Comparator.naturalOrder());
        return result;
    }

    /**
     * YearMonth 转 yyyy-MM。
     */
    private String formatMonth(YearMonth yearMonth) {
        return yearMonth.format(MONTH_FORMATTER);
    }

    /**
     * 数据库中的 year + month 转 yyyy-MM，用于和前端月份统一比对。
     */
    private String buildMonthKey(Integer year, Integer month) {
        return String.format("%04d-%02d", year, month);
    }
}
