package com.zemcho.guzhe.service.product;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.product.param.ProductParam;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.controller.product.param.StockParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HXH
 */
public interface ProductService {

    /**
     * 新增商品
     *
     * @return
     */
    Result saveProduct(ProductParam param);

    Result selectList(ProductSearchParam param);

    void productDataExport(ProductSearchParam param, HttpServletResponse response);

    Result getProduct(SearchParam param);

    Result deleteProduct(DeleteParam param);

    Result updateStock(StockParam param);

    void exportTicket(SearchParam param, HttpServletResponse response);

    Result importTicket(MultipartFile file, Integer productId);
}
