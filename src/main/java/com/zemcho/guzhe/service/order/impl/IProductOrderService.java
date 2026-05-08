package com.zemcho.guzhe.service.order.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AuthJwtData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.controller.order.param.OrderAuditParam;
import com.zemcho.guzhe.controller.order.param.OrderRefundParam;
import com.zemcho.guzhe.controller.order.vo.ExpressOrderVo;
import com.zemcho.guzhe.controller.order.vo.ProductOrderRefundApplyListVo;
import com.zemcho.guzhe.controller.order.vo.ProductOrderUnDispatchedExportVo;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderAddressVo;
import com.zemcho.guzhe.controller.wechat.user.vo.ProductOrderCountVo;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.express.ExpressCompany;
import com.zemcho.guzhe.entity.log.PayLog;
import com.zemcho.guzhe.entity.order.*;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.entity.shop.ShopManager;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.express.ExpressCompanyMapper;
import com.zemcho.guzhe.mapper.express.ExpressOrderMapper;
import com.zemcho.guzhe.mapper.log.PayLogMapper;
import com.zemcho.guzhe.mapper.order.*;
import com.zemcho.guzhe.mapper.product.ProductCheckAdminMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.product.ProductTicketMapper;
import com.zemcho.guzhe.service.common.CommonService;
import com.zemcho.guzhe.service.order.ProductOrderService;
import com.zemcho.guzhe.service.order.listener.ImportProductOrderExpressListener;
import com.zemcho.guzhe.service.shop.ShopManagerService;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.excel.ExcelUtil;
import com.zemcho.guzhe.util.redis.RedisLockUtil;
import com.zemcho.guzhe.util.tgy.TgyPayUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @title: IProductOrderService
 * @Description:
 * @Date: 2026/4/28 8:58
 */
@Service
@Slf4j
public class IProductOrderService implements ProductOrderService {
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
    private ExpressOrderMapper expressOrderMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductCheckAdminMapper productCheckAdminMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ShopManagerService shopManagerService;

    /**
     * 获取商品订单列表
     *
     * @param param
     * @return
     */
    @Override
    public Result productOrderLists(SearchParam param, String token, Boolean isWechat) {
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getSearchField4();
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ProductOrder> list = productOrderMapper.selectLists(param);
        PageInfo<ProductOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商品订单统计信息
     *
     * @param param
     * @return
     */
    @Override
    public Result productOrderStat(SearchParam param, String token, Boolean isWechat) {
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getSearchField4();
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }
        }

        ProductOrderCountVo statData = productOrderMapper.selectCount(param);

        return Result.success("获取成功", statData);
    }

    /**
     * 导出商品订单数据
     *
     * @param param
     * @param response
     */
    @Override
    public void productOrderExport(SearchParam param, HttpServletResponse response, String token, Boolean isWechat) {
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getSearchField4();
            if (shopId == null) {
                return;
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return;
            }
        }

        List<ProductOrder> list = productOrderMapper.selectLists(param);
        ExcelUtil.exportToWeb(response, list, "商品订单信息", "商品订单信息", ProductOrder.class);
    }

    /**
     * 获取商品订单详情
     *
     * @param param
     * @return
     */
    @Override
    public Result productOrderInfo(SearchParam param, String token, Boolean isWechat) {
        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        if (isWechat) { //小程序端进入的
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, orderInfo.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }
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

        List<ProductOrderLog> logList = productOrderLogMapper.selectByOrderId(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("refundApplyInfo", refundApplyInfo);
        result.put("orderAddress", orderAddress);
        result.put("expressOrder", expressOrder);
        result.put("ticketList", ticketList);
        result.put("logList", logList);

        return Result.success("获取成功", result);
    }

    /**
     * 商品订单--未发货数据导出
     *
     * @param param
     * @param response
     */
    @Override
    public void productOrderUnDispatchedExport(SearchParam param, HttpServletResponse response, String token,
                                               Boolean isWechat) {
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getSearchField4();
            if (shopId == null) {
                return;
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return;
            }
        }

        param.setSearchIntStatus(2);
        param.setSearchField3(-2);
        List<ProductOrder> list = productOrderMapper.selectLists(param);
        List<ProductOrderUnDispatchedExportVo> data = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (ProductOrder orderInfo : list) {
                if (orderInfo.getExpressNo() != null && !orderInfo.getExpressNo().isEmpty()) {
                    continue;
                }

                ProductOrderUnDispatchedExportVo vo = new ProductOrderUnDispatchedExportVo();
                vo.setOrderNo(orderInfo.getOrderNo());
                vo.setProductNo(orderInfo.getProductNo());
                vo.setProductName(orderInfo.getProductName());
                vo.setAmount(orderInfo.getAmount());
                data.add(vo);
            }
        }
        ExcelUtil.exportToWeb(response, data, "待发货订单信息", "待发货订单信息",
                ProductOrderUnDispatchedExportVo.class);
    }

    /**
     * 商品订单--导入物流单号
     *
     * @param file
     * @return
     */
    @Override
    public Result importExpressNo(MultipartFile file) {
        // 上锁
        String lockKey = Constant.IMPORT_LOCK_PREFIX + "product_order_express";
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("正在导入快递信息，请稍后再试");
        }

        SearchParam param = new SearchParam();
        param.setSearchIntStatus(2);
        param.setSearchField3(-2);
        List<ProductOrder> orderList = productOrderMapper.selectLists(param);
        if (orderList == null || orderList.isEmpty()) {
            return Result.error("暂无未发货的订单数据，无需导入");
        }
        Map<String, ProductOrder> orderMap =
                orderList.stream().collect(Collectors.toMap(ProductOrder::getOrderNo, exchangeOrder -> exchangeOrder));

        SearchParam expressCompanyParam = new SearchParam();
        List<ExpressCompany> expressCompanyList = expressCompanyMapper.selectLists(expressCompanyParam);
        Map<String, ExpressCompany> expressCompanyMap =
                expressCompanyList.stream().collect(Collectors.toMap(ExpressCompany::getName,
                        expressCompany -> expressCompany));

        ImportProductOrderExpressListener listener = new ImportProductOrderExpressListener(productOrderMapper,
                expressOrderMapper, orderMap, expressCompanyMap);

        try {
            EasyExcel.read(file.getInputStream(), ProductOrderUnDispatchedExportVo.class, listener)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(1) // 跳过前几行
                    .doRead();
        } catch (Exception e) {
            // 解锁
            redisLockUtil.unlock(lockKey);

            return Result.error("导入失败，文件格式有误");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        if (listener.getErrorList().size() > 0) {
            return Result.error("部分数据导入失败，数据有误", listener.getErrorList());
        }
        return Result.success("导入成功");
    }

    /**
     * 商品订单--退款
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result orderRefund(OrderRefundParam param, String token, Boolean isWechat) {
        Integer orderId = param.getOrderId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        List<Integer> canReturnOrderStatus = Arrays.asList(1, 2, 3, 4, 7);
        if (!canReturnOrderStatus.contains(orderInfo.getStatus())) {
            return Result.error("该订单状态不可操作退款");
        }
        if (orderInfo.getDivideStatus().equals(1)) {
            return Result.error("该订单已分账，不可退款");
        }

        Integer refundAmount = param.getRefundAmount();
        if (refundAmount == null || refundAmount <= 0) {
            return Result.error("退款金额必须大于0");
        }
        if (refundAmount > orderInfo.getAmount()) {
            return Result.error("退款金额不能大于订单金额");
        }

        Integer adminId;
        String name;
        String phone;
        String handle;
        if (isWechat) { //小程序端进入的
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, orderInfo.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }

            ShopManager shopManager = (ShopManager) checkResult.getData();
            adminId = 0;
            name = shopManager.getName();
            phone = shopManager.getPhone();
            handle = "小程序商家退款";
        } else {
            AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
            if (authJwtData == null) {
                return new Result(10006, "token无效");
            }

            adminId = authJwtData.getAdminId();
            name = authJwtData.getName();
            phone = authJwtData.getAccount();
            handle = "后台退款";
        }

        // 上锁
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "productOrderRefund:" + orderInfo.getUserId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("订单用户正在操作，请稍后再试");
        }

        PayLog payLog = payLogMapper.selectByOrderId(1, orderId, 1);
        if (payLog == null) {
            return Result.error("订单支付信息异常，请联系管理员");
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            ProductOrder updateProductOrder = new ProductOrder();
            updateProductOrder.setId(orderId);
            updateProductOrder.setStatus(6);
            updateProductOrder.setRefundAmount(refundAmount);
            updateProductOrder.setRefundTime(now);
            updateProductOrder.setRefundRemark(param.getRefundReason());
            productOrderMapper.update(updateProductOrder);

            Order updateOrder = new Order();
            updateOrder.setOrderId(orderId);
            updateOrder.setStatus(2);
            updateOrder.setRefundAmount(refundAmount);
            updateOrder.setRefundTime(now);
            orderMapper.updateByOrderId(updateOrder);

            //更新商品库存数和兑换数
            productMapper.updateStockOrSaleNum(orderInfo.getProductId(), orderInfo.getNum(), -orderInfo.getNum());

            ProductOrderLog orderLog = new ProductOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(adminId);
            orderLog.setUserName(name);
            orderLog.setHandle(handle);
            orderLog.setDetails(phone + "  " + name + "操作退款，退款原因：" + param.getRefundReason());
            orderLog.setCreateTime(now);
            productOrderLogMapper.insert(orderLog);

            PayLog payLogUpdate = new PayLog();
            payLogUpdate.setId(payLog.getId());
            payLogUpdate.setStatus(3);
            payLogUpdate.setRefundPrice(refundAmount);
            payLogUpdate.setRefundTime(now);
            payLogMapper.update(payLogUpdate);

            //退款处理
            Result result = tgyPayUtil.refund(orderInfo.getUpOrderNo(), refundAmount);
            if (!result.success()) {
                throw new Exception("通莞退款失败");
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("admin productOrderRefund Error ", e);

            return Result.error("操作失败");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    /**
     * 获取商品订单退款申请列表
     *
     * @param param
     * @return
     */
    @Override
    public Result orderRefundApplyLists(SearchParam param, String token, Boolean isWechat) {
        if (isWechat) { //小程序端进入的
            Integer shopId = param.getSearchField4();
            if (shopId == null) {
                return Result.error("请选择商家");
            }

            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, shopId);
            if (!checkResult.success()) {
                return checkResult;
            }
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ProductOrderRefundApplyListVo> list = productOrderRefundApplyMapper.selectLists(param);
        PageInfo<ProductOrderRefundApplyListVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商品订单退款申请详情
     *
     * @param param
     * @return
     */
    @Override
    public Result orderRefundApplyInfo(SearchParam param) {
        Integer applyId = param.getSearchId();
        if (applyId == null) {
            return Result.error("参数异常");
        }

        ProductOrderRefundApply applyInfo = productOrderRefundApplyMapper.selectById(applyId);
        if (applyInfo == null) {
            return Result.error("申请不存在");
        }

        SearchParam orderParam = new SearchParam();
        orderParam.setSearchId(applyInfo.getOrderId());
        Result orderResult = productOrderInfo(orderParam, null, false);
        if (!orderResult.success()) {
            return orderResult;
        }

        Map<String, Object> result = (Map<String, Object>) orderResult.getData();
        result.put("applyInfo", applyInfo);

        return Result.success("获取成功", result);
    }

    /**
     * 商品订单--退款审核
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result orderRefundAudit(OrderAuditParam param, String token, Boolean isWechat) {
        Integer applyId = param.getApplyId();
        ProductOrderRefundApply applyInfo = productOrderRefundApplyMapper.selectById(applyId);
        if (applyInfo == null) {
            return Result.error("申请不存在");
        }
        if (applyInfo.getStatus() != 1) {
            return Result.error("申请已审核，不可重复审核");
        }

        Integer orderId = applyInfo.getOrderId();
        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (orderInfo.getStatus() != 5) {
            return Result.error("该订单状态不可操作审核");
        }

        Integer status = param.getStatus();
        Integer refundAmount = param.getRefundAmount();
        String remark = param.getRemark();
        String statusStr = "";
        if (status == 2) { // 通过
            if (refundAmount == null || refundAmount <= 0) {
                return Result.error("退款金额必须大于0");
            }
            if (refundAmount > orderInfo.getAmount()) {
                return Result.error("退款金额不能大于订单金额");
            }

            if (orderInfo.getDivideStatus().equals(1)) {
                return Result.error("该订单已分账，不可退款");
            }

            statusStr = "审核通过，退货申请原因：" + applyInfo.getReason();
        } else { // 驳回
            if (remark == null || "".equals(remark)) {
                return Result.error("请填写审核意见");
            }

            statusStr = "审核驳回，驳回原因：" + remark;
        }

        Integer adminId;
        String name;
        String phone;
        String handle;
        if (isWechat) { //小程序端进入的
            Result checkResult = shopManagerService.checkWechatUserIsShopManager(token, orderInfo.getShopId());
            if (!checkResult.success()) {
                return checkResult;
            }

            ShopManager shopManager = (ShopManager) checkResult.getData();
            adminId = 0;
            name = shopManager.getName();
            phone = shopManager.getPhone();
            handle = "小程序退货审核";
        } else {
            AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
            if (authJwtData == null) {
                return new Result(10006, "token无效");
            }

            adminId = authJwtData.getAdminId();
            name = authJwtData.getName();
            phone = authJwtData.getAccount();
            handle = "后台退货审核";
        }

        // 上锁
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "productOrderRefund:" + orderInfo.getUserId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("订单用户正在操作，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            ProductOrderRefundApply updateApply = new ProductOrderRefundApply();
            updateApply.setId(applyId);
            updateApply.setStatus(status);
            updateApply.setAdminId(adminId);
            updateApply.setAccount(phone);
            updateApply.setName(name);
            updateApply.setRefundAmount(refundAmount);
            updateApply.setAuditTime(now);
            updateApply.setAuditRemark(remark);
            productOrderRefundApplyMapper.update(updateApply);

            ProductOrderLog orderLog = new ProductOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(adminId);
            orderLog.setUserName(name);
            orderLog.setHandle(handle);
            orderLog.setDetails(phone + "  " + name + statusStr);
            orderLog.setCreateTime(now);
            productOrderLogMapper.insert(orderLog);

            ProductOrder updateProductOrder = new ProductOrder();
            updateProductOrder.setId(orderId);
            updateProductOrder.setStatus(applyInfo.getOldStatus());
            if (status == 2) {
                updateProductOrder.setStatus(6);
                updateProductOrder.setRefundAmount(refundAmount);
                updateProductOrder.setRefundTime(now);
                updateProductOrder.setRefundRemark(param.getRemark());
            }
            productOrderMapper.update(updateProductOrder);

            if (status == 2) { // 通过
                Order updateOrder = new Order();
                updateOrder.setOrderId(orderId);
                updateOrder.setStatus(2);
                updateOrder.setRefundAmount(refundAmount);
                updateOrder.setRefundTime(now);
                orderMapper.updateByOrderId(updateOrder);

                //更新商品库存数和兑换数
                productMapper.updateStockOrSaleNum(orderInfo.getProductId(), orderInfo.getNum(), -orderInfo.getNum());

                //退款处理
                Result result = tgyPayUtil.refund(orderInfo.getUpOrderNo(), refundAmount);
                if (!result.success()) {
                    throw new Exception("通莞退款失败");
                }
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("productOrderRefundAudit Error ", e);

            return Result.error("操作失败");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    /**
     * 商品订单--券码核销
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result productOrderTicketCheck(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        List<Integer> productIds = productCheckAdminMapper.selectProductIdByAdminId(userInfo.getId());
        if (productIds == null || productIds.isEmpty()) {
            return Result.error("您无权限核销");
        }

        String ticket = param.getSearchStrField1();
        if (ticket == null || "".equals(ticket)) {
            return Result.error("参数异常");
        }

        ProductTicket ticketInfo = productTicketMapper.selectByTicket(ticket);
        if (ticketInfo == null) {
            return Result.error("兑换券码不存在");
        }
        if (!ticketInfo.getStatus().equals(2)) {
            String msg = ticketInfo.getStatus().equals(1) ? "该兑换券码不可核销" : "该兑换券码已核销";
            return Result.error(msg);
        }
        if (!productIds.contains(ticketInfo.getProductId())) {
            return Result.error("您无权限核销!");
        }
        Integer ticketId = ticketInfo.getId();
        Integer orderId = ticketInfo.getOrderId();

        // 上锁
        String lockKey = Constant.OPERATION_LOCK_PREFIX + "productOrderTicketCheck:" + orderId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("该订单正在核销，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("订单信息异常");
        }
        if (orderInfo.getStatus() != 1) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("该订单不可核销");
        }
        if (orderInfo.getDeadline() == null || now.isAfter(orderInfo.getDeadline())) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("该券码已失效");
        }

        List<ProductTicket> ticketList = productTicketMapper.selectByOrderId(orderId, 2);

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            productTicketMapper.updateStatusById(ticketId, 3);

            ProductOrderLog orderLog = new ProductOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(userId);
            orderLog.setUserName(orderInfo.getNickName());
            orderLog.setHandle("券码核销");
            orderLog.setDetails(orderInfo.getNickName() + "  " + orderInfo.getPhone() + "核销券码(" + ticketInfo.getTicket() +
                    ")");
            orderLog.setCreateTime(now);
            productOrderLogMapper.insert(orderLog);

            if (ticketList.size() <= 1) {
                ProductOrder updateProductOrder = new ProductOrder();
                updateProductOrder.setId(orderId);
                updateProductOrder.setStatus(4);
                productOrderMapper.update(updateProductOrder);
            }

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            log.error("productOrderTicketCheck Error ", e);

            return Result.error("操作失败");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }
}
