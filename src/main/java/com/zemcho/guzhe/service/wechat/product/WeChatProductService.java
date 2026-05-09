package com.zemcho.guzhe.service.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;

/**
 * @author HXH
 */
public interface WeChatProductService {
    Result select(ProductSearchParam param, String token);

    Result listCategory(SearchParam param, String token);

    Result selectByCategoryId(SearchParam param, String token);

    Result delete(DeleteParam param, String token);

    Result updateStatus(ChangeParam param, String token);
}
