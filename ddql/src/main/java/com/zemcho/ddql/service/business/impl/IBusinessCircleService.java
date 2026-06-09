package com.zemcho.ddql.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.BusinessCircleParam;
import com.zemcho.ddql.controller.business.vo.BusinessCircleVo;
import com.zemcho.ddql.controller.common.vo.BusinessCircleCommonVo;
import com.zemcho.ddql.entity.business.BusinessCircle;
import com.zemcho.ddql.mapper.business.BusinessCircleMapper;
import com.zemcho.ddql.mapper.business.BusinessCircleShopMapper;
import com.zemcho.ddql.mapper.business.ShopMapper;
import com.zemcho.ddql.service.business.BusinessCircleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IBusinessCircleService implements BusinessCircleService {

    @Autowired
    private BusinessCircleMapper businessCircleMapper;

    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 新增商圈
     *
     * @param param
     * @return
     */
    @Override
    public Result saveBusiness(BusinessCircleParam param) {
        String name = param.getName();
        BusinessCircle businessCircle = businessCircleMapper.selectByName(null, name);
        if (businessCircle != null) {
            return Result.error("商圈名称已存在");
        }
        businessCircle = new BusinessCircle();
        BeanUtils.copyProperties(param, businessCircle, "logoImageUrl");
        if (param.getLogoImageUrl() != null && !param.getLogoImageUrl().isEmpty()) {
            businessCircle.setLogoImageUrl(JSON.toJSONString(param.getLogoImageUrl()));
        }
        businessCircle.setCreateTime(LocalDateTime.now());
        businessCircle.setUpdateTime(LocalDateTime.now());
        businessCircleMapper.insert(businessCircle);
        return Result.success("操作成功");
    }

    /**
     * 修改商圈
     *
     * @param param
     * @return
     */
    @Override
    public Result updateBusiness(BusinessCircleParam param) {
        Integer id = param.getId();
        if (id == null || id <= 0) {
            return Result.error("id错误");
        }
        BusinessCircle businessCircle = businessCircleMapper.selectByName(id, param.getName());
        if (businessCircle != null) {
            return Result.error("商圈名称已存在");
        }

        businessCircle = businessCircleMapper.selectById(id);
        if (businessCircle == null) {
            return Result.error("记录不存在");
        }

        BeanUtils.copyProperties(param, businessCircle, "logoImageUrl");
        if (param.getLogoImageUrl() != null && !param.getLogoImageUrl().isEmpty()) {
            businessCircle.setLogoImageUrl(JSON.toJSONString(param.getLogoImageUrl()));
        }
        businessCircleMapper.update(businessCircle);
        return Result.success("操作成功");
    }

    /**
     * 删除商圈
     *
     * @param param
     * @return
     */
    @Override
    public Result deleteBusiness(DeleteParam param) {
        Boolean existShop = businessCircleShopMapper.existShop(param.getDeleteIds());
        if (existShop) {
            return Result.error("商圈下绑定了店铺，请先删除店铺");
        }
        businessCircleMapper.deleteByIds(param.getDeleteIds());
        return Result.success("操作成功");
    }

    /**
     * 查询商圈列表
     *
     * @param param
     * @return
     */
    @Override
    public Result selectList(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<BusinessCircleVo> businessCircles = businessCircleMapper.selectList(param);
        // 获取商圈id列表
        List<Integer> circleIds = businessCircles.stream().map(BusinessCircleVo::getId).collect(Collectors.toList());
        if (circleIds == null || circleIds.isEmpty()) {
            return Result.success("成功", new PageInfo<>(businessCircles));
        }
        // 获取商圈下绑定的店铺列表
        List<Map<String, Object>> shopVoList = shopMapper.selectByCircleIds(circleIds);
        Map<Object, List<Map<String, Object>>> shopNameMap =
                shopVoList.stream().collect(Collectors.groupingBy(item -> item.get("circleId")));
        for (BusinessCircleVo item : businessCircles) {
            List<Map<String, Object>> list =
                    Optional.ofNullable(shopNameMap.get(item.getId())).orElse(Collections.emptyList());
            List<String> shopNameList =
                    list.stream().map(vo -> (String) vo.get("shopName")).collect(Collectors.toList());
            item.setShopNameList(shopNameList);
            item.setLogoImageUrlList(JSON.parseArray(item.getLogoImageUrl(), String.class));
        }
        PageInfo<BusinessCircleVo> pageInfo = new PageInfo<>(businessCircles);
        return Result.success("操作成功", pageInfo);
    }

    /**
     * 禁用/启用商圈
     *
     * @param param
     * @return
     */
    @Override
    public Result updateStatus(ChangeOneParam param) {
        BusinessCircle businessCircle = businessCircleMapper.selectById(param.getChangeId());
        if (businessCircle == null) {
            return Result.error("记录不存在");
        }
        if (param.getStatus().equals(businessCircle.getStatus())) {
            return Result.success("操作成功!");
        }
        businessCircle.setStatus(param.getStatus());
        businessCircleMapper.update(businessCircle);
        return Result.success("操作成功");
    }

    /**
     * 根据id查询商圈信息
     *
     * @param param
     * @return
     */
    @Override
    public Result selectById(SearchParam param) {
        Integer searchId = param.getSearchId();
        if (searchId == null) {
            return Result.error("参数错误");
        }
        BusinessCircle businessCircle = businessCircleMapper.selectById(searchId);
        BusinessCircleVo businessCircleVo = new BusinessCircleVo();
        BeanUtils.copyProperties(businessCircle, businessCircleVo);
        businessCircleVo.setLogoImageUrlList(JSON.parseArray(businessCircle.getLogoImageUrl(), String.class));
        return Result.success("操作成功", businessCircleVo);
    }

    @Override
    public Result selectCommonList(SearchParam param) {
        List<BusinessCircleCommonVo> businessCircleCommonVos = businessCircleMapper.selectCommonList(param);
        return Result.success("操作成功", businessCircleCommonVos);
    }
}
