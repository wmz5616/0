package com.zemcho.ddql.controller.wechat.index.vo;

import com.zemcho.ddql.controller.checkInSettings.vo.CheckInPlaceVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @title: UserCheckInRankVo
 * @Description:
 * @Date: 2025/10/11 17:30
 */
@Data
public class UserCheckInRankVo {
    // 用户id
    private Integer userId;

    // 用户昵称
    private String nickName;

    // 用户头像
    private String avatar;

    // 成员在团队下的姓名
    private String userName;

    // 成员在团队下的电话
    private String userPhone;

    // 成员类型: 0 创建者, 1 管理员, 2 普通用户
    private Integer type;

    // 加入的方式: 0 申请加入, 1 扫码加入
    private Integer joinType;

    // 状态: 0 启用, 1 禁用
    private Integer status;

    // 打卡场地信息
    private String placeIds;
    private List<CheckInPlaceVo> placeList;

    //打卡场地类型统计信息
    private List<UserCheckInPlaceTypeStatVo> placeTypeStatList;

    //是否有步数达标数据：true-有，false-无
    private Boolean hasStepCoin = false;

    //健康币数量
    private Integer healthCoin;

    // 打卡时长（秒）
    private Integer checkInTime;

    // 打卡次数
    private Integer checkInNum;

    //排名
    private Integer rank;

    public List<Integer> getPlaceIdList() {
        if (this.placeIds == null || this.placeIds.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(this.placeIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
