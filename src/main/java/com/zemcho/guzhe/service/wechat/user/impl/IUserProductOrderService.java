package com.zemcho.guzhe.service.wechat.user.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.config.tgy_pay.MerchantConfig;
import com.zemcho.guzhe.controller.order.vo.ExpressOrderVo;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderAddParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderRefundRefundParam;
import com.zemcho.guzhe.controller.wechat.user.param.ProductOrderUpdateBaseParam;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderAddressVo;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderCountVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.log.PayLog;
import com.zemcho.guzhe.entity.order.*;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.entity.user.DeliveryAddress;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.mapper.express.ExpressOrderMapper;
import com.zemcho.guzhe.mapper.log.PayLogMapper;
import com.zemcho.guzhe.mapper.order.*;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.product.ProductTicketMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.user.DeliveryAddressMapper;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.service.wechat.user.UserProductOrderService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisLockUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import com.zemcho.guzhe.util.tgy.SignUtil;
import com.zemcho.guzhe.util.tgy.TgyPayUtil;
import com.zemcho.guzhe.util.tgy.dto.WxJsPayCallBackResponse;
import com.zemcho.guzhe.util.uuid.OrderNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @title: IProductOrderService
 * @Description:
 * @Date: 2026/4/28 8:58
 */
@Service
@Slf4j
public class IUserProductOrderService implements UserProductOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductOrderAddressMapper productOrderAddressMapper;

    @Autowired
    private ProductOrderLogMapper productOrderLogMapper;

    @Autowired
    private ProductOrderRefundApplyMapper productOrderRefundApplyMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private ExpressOrderMapper expressOrderMapper;

    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private MerchantConfig merchantConfig;

    /**
     * 添加商品订单
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result addProductOrder(ProductOrderAddParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 上锁
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "addProductOrder:" + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();

        Integer productId = param.getProductId();
        Product productInfo = productMapper.selectById(productId);
        if (productInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("商品不存在");
        }
        if (productInfo.getStatus() == 2) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("商品未上架");
        }
        if (productInfo.getStatus() == 3
                && (productInfo.getScheduledTime() == null || productInfo.getScheduledTime().isAfter(now))) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("商品未上架!");
        }

        Integer num = param.getNum();
        if (productInfo.getStock() < num) { //目前时间不够暂时不使用redis预热
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("商品库存不足");
        }
        Integer amount = num * productInfo.getAmount();

        Integer addressId = param.getAddressId();
        DeliveryAddress addressInfo = null;
        LocalDateTime deadline = null;
        if (productInfo.getIsVirtual() == 0) {
            if (amount == 0 && (addressId == null || addressId <= 0)) {
                // 解锁
                redisLockUtil.unlock(lockKey);
                return Result.error("请选择收货地址");
            }

            if (addressId != null && addressId > 0) {
                addressInfo = deliveryAddressMapper.selectById(addressId);
                if (addressInfo == null) {
                    // 解锁
                    redisLockUtil.unlock(lockKey);
                    return Result.error("收货地址不存在");
                }
                if (!addressInfo.getUserId().equals(userId)) {
                    // 解锁
                    redisLockUtil.unlock(lockKey);
                    return Result.error("您无权选择该收货地址");
                }
            }
        } else {
            if (productInfo.getTimeLimit() <= 0) {
                // 解锁
                redisLockUtil.unlock(lockKey);
                return Result.error("该商品暂未设置有效期，请联系管理员");
            }
            deadline = now.plusDays(productInfo.getTimeLimit());
        }

        Shop shopInfo = shopMapper.selectById(productInfo.getShopId());
        if (shopInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("商品商家信息有误，请联系管理员");
        }

        Integer equipmentId = param.getEquipmentId();
        String serialNumber = "";
        if (equipmentId != null && equipmentId > 0) {
            Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
            if (equipmentInfo == null) {
                // 解锁
                redisLockUtil.unlock(lockKey);
                return Result.error("设备不存在");
            }
            serialNumber = equipmentInfo.getSerialNumber();
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }

        List<String> ticketList = null;
        if (productInfo.getIsVirtual() != 0) {
            ticketList = getProductUnUsedTicket(productId, num);
            if (ticketList == null) {
                // 解锁
                redisLockUtil.unlock(lockKey);
                return Result.error("商品库存不足!");
            }
        }

        String orderNo = OrderNoUtil.generateNo(userId);

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer orderId;
        try {
            Integer status = 0;
            if (amount == 0) {
                if (productInfo.getIsVirtual() == 0) {
                    status = 2;
                } else {
                    status = 1;
                }
            }
            ProductOrder orderData = new ProductOrder();
            orderData.setOrderNo(orderNo);
            orderData.setUserId(userId);
            orderData.setPhone(userInfo.getPhone());
            orderData.setNickName(userInfo.getNickname());
            orderData.setEquipmentId(equipmentId);
            orderData.setSerialNumber(serialNumber);
            orderData.setShopId(shopInfo.getId());
            orderData.setShopName(shopInfo.getName());
            orderData.setProductId(productId);
            orderData.setProductNo(productInfo.getProductNo());
            orderData.setCoverImage(productInfo.getCoverImage());
            orderData.setProductName(productInfo.getName());
            orderData.setSpecification(productInfo.getSpecification());
            orderData.setUnit(productInfo.getUnit());
            orderData.setIsVirtual(productInfo.getIsVirtual());
            orderData.setDeadline(deadline);
            orderData.setRemark(param.getRemark());
            orderData.setPrice(productInfo.getAmount());
            orderData.setNum(num);
            orderData.setAmount(amount);
            orderData.setStatus(status);
            orderData.setPayTime(amount == 0 ? now : null);
            orderData.setExpressStatus(-2);
            orderData.setCreateTime(now);
            productOrderMapper.insert(orderData);
            orderId = orderData.getId();

            //更新商品库存数和销量
            productMapper.updateStockOrSaleNum(productId, -num, num);

            if (productInfo.getIsVirtual() == 0) {
                ProductOrderAddress orderAddress = new ProductOrderAddress();
                orderAddress.setOrderId(orderId);
                if (addressInfo != null) {
                    orderAddress.setAddressId(addressId);
                    orderAddress.setName(addressInfo.getName());
                    orderAddress.setPhone(addressInfo.getPhone());
                    orderAddress.setRegionId(addressInfo.getRegionId());
                    orderAddress.setAddress(addressInfo.getAddress());
                    orderAddress.setLocation(addressInfo.getLocation());
                }
                orderAddress.setCreateTime(now);
                productOrderAddressMapper.insert(orderAddress);
            } else {
                log.info(orderId + " 订单分配券码 : " + ticketList);
                productTicketMapper.updateByProductIdAndTicket(productId, ticketList, 2, orderId);
            }

            ProductOrderLog orderLog = new ProductOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderData.getOrderNo());
            orderLog.setUserId(userId);
            orderLog.setUserName(userInfo.getNickname());
            orderLog.setHandle("创建订单");
            orderLog.setDetails(userInfo.getNickname() + "  " + userInfo.getPhone() + "创建订单");
            orderLog.setCreateTime(now);
            productOrderLogMapper.insert(orderLog);

            if (amount == 0) {
                Order order = new Order();
                order.setUserId(userId);
                order.setPhone(userInfo.getPhone());
                order.setNickName(userInfo.getNickname());
                order.setShopId(shopInfo.getId());
                order.setOrderType(1);
                order.setOrderId(orderId);
                order.setOrderNo(orderData.getOrderNo());
                order.setUpOrderNo("");
                order.setPrice(productInfo.getAmount());
                order.setNum(num);
                order.setAmount(amount);
                order.setStatus(1);
                order.setPayTime(now);
                order.setCreateTime(now);
                orderMapper.insert(order);
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 回滚时重新把券码放回redis列表里
            if (ticketList != null) {
                String key = Constant.PRODUCT_TICKET_LIST + productId;
                redisUtil.rightPushAll(key, ticketList);
            }

            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("addProductOrder Error ", e);

            return Result.error("操作失败");
        }

        if (amount > 0) {
            // 添加到订单超时未支付监控
            Integer timeLimit = 30; // 目前系统没有配置的功能，先暂时写死30分钟
            long timestamp = LocalDateTime.now().plusMinutes(timeLimit)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String key = Constant.ORDER_UNPAY_MONITOR_PREFIX + "productOrder";
            redisUtil.zSetAdd(key, orderId, timestamp);
        }

        if (deadline != null) {
            long timestamp = deadline.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            String key = Constant.ORDER_EXPIRED_MONITOR_PREFIX + "productOrder";
            redisUtil.zSetAdd(key, orderId, timestamp);
        }

        //添加到设备商品扫码下单结果队列里，目前先用redis处理，暂时不维护多一个mq队列
        if (equipmentId != null && equipmentId > 0) {
            redisUtil.rightPush(Constant.PRODUCT_ORDER_RESULT_QUEUE_PREFIX + equipmentId, orderId);
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功", orderId);
    }

    /**
     * 获取商品未使用的券码
     *
     * @param productId
     * @param num
     * @return
     */
    public List<String> getProductUnUsedTicket(Integer productId, Integer num) {
        String key = Constant.PRODUCT_TICKET_LIST + productId;
        List<String> ticketList = redisUtil.batchLeftPop(key, num);
        if (ticketList == null || ticketList.size() == 0) {
            return null;
        }

        //数量不足则不获取
        if (ticketList.size() != num) {
            redisUtil.rightPushAll(key, ticketList);
            return null;
        }

        List<ProductTicket> productTicketList = productTicketMapper.selectByProductIdAndTicket(productId, ticketList,
                1);
        if (productTicketList == null || productTicketList.size() == 0 || productTicketList.size() != num) {
            redisUtil.rightPushAll(key, ticketList);
            return null;
        }

        return ticketList;
    }

    /**
     * 获取商品订单详情
     *
     * @param param
     * @return
     */
    @Override
    public Result userProductOrderInfo(SearchParam param) {
        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        ProductOrderAddressVo orderAddress = null;
        ExpressOrderVo expressOrder = null;
        List<ProductTicket> ticketList = null;
        if (orderInfo.getIsVirtual() == 0) {
            orderAddress = productOrderAddressMapper.selectByOrderId(orderId);
            if (orderAddress != null && orderAddress.getRegionId() > 0) {
                orderAddress.setRegionList(commonService.selectRegionDataById(orderAddress.getRegionId()));
            }

            expressOrder = expressOrderMapper.selectByTxn(1, orderId);
            if (expressOrder != null) {
                if (expressOrder.getInfo() != null && !expressOrder.getInfo().isEmpty()) {
                    List<Map> infoData = JSON.parseArray(expressOrder.getInfo(), Map.class);
                    expressOrder.setInfoData(infoData);
                    expressOrder.setInfo(null);
                }
            }
        } else {
            ticketList = productTicketMapper.selectByOrderId(orderId, null);
        }

        ProductOrderRefundApply refundApplyInfo = productOrderRefundApplyMapper.selectLatestByOrderId(orderId);

        Shop shopInfo = shopMapper.selectById(orderInfo.getShopId());
        String customerPhone = "";
        String customerCodeImg = "";
        if (shopInfo != null) {
            customerPhone = shopInfo.getCustomerPhone();
            customerCodeImg = shopInfo.getCustomerCodeImg();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("refundApplyInfo", refundApplyInfo);
        result.put("orderAddress", orderAddress);
        result.put("expressOrder", expressOrder);
        result.put("ticketList", ticketList);
        result.put("customerPhone", customerPhone);
        result.put("customerCodeImg", customerCodeImg);

        return Result.success("获取成功", result);
    }

    /**
     * 更新商品订单基础信息
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional
    public Result updateProductOrderBase(ProductOrderUpdateBaseParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer addressId = param.getAddressId();
        String remark = param.getRemark();
        if (addressId == null && remark == null) {
            return Result.error("修改信息为空");
        }

        ProductOrder orderInfo = productOrderMapper.selectById(param.getOrderId());
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            return Result.error("您无权操作该订单");
        }
        if (!orderInfo.getStatus().equals(0)) {
            return Result.error("只有待支付的订单才可修改");
        }

        DeliveryAddress addressInfo = null;
        if (orderInfo.getIsVirtual() == 0) {
            if (addressId != null) {
                addressInfo = deliveryAddressMapper.selectById(addressId);
                if (addressInfo == null) {
                    return Result.error("收货地址不存在");
                }
                if (!addressInfo.getUserId().equals(userId)) {
                    return Result.error("您无权选择该收货地址");
                }
            }
        }

        if (remark != null) {
            ProductOrder orderUpdate = new ProductOrder();
            orderUpdate.setId(orderInfo.getId());
            orderUpdate.setRemark(remark);
            productOrderMapper.update(orderUpdate);
        }

        if (addressInfo != null) {
            ProductOrderAddressVo orderAddress = productOrderAddressMapper.selectByOrderId(orderInfo.getId());
            ProductOrderAddress orderAddressUpdate = new ProductOrderAddress();
            orderAddressUpdate.setId(orderAddress.getId());
            orderAddressUpdate.setAddressId(addressId);
            orderAddressUpdate.setName(addressInfo.getName());
            orderAddressUpdate.setPhone(addressInfo.getPhone());
            orderAddressUpdate.setRegionId(addressInfo.getRegionId());
            orderAddressUpdate.setAddress(addressInfo.getAddress());
            orderAddressUpdate.setLocation(addressInfo.getLocation());
            productOrderAddressMapper.update(orderAddressUpdate);
        }

        return Result.success("操作成功");
    }

    /**
     * 获取商品订单支付配置信息
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result productOrderPayConfig(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        // 上锁开始操作
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "productOrderPayConfig:" + orderId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("记录不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您无权操作该订单");
        }
        if (!orderInfo.getStatus().equals(0)) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("该订单不可支付");
        }

        if (orderInfo.getIsVirtual() == 0) {
            ProductOrderAddressVo orderAddress = productOrderAddressMapper.selectByOrderId(orderId);
            if (orderAddress.getAddressId() <= 0) {
                // 解锁
                redisLockUtil.unlock(lockKey);
                return Result.error("请先填写收货地址");
            }
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }

        Result result = tgyPayUtil.getWxJsPayConfig(userInfo.getOpenId(), orderInfo.getAmount(), orderInfo.getOrderNo(),
                "商品订单支付", merchantConfig.getProductOrderCallBackUrl(), true);
        if (!result.success()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return result;
        }

        Map<String, Object> resultData = (Map<String, Object>) result.getData();
        String upOrderId = resultData.get("upOrderId").toString();
        Map<String, Object> payInfo = (Map<String, Object>) resultData.get("payInfo");

        // 将通莞订单号保存到数据库中
        if (orderInfo.getUpOrderNo() == null || !orderInfo.getUpOrderNo().equals(upOrderId)) {
            ProductOrder updateOrder = new ProductOrder();
            updateOrder.setId(orderInfo.getId());
            updateOrder.setUpOrderNo(upOrderId);
            productOrderMapper.update(updateOrder);
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("获取成功", payInfo);
    }

    /**
     * 商品订单支付回调
     *
     * @param response
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public String productOrderPayCallBack(WxJsPayCallBackResponse response) {
        if (response == null) {
            return Constant.TGY_PAY_CALLBACK_FAIL;
        }

        log.info("productOrderPayCallBack Param : {}", JSON.toJSONString(response));

        //验签
        Boolean verifySign = SignUtil.verifySign(merchantConfig.getKey(), response.getSign(), response);
        if (!verifySign) {
            log.error("productOrderPayCallBack 验签失败 lowOrderId : {} upOrderId : {}" + response.getLowOrderId(),
                    response.getUpOrderId());
            return Constant.TGY_PAY_CALLBACK_FAIL;
        }

        String orderNo = response.getLowOrderId();

        // 查出对应订单
        ProductOrder orderInfo = productOrderMapper.selectByOrderNo(orderNo, null);
        if (orderInfo == null) {
            log.error("productOrderPayCallBack 订单不存在 lowOrderId : {} upOrderId : {}" + response.getLowOrderId(),
                    response.getUpOrderId());
            return Constant.TGY_PAY_CALLBACK_FAIL;
        }
        if (!orderInfo.getStatus().equals(0) && !orderInfo.getStatus().equals(8)) {
            log.error("productOrderPayCallBack 订单状态错误 lowOrderId : {} upOrderId : {} status : {}" + response.getLowOrderId(),
                    response.getUpOrderId(), orderInfo.getStatus());
            return Constant.TGY_PAY_CALLBACK_FAIL;
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer payStatus = 1;
        try {
            if (!response.getState().equals("0")) {
                payStatus = 2;
            }

            LocalDateTime now = LocalDateTime.now();

            PayLog payLog = new PayLog();
            payLog.setOrderType(1);
            payLog.setOrderId(orderInfo.getId());
            payLog.setUserId(orderInfo.getUserId());
            payLog.setOutTradeNo(orderNo);
            payLog.setWxTransactionNo(response.getUpOrderId());
            payLog.setWxResult(JSON.toJSONString(response));
            payLog.setTotalFee(orderInfo.getAmount());
            payLog.setStatus(payStatus);
            payLog.setCreateTime(now);
            payLogMapper.insert(payLog);

            if (payStatus == 1) {  //支付成功
                // 修改订单状态
                Integer status;
                if (orderInfo.getIsVirtual() == 0) {
                    status = 2;
                } else {
                    status = 1;
                }
                ProductOrder orderUpdate = new ProductOrder();
                orderUpdate.setId(orderInfo.getId());
                orderUpdate.setUpOrderNo(response.getUpOrderId());
                orderUpdate.setStatus(status);
                orderUpdate.setPayTime(now);
                productOrderMapper.update(orderUpdate);

                Order order = new Order();
                order.setUserId(orderInfo.getUserId());
                order.setPhone(orderInfo.getPhone());
                order.setNickName(orderInfo.getNickName());
                order.setShopId(orderInfo.getShopId());
                order.setOrderType(1);
                order.setOrderId(orderInfo.getId());
                order.setOrderNo(orderInfo.getOrderNo());
                order.setUpOrderNo(response.getUpOrderId());
                order.setPrice(orderInfo.getPrice());
                order.setNum(orderInfo.getNum());
                order.setAmount(orderInfo.getAmount());
                order.setStatus(1);
                order.setPayTime(now);
                order.setCreateTime(now);
                orderMapper.insert(order);

                // 保存操作日志
                ProductOrderLog orderLog = new ProductOrderLog();
                orderLog.setOrderId(orderInfo.getId());
                orderLog.setOrderNo(orderInfo.getOrderNo());
                orderLog.setHandle("完成支付");
                String details = orderInfo.getNickName() + "  " + orderInfo.getPhone() + "完成支付";
                orderLog.setDetails(details);
                orderLog.setUserId(orderInfo.getUserId());
                orderLog.setUserName(orderInfo.getNickName());
                orderLog.setCreateTime(now);
                orderLog.setUpdateTime(now);
                productOrderLogMapper.insert(orderLog);
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            log.error("商品订单支付回调处理失败，订单号：{}", response.getLowOrderId(), e);

            return Constant.TGY_PAY_CALLBACK_FAIL;
        }

        //添加到设备商品扫码下单结果队列里，目前先用redis处理，暂时不维护多一个mq队列
        if (payStatus == 1 && orderInfo.getEquipmentId() != null && orderInfo.getEquipmentId() > 0) {
            redisUtil.rightPush(Constant.PRODUCT_ORDER_RESULT_QUEUE_PREFIX + orderInfo.getEquipmentId(),
                    orderInfo.getId());
        }

        return Constant.TGY_PAY_CALLBACK_SUCCESS;
    }

    /**
     * 获取用户商品订单列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userProductOrderLists(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        param.setSearchField1(userId);

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ProductOrder> list = productOrderMapper.selectLists(param);
        PageInfo<ProductOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取用户商品订单统计信息
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userProductOrderStat(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        param.setSearchField1(userId);

        ProductOrderCountVo statData = productOrderMapper.selectCount(param);

        return Result.success("获取成功", statData);
    }

    /**
     * 商品订单退款申请
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result productOrderRefund(ProductOrderRefundRefundParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 上锁
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "productOrderRefund:" + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        Integer orderId = param.getOrderId();
        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您无权操作该订单");
        }
        List<Integer> canReturnOrderStatus = Arrays.asList(1, 2, 3, 4, 7);
        if (!canReturnOrderStatus.contains(orderInfo.getStatus())) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("该订单不可退款");
        }
        if (orderInfo.getDivideStatus().equals(1)) {
            return Result.error("该订单不可退款!");
        }

        List<String> images = param.getImages();
        String refundImg = "";
        if (images != null && !images.isEmpty()) {
            refundImg = String.join(",", images);
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            ProductOrderRefundApply refundApply = new ProductOrderRefundApply();
            refundApply.setOrderId(orderId);
            refundApply.setOldStatus(orderInfo.getStatus());
            refundApply.setReason(param.getRefundReason());
            refundApply.setImages(refundImg);
            refundApply.setStatus(1);
            refundApply.setCreateTime(now);
            productOrderRefundApplyMapper.insert(refundApply);

            ProductOrder orderUpdate = new ProductOrder();
            orderUpdate.setId(orderId);
            orderUpdate.setStatus(5);
            productOrderMapper.update(orderUpdate);

            ProductOrderLog orderLog = new ProductOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(userId);
            orderLog.setUserName(orderInfo.getNickName());
            orderLog.setHandle("申请退货");
            orderLog.setDetails(orderInfo.getNickName() + "  " + orderInfo.getPhone() + "申请退货");
            orderLog.setCreateTime(now);
            productOrderLogMapper.insert(orderLog);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("productOrderRefund Error ", e);

            return Result.error("操作失败");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }
}
