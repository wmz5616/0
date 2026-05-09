package com.zemcho.guzhe.service.merchant;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.merchant.param.MerchantSaveParam;
import com.zemcho.guzhe.controller.merchant.param.UploadMerchantImageParam;
import com.zemcho.guzhe.entity.merchant.Merchant;

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

    // id 必传
    Result update(MerchantSaveParam param, String token, Boolean isWechat);

    Result delete(Integer id);

    // keyword(merchant_name contact_phone legal_person)  searchType(main_type) searchField1(acc_type)
    // searchIntStatus(status) pageNum pageSize
    Result select(SearchParam param);

    Result selectById(Integer id, String token, Boolean isWechat, Integer shopId);

    /**
     * 更改启用状态
     *
     * @param param
     * @return
     */
    Result setStatus(SearchParam param);
}
