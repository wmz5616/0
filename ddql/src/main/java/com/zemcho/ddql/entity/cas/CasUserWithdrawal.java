package com.zemcho.ddql.entity.cas;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.util.excel.converter.order.WithdrawalStatusConverter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CasUserWithdrawal {
    // 主键ID
    @ExcelProperty(value = "提现id")
    @ColumnWidth(15)
    private Integer id;

    // 用户id
    @ExcelIgnore
    private Integer userId;

    // 用户昵称
    @ExcelProperty(value = "用户昵称")
    @ColumnWidth(25)
    private String nickname;

    // 用户手机号
    @ExcelProperty(value = "用户手机号")
    @ColumnWidth(25)
    private String phone;

    //团队id
    @ExcelIgnore
    private Integer teamId;

    // 团队名字
    @ExcelProperty(value = "团队名字")
    @ColumnWidth(25)
    private String teamName;

    // 团队类型 0企事单位 1政府部分 2家庭 3朋友
    @ExcelIgnore
    private Integer teamType;

    //提现金额（元），只能为整数，一元对应一个健康币
    @ExcelProperty(value = "提现金额（元）")
    @ColumnWidth(25)
    private Integer amount;

    // 系统生成的订单号
    @ExcelProperty(value = "系统订单号")
    @ColumnWidth(25)
    private String outBillNo;

    // 微信转账单号
    @ExcelProperty(value = "微信转账单号")
    @ColumnWidth(25)
    private String wxTransactionNo;

    /**
     * 状态：
     * 空字符串目前也为失败状态
     * ACCEPTED:  转账已受理，可原单重试（非终态）
     * PROCESSING:  转账锁定资金中。如果一直停留在该状态，建议检查账户余额是否足够，如余额不足，可充值后再原单重试（非终态）
     * WAIT_USER_CONFIRM:  待收款用户确认，当前转账单据资金已锁定，可拉起微信收款确认页面进行收款确认（非终态）
     * TRANSFERING:  转账中，可拉起微信收款确认页面再次重试确认收款（非终态）
     * SUCCESS:  转账成功，表示转账单据已成功（终态）
     * FAIL:  转账失败，表示该笔转账单据已失败。若需重新向用户转账，请重新生成单据并再次发起（终态）
     * CANCELING:  转账撤销中，商户撤销请求受理成功，该笔转账正在撤销中，需查单确认撤销的转账单据状态（非终态）
     * CANCELLED:  转账撤销完成，代表转账单据已撤销成功（终态）
     */
    @ExcelProperty(value = "状态", converter = WithdrawalStatusConverter.class)
    @ColumnWidth(25)
    private String state;

    // 微信响应结果信息（json格式）
    @ExcelIgnore
    private String wxResult;

    // 创建时间
    @ExcelProperty(value = "提现时间")
    @ColumnWidth(20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
