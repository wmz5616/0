package com.zemcho.ddql.service.checkInSettings;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.checkInSettings.param.CheckInPlaceParam;

import java.util.List;

public interface CheckInPlaceService {

    public Result add(CheckInPlaceParam param);

    public Result update(CheckInPlaceParam param);

    public Result select(SearchParam param);

    public Result selectUserByPlaceId(Integer placeId);

    public Result delete(List<Integer> deleteIds);

    /**
     * 编辑打卡地点状态
     *
     * @param param
     * @return
     */
    public Result setStatus(ChangeParam param);

    // 查出用户(管理员)关联的场所 小程序用
    public Result selectUserPlace(String token);

    // 更新场所 小程序用 id name images remark
    public Result update(CheckInPlaceParam param, String token);
}
