package com.zemcho.ddql.entity.checkInSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInSettings {

    /**
     * id
     */
    private Integer id;


    /**
     * 目标步数
     */
    private Integer targetSteps;

    /**
     * 步数打卡发放的金币数量
     */
    private Integer stepsGoldCoin;

    /**
     * 扫码打卡发放的金币数量
     */
    private Integer scanCodeGoldCoin;

    /**
     * 打卡说明
     */
    private String checkInInstruction;

    /**
     * 提现说明
     */
    private String withdrawalInstruction;

    /**
     * 提现分享海报图，json格式
     */
    private String withdrawalPicture;

    private List<String> withdrawalPictureList;
}
