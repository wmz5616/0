package com.zemcho.guzhe.service.supermarket.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.supermarket.param.SupermarketParam;
import com.zemcho.guzhe.controller.supermarket.vo.SupermarketInfoVo;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import com.zemcho.guzhe.mapper.supermarket.SupermarketMapper;
import com.zemcho.guzhe.service.supermarket.SupermarketService;
import com.zemcho.guzhe.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ryan
 */
@Service
public class ISupermarketService implements SupermarketService {

    @Autowired
    private SupermarketMapper supermarketMapper;

    @Override
    public Result select(SearchParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<SupermarketInfoVo> list = supermarketMapper.select(param);
        // 2. 创建 实体类 集合
        List<SupermarketInfo> infoList = new ArrayList<>();

        for (SupermarketInfoVo vo : list) {
            SupermarketInfo info = new SupermarketInfo();

            BeanUtils.copyProperties(vo, info);
            String logoImageStr = vo.getLogoImage();
            if (logoImageStr != null && !logoImageStr.isEmpty()) {
                List<String> logoList = Arrays.asList(logoImageStr.split(","));
                info.setLogoImage(logoList);
            }

            infoList.add(info);
        }


        PageInfo<SupermarketInfo> pageInfo = new PageInfo<>(infoList);
        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result add(SupermarketParam data) {
        // 检查参数
        if (data.getStatus() == null) {
            data.setStatus(1);
        }
        if (data.getStatus() != 0 && data.getStatus() != 1) {
            return Result.error("参数错误");
        }
        //校验商超名称是否唯一
        if(supermarketMapper.ifExistsByName(data.getName(), null)){
            return Result.error("商超名称已存在");
        }
        String join = String.join(",", data.getLogoImage());
        data.setLogoImageUrl(join);
        supermarketMapper.insert(data);
        return Result.success("操作成功");
    }

    @Override
    public Result delete(List<Integer> deleteIds) {
        supermarketMapper.delete(deleteIds);
        return Result.success("删除成功");
    }

    @Override
    public Result setStatus(ChangeParam param) {
        List<Integer> ids = new ArrayList<>(param.getChangeIds());

        supermarketMapper.updateStatusByIds(ids, param.getStatus());

        return Result.success("操作成功");
    }

    @Override
    public Result update(SupermarketParam data) {
        // 检查参数
        if (data.getId() == null) {
            return Result.error("参数错误");
        }
        //校验商超名称是否唯一
        if(data.getName()!=null && supermarketMapper.ifExistsByName(data.getName(), data.getId())){
            return Result.error("商超名称已存在");
        }
        if(data.getStatus()!=null && data.getStatus()!=0 && data.getStatus()!=1){
            return Result.error("参数错误");
        }
        if(data.getStatus()==null){
            data.setStatus(1);
        }
        String join = String.join(",", data.getLogoImage());
        data.setLogoImageUrl(join);
        supermarketMapper.update(data);
        return Result.success("操作成功");
    }

    @Override
    public Result get() {
        List<SupermarketInfoVo> list =  supermarketMapper.selectSuperList();
         return Result.success("操作成功",list);
    }
}
