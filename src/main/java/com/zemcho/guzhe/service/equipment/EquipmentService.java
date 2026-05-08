package com.zemcho.guzhe.service.equipment;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.equipment.param.EquipmentParam;
import com.zemcho.guzhe.entity.equipment.Equipment;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface EquipmentService {
    Result add(EquipmentParam data);

    Result update(EquipmentParam data);

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

}
