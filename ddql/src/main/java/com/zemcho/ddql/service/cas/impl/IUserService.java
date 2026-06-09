package com.zemcho.ddql.service.cas.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.vo.CasUserVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.service.cas.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserService implements UserService {

    @Autowired
    private CasUserMapper casUserMapper;

    @Override
    public Result updateUser(CasUser user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        // 如果更新手机号的话需要判断是否已经存在了
        if (user.getPhone() != null && casUserMapper.existByPhone(user.getPhone())) {
            return Result.error("该手机号已存在");
        }
        // 更新
        casUserMapper.update(user);
        return Result.success("操作成功");
    }

    @Override
    public Result selectUserList(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<CasUserVo> list = casUserMapper.selectLists(param);
        PageInfo<CasUserVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    @Override
    public Result selectUserDetail(Integer id) {
        return Result.success("操作成功", casUserMapper.selectDetailById(id));
    }

    @Override
    public List<CasUserVo> selectByIds(SearchParam param) {
        return casUserMapper.selectLists(param);
    }
}
