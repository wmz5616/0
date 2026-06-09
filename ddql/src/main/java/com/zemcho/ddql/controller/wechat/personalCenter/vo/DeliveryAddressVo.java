package com.zemcho.ddql.controller.wechat.personalCenter.vo;

import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import com.zemcho.ddql.entity.personalCenter.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddressVo extends DeliveryAddress {
    /**
     * 地区信息
     */
    LinkedList<Region> regionList;
}
