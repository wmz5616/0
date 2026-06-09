package com.zemcho.ddql.entity.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductTicket {
    // 主键ID
    private Integer id;

    // 商品id
    private Integer productId;

    // 券码
    private String ticket;

    // 序号
    private Integer sort;

    // 状态：1 未下发 2 已下发 3已核销
    private Integer status;

    // 订单id
    private Integer orderId;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
