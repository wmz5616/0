package com.zemcho.ddql.service.business;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.BusinessCircleParam;

public interface BusinessCircleService {
    /**
     * 新增商圈
     * @param param
     * @return
     */
    Result saveBusiness(BusinessCircleParam param);

    /**
     * 修改商圈
     * @param param
     * @return
     */
    Result updateBusiness(BusinessCircleParam param);

    /**
     * 删除商圈
     * @param param
     * @return
     */
    Result deleteBusiness(DeleteParam param);

    /**
     * 查询商圈列表
     * @param param
     * @return
     */
    Result selectList(SearchParam param);

    /**
     * 禁用/启用商圈
     * @param param
     * @return
     */
    Result updateStatus(ChangeOneParam param);

    /**
     * 查询商圈信息
     * @param param
     * @return
     */
    Result selectById(SearchParam param);

    Result selectCommonList(SearchParam param);
}
