package com.zemcho.guzhe.entity.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppVersionPushLog {
    /**
     * id
     */
    private Integer id;

    /**
     * 设备编号
     */
    private String serialNumber;
    /**
     * 商超id
     */
    private Integer sup_id;

    /**
     * 下发状态 1成功 2失败
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
