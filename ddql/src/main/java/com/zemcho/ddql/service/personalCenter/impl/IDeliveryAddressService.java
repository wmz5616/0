package com.zemcho.ddql.service.personalCenter.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.DeliveryAddressVo;
import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import com.zemcho.ddql.mapper.personalCenter.DeliveryAddressMapper;
import com.zemcho.ddql.mapper.personalCenter.RegionMapper;
import com.zemcho.ddql.service.personalCenter.DeliveryAddressService;
import com.zemcho.ddql.service.personalCenter.RegionService;
import com.zemcho.ddql.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IDeliveryAddressService implements DeliveryAddressService {
    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private RegionService regionService;

    @Override
    @Transactional
    public Result add(DeliveryAddress data, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        if (data.getIsDefault() == null || data.getName() == null || data.getAddress() == null || data.getPhone() == null || data.getRegionId() == null || data.getLocation() == null) {
            return Result.error("参数错误");
        }
        // 判断手机号是否匹配格式
        if (!data.getPhone().matches("^1[3456789]\\d{9}$")) {
            return Result.error("手机号格式错误");
        }
        // 如果新地址是默认则把旧默认地址取消默认
        if (data.getIsDefault() == 1) {
            deliveryAddressMapper.updateIsDefault(userId);
        }
        if (!regionMapper.ifExistsById(data.getRegionId())) {
            return Result.error("地区不存在");
        }

        data.setUserId(userId);
        data.setCreateTime(LocalDateTime.now());
        deliveryAddressMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    @Transactional
    public Result update(DeliveryAddress data) {
        if (data.getId() == null || !deliveryAddressMapper.ifExistsById(data.getId())) {
            return Result.error("id不能为空");
        }
        if (data.getIsDefault() == 1) {
            deliveryAddressMapper.updateIsDefault(data.getUserId());
        }
        if (data.getPhone() != null && !data.getPhone().matches("^1[3456789]\\d{9}$")) {
            return Result.error("手机号格式错误");
        }
        if (!regionMapper.ifExistsById(data.getRegionId())) {
            return Result.error("地区不存在");
        }

        deliveryAddressMapper.update(data);
        return Result.success("操作成功");
    }

    @Override
    public Result select(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        List<DeliveryAddressVo> list = deliveryAddressMapper.selectByUserId(userId);
        if (list != null && !list.isEmpty()) {
            for (DeliveryAddressVo item : list) {
                item.setRegionList(regionService.selectRegionDataById(item.getRegionId()));
            }
        }

        return Result.success("获取成功", list);
    }

    @Override
    public Result delete(Integer id) {
        if (!deliveryAddressMapper.ifExistsById(id)) {
            return Result.error("不存在该数据");
        }
        deliveryAddressMapper.delete(id);
        return Result.success("删除成功");
    }


}
