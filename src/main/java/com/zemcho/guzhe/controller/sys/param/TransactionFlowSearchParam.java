package com.zemcho.guzhe.controller.sys.param;

import com.zemcho.guzhe.common.param.SearchParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionFlowSearchParam extends SearchParam {

    private String orderNo;

    private String merchantOrderNo;

    private Integer type;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
