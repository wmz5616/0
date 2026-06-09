package com.zemcho.guzhe.controller.merchant.vo;

import com.zemcho.guzhe.entity.merchant.Merchant;
import lombok.Data;

import java.util.List;

/**
 * @title: MerchantListVo
 * @Description:
 * @Date: 2025/9/5 15:05
 */
@Data

public class MerchantListVO extends Merchant {
    //关联的门店列表
    private List<ShopMerchantVO> shopListMerchantVO;
}
