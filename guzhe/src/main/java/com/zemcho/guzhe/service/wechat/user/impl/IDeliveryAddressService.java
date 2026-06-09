package com.zemcho.guzhe.service.wechat.user.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.wechat.user.param.DeliveryAddressSaveParam;
import com.zemcho.guzhe.controller.wechat.user.vo.DeliveryAddressVo;
import com.zemcho.guzhe.entity.user.DeliveryAddress;
import com.zemcho.guzhe.mapper.user.DeliveryAddressMapper;
import com.zemcho.guzhe.mapper.sys.RegionMapper;
import com.zemcho.guzhe.service.wechat.user.DeliveryAddressService;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.util.Constant;
import org.springframework.beans.BeanUtils;
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
    private CommonService commonService;

    /**
     * 添加收货地址
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional
    public Result add(DeliveryAddressSaveParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 判断手机号是否匹配格式
        if (!param.getPhone().matches("^1[3456789]\\d{9}$")) {
            return Result.error("手机号格式错误");
        }
        if (!regionMapper.ifExistsById(param.getRegionId())) {
            return Result.error("地区不存在");
        }
        // 如果新地址是默认则把旧默认地址取消默认
        if (param.getIsDefault() == 1) {
            deliveryAddressMapper.updateIsDefault(userId);
        }

        DeliveryAddress data = new DeliveryAddress();
        BeanUtils.copyProperties(param, data);
        data.setUserId(userId);
        data.setCreateTime(LocalDateTime.now());
        deliveryAddressMapper.insert(data);

        return Result.success("操作成功");
    }

    /**
     * 修改收货地址
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional
    public Result update(DeliveryAddressSaveParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        if (param.getId() == null) {
            return Result.error("参数异常");
        }
        if (!param.getPhone().matches("^1[3456789]\\d{9}$")) {
            return Result.error("手机号格式错误");
        }
        if (!regionMapper.ifExistsById(param.getRegionId())) {
            return Result.error("地区不存在");
        }

        DeliveryAddress existInfo = deliveryAddressMapper.selectById(param.getId());
        if (existInfo == null) {
            return Result.error("记录不存在");
        }
        if (!existInfo.getUserId().equals(userId)) {
            return Result.error("您无权操作该记录");
        }

        if (param.getIsDefault() == 1) {
            deliveryAddressMapper.updateIsDefault(userId);
        }

        DeliveryAddress data = new DeliveryAddress();
        BeanUtils.copyProperties(param, data);
        deliveryAddressMapper.update(data);

        return Result.success("操作成功");
    }

    /**
     * 获取收货地址列表
     *
     * @param token
     * @return
     */
    @Override
    public Result getList(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        List<DeliveryAddressVo> list = deliveryAddressMapper.selectByUserId(userId);
        if (list != null && !list.isEmpty()) {
            for (DeliveryAddressVo item : list) {
                item.setRegionList(commonService.selectRegionDataById(item.getRegionId()));
            }
        }

        return Result.success("获取成功", list);
    }

    /**
     * 删除收货地址
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result delete(DeleteOneParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer id = param.getDeleteId();

        DeliveryAddress existInfo = deliveryAddressMapper.selectById(id);
        if (existInfo == null) {
            return Result.error("记录不存在");
        }
        if (!existInfo.getUserId().equals(userId)) {
            return Result.error("您无权删除该记录");
        }

        deliveryAddressMapper.delete(id);

        return Result.success("删除成功");
    }
}
