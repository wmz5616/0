package com.zemcho.ddql.entity.team;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.ddql.util.excel.converter.common.YesOrNoConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamStatusConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamTypeConverter;
import com.zemcho.ddql.util.excel.converter.team.TeamVerifiedConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    /**
     * 主键ID
     */
    @ColumnWidth(5)
    @ExcelProperty("id")
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
    @ExcelProperty(value = "团体类型", converter = TeamTypeConverter.class)
    private Integer type;

    /**
     * 是否已认证: 0 未认证, 1 已认证
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "认证情况", converter = TeamVerifiedConverter.class)
    private Integer isVerified;

    /**
     * 人数
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "人数")
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
    @ColumnWidth(20)
    @ExcelProperty(value = "详细地址")
    private String address;

    /**
     * 联系人名称
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "联系人名称")
    private String contactPerson;

    /**
     * 联系电话
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "联系邮箱")
    private String contactEmail;

    /**
     * 状态: 0 启用, 1 禁用
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "状态", converter = TeamStatusConverter.class)
    private Integer status;

    /**
     * 进团是否审核：0否，1是
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "进团是否审核", converter = YesOrNoConverter.class)
    private Integer isUserAuth;

    /**
     * 是否多部门管理：0否，1是
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "多部门管理", converter = YesOrNoConverter.class)
    private Integer isMultiDepartment;

    /**
     * 每月打卡次数要求，0为无
     */
    @ExcelProperty(value = "每月打卡次数要求")
    private Integer checkInNumLimit;

    /**
     * 团队健康币余额
     */
    @ColumnWidth(10)
    @ExcelProperty(value = "团队健康币余额")
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
}
