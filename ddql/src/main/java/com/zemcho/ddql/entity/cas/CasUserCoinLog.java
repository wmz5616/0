package com.zemcho.ddql.entity.cas;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CasUserCoinLog {
    // 主键ID
    private Integer id;

    // 类型：1打卡、2充值、3兑换、4提现、5兑换退货、6步数达标
    private Integer txnType;

    // 对应类型的记录id
    private Integer txnId;

    //币类型：1健康币、2金币
    private Integer coinType;

    //变更类型：1增加、2减少
    private Integer numType;

    //币数量
    private Integer coinNum;

    // 用户id
    private Integer userId;

    // 用户手机号
    private String phone;

    // 用户昵称
    private String nickName;

    //团队id
    private Integer teamId;

    // 团队名字
    private String teamName;

    // 团队类型 0企事单位 1政府部分 2家庭 3朋友
    private Integer teamType;

    //备注说明
    private String remark;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
