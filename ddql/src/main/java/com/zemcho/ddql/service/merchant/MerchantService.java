package com.zemcho.ddql.service.merchant;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.merchant.param.MerchantSaveParam;
import com.zemcho.ddql.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.ddql.entity.merchant.Merchant;

public interface MerchantService {

    void checkProcess();

    /**
     * 商户图片上传
     *
     * @param param
     * @return
     */
    Result uploadMerchantImage(UploadMerchantImageParam param);

    Result add(MerchantSaveParam param, String token, Boolean isWechat);

    // id 回传
    Result update(MerchantSaveParam param, String token, Boolean isWechat);

    Result delete(Integer id);

    // keyword(merchant_name contact_phone legal_person)  searchType(main_type) searchField1(acc_type)
    // searchIntStatus(status) pageNum pageSize
    Result select(SearchParam param);

    /**
     * 查询启用状态商户列表 id 商户名称
     *
     * @return 结果
     */
    Result selectEnabledList();

    Result selectById(Integer id, String token, Boolean isWechat, Integer shopId);

    /**
     * 更改状态
     *
     * @param param
     * @return
     */
    Result setStatus(SearchParam param);
}
