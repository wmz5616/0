package com.zemcho.guzhe.service.wechat.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.param.StockParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HXH
 */
public interface ProductDetailService {
    Result selectDetail(SearchParam param, String token,Boolean isWechat);

    Result saveProduct(ProductParam param, String token,Boolean isWechat);

    Result updateStock(StockParam param,Boolean isWechat);

    Result importTicket(MultipartFile file, Integer productId,Boolean isWechat);

    void exportTicket(SearchParam param, HttpServletResponse response, Boolean b);
}
