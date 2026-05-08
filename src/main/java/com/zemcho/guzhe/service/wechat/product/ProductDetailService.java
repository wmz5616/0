package com.zemcho.guzhe.service.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;

/**
 * @author HXH
 */
public interface ProductDetailService {
    Result selectDetail(SearchParam param, String token);

    Result saveProduct(ProductParam param, String token);
}
