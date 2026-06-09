package com.zemcho.ddql.service.equipment;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.equipment.param.EquipmentPosterParam;

public interface EquipmentPosterService {

    Result save(EquipmentPosterParam param);

    Result select(SearchParam param);

    /**
     * 修改设备海报顺序
     *
     * @param param
     * @return
     */
    Result setSort(SearchParam param);

    /**
     * 删除设备海报
     *
     * @param param
     * @return
     */
    Result delete(DeleteParam param);

    // 用于定时更新海报状态
    public void updateEquipmentPoster();

}
