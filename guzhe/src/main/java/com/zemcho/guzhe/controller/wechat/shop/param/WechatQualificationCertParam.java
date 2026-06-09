package com.zemcho.guzhe.controller.wechat.shop.param;

import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author HXH
 */
@Data
public class WechatQualificationCertParam{
    // 主键ID
    private Integer id;

    // 营业执照
    @NotBlank(message = "营业执照不能为空")
    private String businessLicense;

    // 其他附件
    private String otherFile;

    // 联系电话
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    // 联系邮箱
//    @NotBlank(message = "联系邮箱不能为空")
    private String email;

    /**
     * 查询id
     */
    private Integer searchId;

    /**
     * 1:申请入驻记录入口，2:首页商家列表
     */
    private Integer type;
}
