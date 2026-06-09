package com.zemcho.ddql.service.recharge.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteOneParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.recharge.param.RechargeActivityParam;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import com.zemcho.ddql.mapper.recharge.RechargeActivityMapper;
import com.zemcho.ddql.service.recharge.RechargeActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值活动服务实现类
 *
 * @author Ryan
 */
@Service
public class RechargeActivityServiceImpl implements RechargeActivityService {

    @Autowired
    private RechargeActivityMapper rechargeActivityMapper;

    @Override
    public Result addRechargeActivity(List<RechargeActivityParam> param) {
        if (param == null || param.isEmpty()) {
            return Result.error("充值活动不能为空");
        }

        // 分类处理新增和更新
        List<RechargeActivity> toInsert = new ArrayList<>();
        List<RechargeActivity> toUpdate = new ArrayList<>();

        for (RechargeActivityParam rechargeActivityParam : param) {
            if (rechargeActivityParam.getRechargeAmount() <= 0) {
                return Result.error("充值金额必须大于0");
            }
            RechargeActivity rechargeActivity = new RechargeActivity();
            BeanUtils.copyProperties(rechargeActivityParam, rechargeActivity);
            if (rechargeActivity.getId() == null || rechargeActivity.getId() == 0) {
                // 新增
                rechargeActivity.setCreateTime(LocalDateTime.now());
                rechargeActivity.setUpdateTime(LocalDateTime.now());
                toInsert.add(rechargeActivity);
            } else {
                // 更新
                toUpdate.add(rechargeActivity);
            }
        }

        if (!toInsert.isEmpty()) {
            rechargeActivityMapper.insertBatch(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            rechargeActivityMapper.updateBatch(toUpdate);
        }
        return Result.success("操作成功");
    }

    /**
     * 获取充值活动列表
     *
     * @param param
     * @return
     */
    @Override
    public Result selectList(SearchParam param) {
        List<RechargeActivity> rechargeActivityList = rechargeActivityMapper.selectList(param);
        return Result.success("操作成功", rechargeActivityList);
    }

    /**
     * 删除充值活动
     *
     * @param param
     * @return
     */
    @Override
    public Result deleteRechargeActivity(DeleteOneParam param) {
        Integer id = param.getDeleteId();
        RechargeActivity rechargeActivity = rechargeActivityMapper.selectById(id);
        rechargeActivityMapper.deleteById(id);
        Integer deletedSort = rechargeActivity.getSort();
        if (deletedSort != null) {
            rechargeActivityMapper.updateSortAfterDelete(deletedSort);
        }
        return Result.success("操作成功");
    }

    /**
     * 排序充值活动
     *
     * @param param
     * @return
     */
    @Override
    public Result sortRechargeActivity(SearchParam param) {
        List<Integer> rechargeIds = param.getSearchIds();
        if (rechargeIds == null || rechargeIds.isEmpty()) {
            return Result.error("充值活动列表不能为空");
        }
        // 根据id顺序批量更新sort字段
        List<RechargeActivity> rechargeActivityList = new ArrayList<>();
        for (int i = 0; i < rechargeIds.size(); i++) {
            RechargeActivity rechargeActivity = rechargeActivityMapper.selectById(rechargeIds.get(i));
            rechargeActivity.setSort(i + 1); // sort从1开始
            rechargeActivityList.add(rechargeActivity);
        }
        // 批量更新
        rechargeActivityMapper.updateBatch(rechargeActivityList);
        return Result.success("操作成功");
    }
}
