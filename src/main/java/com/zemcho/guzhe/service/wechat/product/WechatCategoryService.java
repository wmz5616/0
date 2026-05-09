package com.zemcho.guzhe.service.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ServeTypeRequest;

/**
 * @author HXH
 */
public interface WechatCategoryService {
    Result addCategory(ServeTypeRequest param,String token);

    Result selectList(SearchParam param);

    Result deleteCategory(DeleteOneParam param);

    Result sortCategory(SearchParam param);
}
