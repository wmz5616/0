package com.zemcho.ddql.service.business;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.ChangeOneParam;
import com.zemcho.ddql.common.param.DeleteParam;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.business.param.ShopParam;

public interface ShopService {

    /**
     * 新增/编辑店铺
     * @param param
     * @return
     */
    Result saveShop(ShopParam param);


    /**
     * 删除店铺
     * @param param
     * @return
     */
    Result deleteShop(DeleteParam param);

    /**
     * 获取店铺列表
     * @param param
     * @return
     */
    Result selectList(SearchParam param);

    /**
     * 禁用/启用店铺
     * @param param
     * @return
     */
    Result updateStatus(ChangeOneParam param);

    /**
     * 获取店铺详情
     * @param param
     * @return
     */
    Result selectById(SearchParam param,Boolean isWechat,String token);


    /**
     * 启用、禁用消费置顶
     * @param param 商家id和目标状态
     * @return result
     */
    Result updateTopConsumptionStatus(SearchParam param);

    /**
     * 修改收款相关配置
     * @param param searchId 商家ID searchType 目标状态 searchField1 绑定商户号 searchField2 抽成比例 单位百分之一
     * @return result
     */
    Result updateReceiptConfig(SearchParam param);

    /**
     * 修改合同照片
     * @param param searchId 商家ID searchStrField1 合同照片
     * @return result
     */
    Result updateContract(SearchParam param);

    Result updateShopStatus(SearchParam param);

    Result generateQrCode(SearchParam param,String token,Boolean isWechat);

    Result downloadQrCode(SearchParam param,String token,Boolean isWechat);


}
