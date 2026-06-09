package com.zemcho.ddql.entity.checkInSettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打卡场所和管理员关联
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInPlaceUser {
    private Integer id;

    private Integer userId;

    private Integer placeId;
}
