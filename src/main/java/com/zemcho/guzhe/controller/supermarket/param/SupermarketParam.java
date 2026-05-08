package com.zemcho.guzhe.controller.supermarket.param;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @author HXH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupermarketParam {
    /**
     * 商超id
     */
    private Integer id;

    /**
     * 封面图
     */
    @NotBlank(message = "封面图不能为空")
    private String coverImage;

    /**
     * 轮播图
     */
    @NotEmpty(message = "轮播图不能为空")
    private List<String> logoImage;

    private  String logoImageUrl;

    /**
     * 商超名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 商超描述
     */
    private String description;

    /**
     * 商超营业时间
     */
    private String businessHours;

    /**
     * 商超地址
     */
    @NotBlank(message = "商超地址不能为空")
    private String address;

    /**
     * 商超经度
     */
    private String longitude;

    /**
     * 商超纬度
     */
    private String latitude;

    /**
     * 启用状态 0禁用 1启用
     */
    private Integer status;
}
