package com.zemcho.ddql.controller.wechat.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.personalCenter.Region;
import com.zemcho.ddql.mapper.personalCenter.RegionMapper;
import com.zemcho.ddql.service.personalCenter.RegionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地区信息
 */
@RestController
@RequestMapping("/wechat/personalCenter/region")
public class WechatRegionController {

    @Autowired
    private RegionService regionService;

    /**
     * 根据当前地区id查询下级地区
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/selectLowRegions")
    public Result selectLowRegions(@Valid @RequestBody Region data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return regionService.selectLowRegions(data.getId());
    }

    /**
     * 根据地区id查询上级地区
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/selectRegionParent")
    public Result selectRegionParent(@Valid @RequestBody Region data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return regionService.selectRegionParent(data.getId());
    }

    /**
     * 根据地区id查询地区信息
     * @param data
     * @param result
     * @return
     */
    @RequestMapping("/selectRegionById")
    public Result selectRegionById(@Valid @RequestBody Region data, BindingResult result) {
        if (result.hasErrors()) {
            return Result.error(result.getFieldError().getDefaultMessage());
        }
        return regionService.selectRegionById(data.getId());
    }
}
