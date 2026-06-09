package com.zemcho.ddql.controller.team.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.util.excel.converter.common.YesOrNoConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamStatusConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamTypeConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamVerificationStatusConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamVerifiedConverter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @title: TeamListVo
 * @Description:
 * @Date: 2025/11/5 18:50
 */
@Data
public class TeamListVo {
    /**
     * 主键ID
     */
    @ColumnWidth(5)
    @ExcelProperty("序号")
    private Integer id;

    /**
     * 团队名字
     */
    @ColumnWidth(10)
    @ExcelProperty("团体名称")
    private String name;

    /**
     * 团队类型: 0 企事业单位, 1 政府部门, 2 家庭, 3 朋友
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "团体类型",converter = TeamTypeConverter.class)
    private Integer type;

    /**
     * 是否已认证: 0 未认证, 1 已认证
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "资质认证",converter = TeamVerifiedConverter.class)
    private Integer isVerified;

    /**
     * 认证审核状态: 0 审核中, 1 审核通过, 2 审核驳回
     */
    @ExcelIgnore
    private Integer verificationStatus;

    /**
     * 人数
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "团体人数")
    private Integer peopleNumber;

    /**
     * 地区
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "地区")
    private String region;

    /**
     * 详细地址
     */
    @ExcelIgnore
    private String address;

    /**
     * 联系人名称
     */
    @ExcelIgnore
    private String contactPerson;

    /**
     * 联系电话
     */
    @ExcelIgnore
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @ExcelIgnore
    private String contactEmail;

    /**
     * 状态: 0 启用, 1 禁用
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "启用状态",converter = TeamStatusConverter.class)
    private Integer status;

    /**
     * 进团是否审核：0否，1是
     */
    @ExcelIgnore
    private Integer isUserAuth;

    /**
     * 每月打卡次数要求，0为无
     */
    @ExcelIgnore
    private Integer checkInNumLimit;

    /**
     * 团队健康币余额
     */
    @ExcelIgnore
    private Integer healthyCoin;

    /**
     * 创建时间
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 解散时间
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "解散时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deleteTime;

    /**
     * 是否开启多部门
     */
    @ExcelIgnore
    private Integer isMultiDepartment;
}
