package com.zemcho.ddql.controller.recharge.param;

import lombok.Data;

import java.util.List;

@Data
public class RechargeRequest {

    private List<RechargeActivityParam> rechargeActivityList;
}
