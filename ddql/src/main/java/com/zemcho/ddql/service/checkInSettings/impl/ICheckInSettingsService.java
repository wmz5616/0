package com.zemcho.ddql.service.checkInSettings.impl;

import com.alibaba.fastjson.JSON;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;
import com.zemcho.ddql.mapper.checkInSettings.CheckInSettingsMapper;
import com.zemcho.ddql.service.checkInSettings.CheckInSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ICheckInSettingsService implements CheckInSettingsService {

    @Autowired
    private CheckInSettingsMapper checkInSettingsMapper;

    @Override
    public Result getSettings(SearchParam param) {
        CheckInSettings checkInSettings = checkInSettingsMapper.get();

        if (checkInSettings != null) {
            List<String> withdrawalPictureList = new ArrayList<>();
            if (checkInSettings.getWithdrawalPicture() != null && !checkInSettings.getWithdrawalPicture().isEmpty()) {
                withdrawalPictureList = JSON.parseArray(checkInSettings.getWithdrawalPicture(), String.class);
            }
            checkInSettings.setWithdrawalPictureList(withdrawalPictureList);
            checkInSettings.setWithdrawalPicture(null);
        }

        return Result.success("获取成功", checkInSettings);
    }

    @Override
    public Result updateSettings(CheckInSettings checkInSettings) {
        if (checkInSettings.getId() == null) {
            return Result.error("id不能为空");
        }

        String withdrawalPicture = "";
        if (checkInSettings.getWithdrawalPictureList() != null && !checkInSettings.getWithdrawalPictureList().isEmpty()) {
            withdrawalPicture = JSON.toJSONString(checkInSettings.getWithdrawalPictureList());
        }
        checkInSettings.setWithdrawalPicture(withdrawalPicture);
        checkInSettings.setWithdrawalPictureList(null);

        checkInSettingsMapper.update(checkInSettings);

        return Result.success("操作成功");
    }
}
