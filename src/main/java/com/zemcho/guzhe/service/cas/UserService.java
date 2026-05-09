package com.zemcho.guzhe.service.cas;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.cas.param.CasUserParam;
import com.zemcho.guzhe.controller.cas.vo.CasUserVo;
import com.zemcho.guzhe.entity.cas.CasUser;

import java.util.List;

public interface UserService {

     Result selectUserList(SearchParam param);


     List<CasUserVo> selectByIds(SearchParam param);

     Result updateStatus(CasUserParam param);
}
