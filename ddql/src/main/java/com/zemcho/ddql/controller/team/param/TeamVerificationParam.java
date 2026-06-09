package com.zemcho.ddql.controller.team.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamVerificationParam {

    private Integer id;

    /**
     * 团队id
     */
    private Integer teamId;


    /**
     * 证件类型 0营业执照 1法人证书
     */
    private Integer licenseType;

    /**
     * 营业执照或者法人证书 (多张图片路径以分号";"分隔)
     */
    private List<String> licenseImageList;

    /**
     * 附件 (多张图片路径以分号";"分隔)
     */
    private List<String> additionPictureList;

    /**
     * 审核的类型：0 企事业单位, 1 政府部门, 2 家庭, 3 朋友
     */
    private Integer verificationType;

    /**
     * 审核方式: 0 正常审核, 1 人工审核
     */
    private Integer type;

    /**
     * 审核状态: 0 审核中, 1 审核通过, 2 审核驳回
     */
    private Integer status;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

}
