package com.zemcho.guzhe.service.open.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.kuaidi100.sdk.response.SubscribePushParamResp;
import com.kuaidi100.sdk.response.SubscribePushResult;
import com.kuaidi100.sdk.response.SubscribeResp;
import com.kuaidi100.sdk.utils.SignUtils;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.entity.express.ExpressOrder;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.mapper.express.ExpressOrderMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.service.open.ExpressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @title: IExpressService
 * @Description:
 * @Date: 2025/10/20 18:46
 */
@Service
public class IExpressService implements ExpressService {
    private static final Logger log = LoggerFactory.getLogger(IExpressService.class);

    @Autowired
    private ExpressOrderMapper expressOrderMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    /**
     * 快递100订阅回调
     *
     * @param param
     * @return
     */
    @Override
    public SubscribeResp subscribeCallback(String sign, String param) {
        try {
            log.info("快递100订阅回调 param : {} sign : {}", param, sign);

//            String sign = param.getSign();
//            String paramStr = param.getParam();
            if (param == null) {
                return null;
            }

            if (sign != null && !sign.isEmpty()) {
                //订阅时传的salt,没有可以忽略
                String salt = "";
                String ourSign = SignUtils.sign(param + salt);

                //加密如果相等，属于快递100推送；否则可以忽略掉当前请求
                if (!ourSign.equals(sign)) {
                    log.error("快递100订阅回调签名验证失败: sign : {} ourSign : {}", sign, ourSign);
                    return null;
                }
            }

            SubscribePushParamResp resp = new Gson().fromJson(param, SubscribePushParamResp.class);

            String status = resp.getStatus();
            SubscribePushResult lastResult = resp.getLastResult();
            String expressCompanyCode = lastResult.getCom();
            String expressNo = lastResult.getNu();
            String state = lastResult.getState();
            Integer expressOrderStatus = Integer.valueOf(state);

            SearchParam searchParam = new SearchParam();
            searchParam.setSearchStrField2(expressCompanyCode);
            searchParam.setSearchStrField3(expressNo);
            List<ExpressOrder> expressOrderList = expressOrderMapper.selectLists(searchParam);
            if (expressOrderList == null || expressOrderList.isEmpty()) {
                log.error("快递100订阅回调匹配运单信息失败: expressCompanyCode : {} expressNo : {}", expressCompanyCode, expressNo);
                return null;
            }

            //更新订单物流信息
            for (ExpressOrder expressOrder : expressOrderList) {
                ExpressOrder updateOrder = new ExpressOrder();
                updateOrder.setId(expressOrder.getId());
                Boolean isUpdate = false;
                if (status.equals("abort")) { //中止
                    if (expressOrder.getIsRecord().equals(1)) {
                        updateOrder.setIsRecord(0);
                        isUpdate = true;
                    }
                } else { //polling:监控中，shutdown:结束，updateall：重新推送
                    if (!expressOrderStatus.equals(expressOrder.getStatus())) {
                        updateOrder.setStatus(expressOrderStatus);
                        updateOrder.setInfo(JSON.toJSONString(lastResult.getData()));
                        isUpdate = true;
                    }
                }

                if (isUpdate) {
                    expressOrderMapper.update(updateOrder);

                    if (expressOrder.getIsRecord() != null && !expressOrder.getIsRecord().equals(0)) {
                        Integer txnType = expressOrder.getTxnType();
                        Integer txnId = expressOrder.getTxnId();
                        switch (txnType) {
                            case 1: //商品订单
                                ProductOrder productOrderUpdate = new ProductOrder();
                                productOrderUpdate.setId(txnId);
                                productOrderUpdate.setExpressStatus(expressOrderStatus);
                                if (expressOrderStatus.equals(3)) {
                                    productOrderUpdate.setStatus(4);
                                } else if (!expressOrderStatus.equals(-2) && !expressOrderStatus.equals(-1)) {
                                    productOrderUpdate.setStatus(3);
                                }
                                productOrderMapper.update(productOrderUpdate);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            SubscribeResp subscribeResp = new SubscribeResp();
            subscribeResp.setResult(Boolean.TRUE);
            subscribeResp.setReturnCode("200");
            subscribeResp.setMessage("成功");

            return subscribeResp;
        } catch (Exception e) {
            log.error("快递100订阅回调异常", e);
            return null;
        }
    }
}
