package com.zemcho.guzhe.service.supermarket;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.ChangeParam;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.controller.supermarket.param.SupermarketParam;
import java.util.List;

/**
 * @author Ryan
 */
public interface SupermarketService {

    Result select(SearchParam param);

    Result add(SupermarketParam data);

    Result delete(List<Integer> deleteIds);

    Result setStatus(ChangeParam param);

    Result update( SupermarketParam data);

    Result get();
}
