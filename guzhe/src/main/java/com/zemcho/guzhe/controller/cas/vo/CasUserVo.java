package com.zemcho.guzhe.controller.cas.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.util.excel.converter.user.UserCertificateConverter;
import com.zemcho.guzhe.util.excel.converter.user.UserLockConverter;
import com.zemcho.guzhe.util.excel.converter.user.UserSexConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CasUserVo extends CasUser{

}
