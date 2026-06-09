package com.zemcho.ddql.service.order.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.entity.cas.CasUserWithdrawal;
import com.zemcho.ddql.mapper.cas.CasUserWithdrawalMapper;
import com.zemcho.ddql.service.order.WithdrawalOrderService;
import com.zemcho.ddql.util.excel.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: IWithdrawalOrderService
 * @Description:
 * @Date: 2025/10/15 17:57
 */
@Service
public class IWithdrawalOrderService implements WithdrawalOrderService {
    @Autowired
    private CasUserWithdrawalMapper casUserWithdrawalMapper;

    /**
     * 获取提现列表
     *
     * @param param
     * @return
     */
    @Override
    public Result withdrawalLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<CasUserWithdrawal> list = casUserWithdrawalMapper.selectList(param);
        PageInfo<CasUserWithdrawal> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 导出提现数据
     *
     * @param param
     * @param response
     */
    @Override
    public void withdrawalExport(SearchParam param, HttpServletResponse response) {
        List<CasUserWithdrawal> list = casUserWithdrawalMapper.selectList(param);
        ExcelUtil.exportToWeb(response, list, "提现记录信息", "提现记录信息", CasUserWithdrawal.class);
    }

    /**
     * 获取提现详情
     *
     * @param param
     * @return
     */
    @Override
    public Result withdrawalInfo(SearchParam param) {
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数异常");
        }

        CasUserWithdrawal info = casUserWithdrawalMapper.selectById(id);

        return Result.success("获取成功", info);
    }
}
