package com.zemcho.guzhe.controller.wechat.user.vo;

import com.zemcho.guzhe.entity.user.DeliveryAddress;
import com.zemcho.guzhe.entity.sys.Region;
import lombok.Data;

import java.util.LinkedList;

@Data
public class DeliveryAddressVo extends DeliveryAddress {
    /**
     * 地区信息
     */
    LinkedList<Region> regionList;
}
