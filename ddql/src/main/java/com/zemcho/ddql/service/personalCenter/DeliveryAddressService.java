package com.zemcho.ddql.service.personalCenter;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import org.springframework.stereotype.Service;

@Service
public interface DeliveryAddressService {

    Result add(DeliveryAddress data, String token);

    Result update(DeliveryAddress data);

    Result select(String token);

    Result delete(Integer id);


}
