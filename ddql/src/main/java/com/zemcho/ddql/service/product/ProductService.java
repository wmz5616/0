package com.zemcho.ddql.service.product;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.product.param.ProductParam;
import com.zemcho.ddql.controller.product.param.ProductSearchParam;
import com.zemcho.ddql.controller.product.param.StockParam;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    /**
     * 新增商品
     *
     * @return
     */
    Result saveProduct(ProductParam param);

    /**
     * 获取商品列表
     *
     * @param param
     * @return
     */
    Result selectList(ProductSearchParam param);

    /**
     * 导出商品数据
     *
     * @param param
     * @param response
     */
    void productDataExport(ProductSearchParam param, HttpServletResponse response);

    /**
     * 根据id批量删除商品
     *
     * @return
     */
    Result deleteProduct(DeleteParam param);

    /**
     * 更新商品库存
     *
     * @return
     */
    Result updateStock(StockParam param);

    /**
     * 导入券码
     *
     * @param file
     * @param productId
     * @return
     */
    Result importTicket(MultipartFile file, Integer productId);

    /**
     * 导出券码
     *
     * @param param
     * @param response
     */
    void exportTicket(SearchParam param, HttpServletResponse response);

    /**
     * 获取商品信息
     *
     * @param param
     * @return
     */
    Result getProduct(SearchParam param);

}
