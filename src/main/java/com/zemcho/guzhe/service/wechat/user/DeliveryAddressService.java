package com.zemcho.guzhe.service.wechat.user;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.DeleteOneParam;
import com.zemcho.guzhe.controller.wechat.user.param.DeliveryAddressSaveParam;
import org.springframework.stereotype.Service;

@Service
public interface DeliveryAddressService {
    /**
     * 添加收货地址
     *
     * @param param
     * @param token
     * @return
     */
    Result add(DeliveryAddressSaveParam param, String token);

    /**
     * 修改收货地址
     *
     * @param param
     * @param token
     * @return
     */
    Result update(DeliveryAddressSaveParam param, String token);

    /**
     * 获取收货地址列表
     *
     * @param token
     * @return
     */
    Result getList(String token);

    /**
     * 删除收货地址
     *
     * @param param
     * @return
     */
    Result delete(DeleteOneParam param, String token);
}
