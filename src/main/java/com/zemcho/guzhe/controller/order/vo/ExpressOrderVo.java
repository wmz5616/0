package com.zemcho.guzhe.controller.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @title: ExpressOrderVo
 * @Description:
 * @Date: 2026/04/29 10:37
 */
@Data
public class ExpressOrderVo {
    // ID
    private Integer id;

    //关联的系统订单类型：1商品订单
    private Integer txnType;

    //对应的系统记录id
    private Integer txnId;

    //快递公司名称
    private String expressCompanyName;

    //快递公司标识码
    private String expressCompanyCode;

    //快递单号
    private String expressNo;

    //物流详情，json格式
    private String info;

    //物流详情
    private List<Map> infoData;

    //快递状态：-1--待发货，0--在途，1--揽件，2--疑难，3--签收，4--退签，5--派件，6--退回，10--待清关，11--清关中，12--已清关，
    // 13--清关异常，14--收件人拒签
    private Integer status;

    //是否为被系统记录关联着，0否，1是（0时代表该单跟踪中途，对应的记录被切换到了新的物流，但是快递100那边无法停止订阅，估需要该字段判断）
    private Integer isRecord;

    //是否已订阅，0否，1是
    private Integer isSub;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
