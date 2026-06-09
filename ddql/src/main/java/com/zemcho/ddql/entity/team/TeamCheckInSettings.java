package com.zemcho.ddql.entity.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamCheckInSettings {

    /**
     * id
     */
    private Integer id;

    /**
     * 关联的团队id
     */
    private Integer teamId;

    /**
     * 扫码打卡时长（分钟）
     */
    private Integer scanCodeTime;

    /**
     * 扫码打卡可得健康币数量
     */
    private Integer scanCodeHealthyCoin;

    /**
     * 是否开启步数打卡 0开启 1关闭
     */
    private Integer stepsOpen;

    /**
     * 步数打卡的目标步数
     */
    private Integer targetSteps;

    /**
     * 步数打卡可得的健康币数量
     */
    private Integer stepsHealthyCoin;

    /**
     * 最低提现金额
     */
    private Integer lowestWithdrawalMoney;

}
