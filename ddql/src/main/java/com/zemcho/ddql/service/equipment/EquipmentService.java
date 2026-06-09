package com.zemcho.ddql.service.equipment;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.equipment.Equipment;

import java.util.List;

public interface EquipmentService {
    Result add(Equipment data);

    Result update(Equipment data);

    Result select(SearchParam param);

    Result delete(List<Integer> deleteIds);

    /**
     * 编辑设备状态
     *
     * @param param
     * @return
     */
    Result setStatus(ChangeParam param);

    /**
     * 获取设备日志列表
     *
     * @param param
     * @return
     */
    Result logLists(SearchParam param);

    /**
     * 获取场所下的设备信息
     *
     * @param param
     * @return
     */
    Result getEquipmentByPlace(SearchParam param);
}
