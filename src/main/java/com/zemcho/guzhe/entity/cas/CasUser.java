package com.zemcho.guzhe.entity.cas;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.util.excel.converter.user.UserCertificateConverter;
import com.zemcho.guzhe.util.excel.converter.user.UserLockConverter;
import com.zemcho.guzhe.util.excel.converter.user.UserSexConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @title: CasUser
 * @Description:
 * @Date: 2025/5/6 17:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CasUser {
    //用户id
    @ExcelProperty(value = "用户id")
    @ColumnWidth(5)
    private Integer id;

    //昵称
    @ExcelProperty(value = "昵称")
    @ColumnWidth(10)
    private String nickname;

    //手机号码
    @ExcelProperty(value = "手机号码")
    @ColumnWidth(15)
    private String phone;

    //头像
    @ExcelIgnore
    private String avatar;

    //性别 0:未知 1:男 2:女
    @ExcelProperty(value = "性别", converter = UserSexConverter.class)
    @ColumnWidth(5)
    private Integer sex;

    //微信openid
    @ExcelIgnore
    private String openId;

    //微信unionid
    @ExcelIgnore
    private String unionId;

    //是否已实名认证：0否，1是
    @ExcelProperty(value = "是否已实名认证",converter = UserCertificateConverter.class)
    @ColumnWidth(5)
    private Integer hasCertification;

    //身份证号码
    @ExcelProperty(value = "身份证号码")
    @ColumnWidth(20)
    private String cardNum;

    //姓名
    @ExcelProperty(value = "姓名")
    @ColumnWidth(8)
    private String name;

    //生日
    @ExcelProperty(value = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(value = "yyyy-MM-dd")
    @ColumnWidth(15)
    private LocalDate birthDate;

    //籍贯
    @ExcelProperty(value = "籍贯")
    @ColumnWidth(7)
    private String area;

    //用户锁 0正常 1暂时锁定 2永久锁定
    @ExcelProperty(value = "用户状态",converter = UserLockConverter.class)
    @ColumnWidth(5)
    private Integer lock;

    //锁有效期
    @ExcelProperty(value = "锁有效期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime lockExpiredAt;

    //锁定原因
    @ExcelProperty(value = "锁定原因")
    @ColumnWidth(10)
    private String lockReason;

    //管理员备注
    @ExcelProperty(value = "管理员备注")
    @ColumnWidth(10)
    private String remark;

    //管理员id
    @ExcelIgnore
    private Integer adminId;

    //创建时间
    @ExcelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    //更新时间
    @ExcelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime updateTime;
}