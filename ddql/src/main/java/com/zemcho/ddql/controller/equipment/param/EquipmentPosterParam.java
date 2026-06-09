package com.zemcho.ddql.controller.equipment.param;

import com.zemcho.ddql.entity.equipment.EquipmentPoster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentPosterParam {

    private List<EquipmentPoster> data;

}
