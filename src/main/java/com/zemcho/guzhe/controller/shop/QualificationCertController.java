package com.zemcho.guzhe.controller.shop;

import com.zemcho.guzhe.aspect.log.Log;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.service.shop.QualificationCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/qualification")
public class QualificationCertController {
    @Autowired
    private QualificationCertService qualificationCertService;

    /**
     * 新增资质认证
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "新增资质认证", module = "商家管理")
    @RequestMapping("/add")
    public Result addQualificationCert(@Validated @RequestBody QualificationCertParam param, BindingResult result,
                                       @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return qualificationCertService.addQualificationCert(param, token);
    }

    /**
     * 更新资质认证
     *
     * @param param  参数对象
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "更新资质认证", module = "商家管理")
    @RequestMapping("/update")
    public Result updateQualificationCert(@Validated @RequestBody QualificationCertParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return qualificationCertService.updateQualificationCert(param);
    }


    /**
     * 根据商家ID查询资质认证
     *
     * @param param  查询参数
     * @param result 验证结果
     * @return 结果
     */
    @RequestMapping("/get")
    public Result getByShopId(@Validated @RequestBody SearchParam param, BindingResult result) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return qualificationCertService.getByShopId(param);
    }

    /**
     * 审核资质认证
     *
     * @param param  审核参数
     * @param result 验证结果
     * @return 结果
     */
    @Log(description = "审核资质认证", module = "商家管理")
    @RequestMapping("/audit")
    public Result auditQualificationCert(@Validated @RequestBody SearchParam param, BindingResult result,
                                         @RequestHeader("token") String token) {
        if (result.hasErrors()) {
            return new Result(10002, result.getFieldError().getDefaultMessage());
        }
        return qualificationCertService.auditQualificationCert(param, token);
    }
}
