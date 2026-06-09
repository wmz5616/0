package com.zemcho.ddql.service.cas;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.cas.vo.CasUserVo;
import com.zemcho.ddql.entity.cas.CasUser;

import java.util.List;

public interface UserService {
    public Result updateUser(CasUser user);

    public Result selectUserList(SearchParam param);

    public Result selectUserDetail(Integer id);

    public List<CasUserVo> selectByIds(SearchParam param);
}
