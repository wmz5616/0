package com.zemcho.ddql.entity.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusinessCircleShop {
    // 主键ID
    private Integer id;

    // 商圈ID
    private Integer circleId;

    // 店铺ID
    private Integer shopId;

}
