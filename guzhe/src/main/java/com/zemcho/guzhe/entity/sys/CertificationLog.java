package com.zemcho.guzhe.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @title: CertificationLog
 * @Description:
 * @Date: 2025/10/10 15:34
 */
@Data
public class CertificationLog {
    // 主键ID
    private Integer id;

    // 身份证号码
    private String cardNum;

    // 姓名
    private String name;

    // 生日
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // 籍贯
    private String area;

    // 实名认证详情json格式
    private String result;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
