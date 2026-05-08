package com.zemcho.guzhe.service.shop.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.shop.param.QualificationCertParam;
import com.zemcho.guzhe.entity.shop.QualificationCert;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.mapper.shop.QualificationCertMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.service.shop.QualificationCertService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class IQualificationCertService implements QualificationCertService {
    @Autowired
    private QualificationCertMapper qualificationCertMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public Result addQualificationCert(QualificationCertParam param, String token) {
        // 获取用户信息
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if(authJwtData == null){
            return Result.error("参数错误");
        }
        Integer userId = authJwtData.getAdminId();
        // 检查该商家是否已有资质认证记录 只有无记录或者只有被驳回的记录才能提交 下面查询待审核和已通过的记录
        QualificationCert existingCert = qualificationCertMapper.selectExistByShopId(param.getShopId());
        if (existingCert != null ) {
            return Result.error("该商家已有待审核的资质认证申请");
        }

        QualificationCert qualificationCert = new QualificationCert();
        BeanUtils.copyProperties(param, qualificationCert);
        
        // 设置默认值
        qualificationCert.setCertResult(0); // 默认未审核
        qualificationCert.setSubmitTime(LocalDateTime.now());
        qualificationCert.setCreateTime(LocalDateTime.now());
        qualificationCert.setUpdateTime(LocalDateTime.now());
        qualificationCert.setRejectReason("");
        qualificationCert.setUserId(userId);
        qualificationCert.setCertSide(1); // 后台

        int result = qualificationCertMapper.insert(qualificationCert);
        if (result > 0) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @Override
    public Result updateQualificationCert(QualificationCertParam param) {
        if (param.getId() == null || param.getId() <= 0) {
            return Result.error("参数错误");
        }
        
        QualificationCert existingCert = qualificationCertMapper.selectById(param.getId());
        if (existingCert == null) {
            return Result.error("记录不存在");
        }
        
        // 只有待审核状态才能修改
        if (existingCert.getCertResult() != 0) {
            return Result.error("只能修改待审核的资质认证信息");
        }
        
        QualificationCert qualificationCert = new QualificationCert();
        BeanUtils.copyProperties(param, qualificationCert);
        qualificationCert.setUpdateTime(LocalDateTime.now());
        qualificationCert.setSubmitTime(LocalDateTime.now());
        
        int result = qualificationCertMapper.update(qualificationCert);
        if (result > 0) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }


    @Override
    public Result getByShopId(SearchParam param) {
        if (param.getSearchField1() == null) {
            return Result.error("参数错误");
        }
        
        QualificationCert qualificationCert = qualificationCertMapper.selectByShopId(param.getSearchField1());
        if (qualificationCert == null) {
            return Result.error("暂无资质认证记录");
        }
        return Result.success("操作成功", qualificationCert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result auditQualificationCert(SearchParam param, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if(authJwtData == null){
            return Result.error("参数错误");
        }
        Integer certUser = authJwtData.getAdminId();
        
        QualificationCert existingCert = qualificationCertMapper.selectById(param.getSearchId());
        if (existingCert == null) {
            return Result.error("参数错误");
        }

        Shop shop = shopMapper.selectById(existingCert.getShopId());
        if (shop == null) {
            return Result.error("参数错误");
        }

        if(param.getSearchType() == null || (!param.getSearchType().equals(1) && !param.getSearchType().equals(2))){
            return Result.error("参数错误");
        }

        if(param.getSearchType().equals(2) && param.getKeyword() == null) {
            return Result.error("参数错误");
        }

        // 只有待审核状态才能审核
        if (existingCert.getCertResult() != 0) {
            return Result.error("只能审核待审核的资质认证记录");
        }
        
        QualificationCert qualificationCert = new QualificationCert();
        qualificationCert.setId(param.getSearchId());
        qualificationCert.setCertResult(param.getSearchType());
        qualificationCert.setCertUser(certUser);
        qualificationCert.setRejectReason(param.getKeyword());
        qualificationCert.setUpdateTime(LocalDateTime.now());
        
        qualificationCertMapper.update(qualificationCert);

        shop.setQualificationCert(param.getSearchType());
        shopMapper.update(shop);
        return Result.error("操作失败");
    }
}