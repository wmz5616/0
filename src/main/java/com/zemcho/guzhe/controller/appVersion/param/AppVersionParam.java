package com.zemcho.guzhe.controller.appVersion.param;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppVersionParam {

    /**
     * id
     */
    private Integer id;

    /**
     * 版本编号
     */
    @NotBlank(message = "版本编号不能为空")
    private String serialNumber;

    /**
     * 文件url
     */
    @NotBlank(message = "文件不能为空")
    private String fileUrl;

    /**
     * 排序字段
     */
    @NotNull(message = "排序字段不能为空")
    private Integer release;

    /**
     * 名称
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 是否发布 0发布 1不发布
     */
    @NotNull(message = "是否发布不能为空")
    private Integer isPublish;

    /**
     * 备注
     */
    private String remark;
}
