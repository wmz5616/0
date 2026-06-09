package com.zemcho.ddql.controller.sys.param;

import com.zemcho.ddql.common.param.SearchParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionFlowSearchParam extends SearchParam {

    private Integer shopId; // 店铺ID

    private String orderNo;

    private String merchantOrderNo;

    private String productName;

    private String orderUser;

    private String yibaoOrderNo; // 易宝订单号

    private String type; // 交易类型：1=收款，2=退款

    private String subLedgerRequestNo; // 分账请求号

    private java.util.List<Long> ids;

    private java.util.List<String> searchStrList;
}
