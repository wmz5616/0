package com.zemcho.guzhe.controller.cas.param;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author HXH
 */
@Data
public class CasUserParam {

    //用户id
    @NotNull(message = "用户id不能为空")
    private Integer id;
    //用户状态 0正常 1锁定
    @NotNull(message = "用户状态不能为空")
    private Integer lock;
    //锁定时效(0:永久，1：有限时间)
    private Integer lockStatus;
    //锁有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lockExpiredAt;
    //锁定原因
    private String lockReason;

    //管理员备注
    private String remark;

}
