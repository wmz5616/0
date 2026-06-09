package com.zemcho.ddql.service.order.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthJwtData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.wechat.WechatConfig;
import com.zemcho.ddql.controller.order.param.OrderAuditParam;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderCountVo;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderRefundApplyListVo;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderUnDispatchedExportVo;
import com.zemcho.ddql.controller.order.vo.ExpressOrderVo;
import com.zemcho.ddql.controller.wechat.index.vo.ExchangeOrderAddressVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.cas.CasUserCoinLog;
import com.zemcho.ddql.entity.express.ExpressCompany;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.order.ExchangeOrderLog;
import com.zemcho.ddql.entity.order.ExchangeOrderRefundApply;
import com.zemcho.ddql.entity.product.ProductTicket;
import com.zemcho.ddql.mapper.cas.CasUserCoinLogMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.express.ExpressCompanyMapper;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderAddressMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderLogMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderRefundApplyMapper;
import com.zemcho.ddql.mapper.product.ProductMapper;
import com.zemcho.ddql.mapper.product.ProductTicketMapper;
import com.zemcho.ddql.service.order.ExchangeOrderService;
import com.zemcho.ddql.service.order.listener.ImportExchangeOrderExpressListener;
import com.zemcho.ddql.service.personalCenter.RegionService;
import com.zemcho.ddql.service.wechat.notice.async.SendSubscribeMsgAsync;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.excel.ExcelUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.tgy.TgyPayUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @title: IExchangeOrderService
 * @Description:
 * @Date: 2025/10/14 13:54
 */
@Service
public class IExchangeOrderService implements ExchangeOrderService {
    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private ExchangeOrderAddressMapper exchangeOrderAddressMapper;

    @Autowired
    private ExchangeOrderLogMapper exchangeOrderLogMapper;

    @Autowired
    private ExchangeOrderRefundApplyMapper exchangeOrderRefundApplyMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    CasUserMapper casUserMapper;

    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;

    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    @Autowired
    private ExpressOrderMapper expressOrderMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private RegionService regionService;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private SendSubscribeMsgAsync sendSubscribeMsgAsync;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    /**
     * 兑换订单列表
     *
     * @param param
     * @return
     */
    @Override
    public Result orderLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ExchangeOrder> list = exchangeOrderMapper.selectLists(param);
        PageInfo<ExchangeOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 兑换订单统计信息
     *
     * @param param
     * @return
     */
    @Override
    public Result orderStat(SearchParam param) {
        //统计总的数据
        ExchangeOrderCountVo totalStatData = exchangeOrderMapper.selectCount(param);

        //统计今日内的数据
        param.setStartTime(LocalDateUtil.strToLDT(LocalDateUtil.getStartTime()));
        param.setEndTime(LocalDateUtil.strToLDT(LocalDateUtil.getEndTime()));
        ExchangeOrderCountVo todayStatData = exchangeOrderMapper.selectCount(param);

        Map<String, Object> result = new HashMap<>();
        result.put("totalStatData", totalStatData);
        result.put("todayStatData", todayStatData);

        return Result.success("获取成功", result);
    }

    /**
     * 兑换订单数据导出
     *
     * @param param
     * @param response
     */
    @Override
    public void orderExport(SearchParam param, HttpServletResponse response) {
        List<ExchangeOrder> list = exchangeOrderMapper.selectLists(param);
        ExcelUtil.exportToWeb(response, list, "兑换订单信息", "兑换订单信息", ExchangeOrder.class);
    }

    /**
     * 兑换订单详情
     *
     * @param param
     * @return
     */
    @Override
    public Result orderInfo(SearchParam param) {
        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        ExchangeOrderAddressVo orderAddress = null;
        ExpressOrderVo expressOrder = null;
        List<ProductTicket> ticketList = null;
        if (orderInfo.getIsVirtual() == 0) {
            orderAddress = exchangeOrderAddressMapper.selectByOrderId(orderId);
            if (orderAddress != null) {
                orderAddress.setRegionList(regionService.selectRegionDataById(orderAddress.getRegionId()));
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
            ticketList = productTicketMapper.selectByOrderId(orderId);
        }

        ExchangeOrderRefundApply refundApplyInfo = exchangeOrderRefundApplyMapper.selectLatestByOrderId(orderId);

        List<ExchangeOrderLog> logList = exchangeOrderLogMapper.selectByOrderId(orderId);

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
     * 兑换订单-未发货数据导出
     *
     * @param param
     * @param response
     */
    @Override
    public void orderUnDispatchedExport(SearchParam param, HttpServletResponse response) {
        param.setSearchIntStatus(2);
        param.setSearchField3(-2);
        List<ExchangeOrder> list = exchangeOrderMapper.selectLists(param);
        List<ExchangeOrderUnDispatchedExportVo> data = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (ExchangeOrder orderInfo : list) {
                if (orderInfo.getExpressNo() != null && !orderInfo.getExpressNo().isEmpty()) {
                    continue;
                }

                ExchangeOrderUnDispatchedExportVo vo = new ExchangeOrderUnDispatchedExportVo();
                vo.setOrderNo(orderInfo.getOrderNo());
                vo.setProductNo(orderInfo.getProductNo());
                vo.setProductName(orderInfo.getProductName());
                vo.setAmount(orderInfo.getAmount());
                data.add(vo);
            }
        }
        ExcelUtil.exportToWeb(response, data, "待发货订单信息", "待发货订单信息",
                ExchangeOrderUnDispatchedExportVo.class);
    }

    /**
     * 兑换订单-导入物流单号
     *
     * @param file
     * @return
     */
    @Override
    public Result importExpressNo(MultipartFile file) {
        // 上锁
        String lockKey = Constant.IMPORT_LOCK_PREFIX + "exchange_order_express";
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("正在导入快递信息，请稍后再试");
        }

        SearchParam param = new SearchParam();
        param.setSearchIntStatus(2);
        param.setSearchField3(-2);
        List<ExchangeOrder> exchangeOrderList = exchangeOrderMapper.selectLists(param);
        if (exchangeOrderList == null || exchangeOrderList.isEmpty()) {
            return Result.error("暂无未发货的订单数据，无需导入");
        }
        Map<String, ExchangeOrder> orderMap =
                exchangeOrderList.stream().collect(Collectors.toMap(ExchangeOrder::getOrderNo,
                        exchangeOrder -> exchangeOrder));

        SearchParam expressCompanyParam = new SearchParam();
        List<ExpressCompany> expressCompanyList = expressCompanyMapper.selectLists(expressCompanyParam);
        Map<String, ExpressCompany> expressCompanyMap =
                expressCompanyList.stream().collect(Collectors.toMap(ExpressCompany::getName,
                        expressCompany -> expressCompany));

        ImportExchangeOrderExpressListener listener = new ImportExchangeOrderExpressListener(exchangeOrderMapper,
                expressOrderMapper, orderMap, expressCompanyMap);

        try {
            EasyExcel.read(file.getInputStream(), ExchangeOrderUnDispatchedExportVo.class, listener)
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
     * 兑换订单-退货
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result orderRefund(OrderRefundParam param, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getOrderId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (orderInfo.getStatus() != 2 && orderInfo.getStatus() != 3 && orderInfo.getStatus() != 4) {
            return Result.error("该订单状态不可操作退货");
        }
        if (!orderInfo.getIsVirtual().equals(0)) {
            return Result.error("该订单不支持退货");
        }

        Integer refundAmount = param.getRefundAmount();
        if (refundAmount == null || refundAmount <= 0) {
            return Result.error("退货币额必须大于0");
        }
        if (refundAmount > orderInfo.getAmount()) {
            return Result.error("退货币额不能大于订单金额");
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + orderInfo.getUserId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("订单用户正在操作，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            ExchangeOrder updateOrder = new ExchangeOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(6);
            updateOrder.setRefundAmount(refundAmount);
            updateOrder.setRefundCashAmount(orderInfo.getCashAmount());
            updateOrder.setRefundTime(now);
            updateOrder.setRefundRemark(param.getRefundReason());
            exchangeOrderMapper.update(updateOrder);

            //更新商品库存数和兑换数
            productMapper.updateStockOrExchangeNum(orderInfo.getProductId(), orderInfo.getNum(), -orderInfo.getNum());

            //更新用户金币数
            casUserMapper.incCoin(orderInfo.getUserId(), refundAmount, null);
            List<CasUserCoinLog> logList = new ArrayList<>();
            CasUserCoinLog goldCoinLog = new CasUserCoinLog();
            goldCoinLog.setTxnType(5);
            goldCoinLog.setTxnId(orderId);
            goldCoinLog.setCoinType(2);
            goldCoinLog.setNumType(1);
            goldCoinLog.setCoinNum(refundAmount);
            goldCoinLog.setUserId(orderInfo.getUserId());
            goldCoinLog.setPhone(orderInfo.getPhone());
            goldCoinLog.setNickName(orderInfo.getNickName());
            goldCoinLog.setTeamId(0);
            goldCoinLog.setTeamName("");
            goldCoinLog.setTeamType(0);
            goldCoinLog.setRemark("管理员操作退货-" + orderInfo.getProductName() + " x" + orderInfo.getNum());
            goldCoinLog.setCreateTime(now);
            logList.add(goldCoinLog);
            casUserCoinLogMapper.insertAll(logList);

            if (orderInfo.getCashAmount() != null && orderInfo.getCashAmount() > 0
                    && orderInfo.getUpOrderId() != null && !orderInfo.getUpOrderId().isEmpty()) {
                Result refundResult = tgyPayUtil.refund(orderInfo.getUpOrderId(), orderInfo.getCashAmount());
                if (!refundResult.success()) {
                    throw new RuntimeException(refundResult.getMsg());
                }
            }

            ExchangeOrderLog orderLog = new ExchangeOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(authJwtData.getAdminId());
            orderLog.setUserName(authJwtData.getName());
            orderLog.setHandle("后台退货");
            orderLog.setDetails(authJwtData.getAccount() + "  " + authJwtData.getName() + "操作退货，退货原因：" + param.getRefundReason());
            orderLog.setCreateTime(now);
            exchangeOrderLogMapper.insert(orderLog);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //发送订阅消息
        CasUser userInfo = casUserMapper.selectById(orderInfo.getUserId());
        Map<String, Object> msgData = new HashMap<>();
        msgData.put("thing1", orderInfo.getProductName());
        msgData.put("amount2", refundAmount);
        msgData.put("short_thing3", "管理员退款");
        msgData.put("character_string4", orderInfo.getOrderNo());
        msgData.put("thing5", "退款成功2小时内到账");
        sendSubscribeMsgAsync.asyncSendSubscribeMsg(userInfo.getOpenId(),
                wechatConfig.getProductRefundTemplateId(), null, msgData);

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    /**
     * 兑换订单-退货申请列表
     *
     * @param param
     * @return
     */
    @Override
    public Result orderRefundApplyLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ExchangeOrderRefundApplyListVo> list = exchangeOrderRefundApplyMapper.selectLists(param);
        PageInfo<ExchangeOrderRefundApplyListVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 兑换订单-退货申请详情
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

        ExchangeOrderRefundApply applyInfo = exchangeOrderRefundApplyMapper.selectById(applyId);
        if (applyInfo == null) {
            return Result.error("申请不存在");
        }

        SearchParam orderParam = new SearchParam();
        orderParam.setSearchId(applyInfo.getOrderId());
        Result orderResult = orderInfo(orderParam);
        if (!orderResult.success()) {
            return orderResult;
        }

        Map<String, Object> result = (Map<String, Object>) orderResult.getData();
        result.put("applyInfo", applyInfo);

        return Result.success("获取成功", result);
    }

    /**
     * 兑换订单-退货审核
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result orderRefundAudit(OrderAuditParam param, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer applyId = param.getApplyId();
        ExchangeOrderRefundApply applyInfo = exchangeOrderRefundApplyMapper.selectById(applyId);
        if (applyInfo == null) {
            return Result.error("申请不存在");
        }
        if (applyInfo.getStatus() != 1) {
            return Result.error("申请已审核，不可重复审核");
        }

        Integer orderId = applyInfo.getOrderId();
        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
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
                return Result.error("退货币额必须大于0");
            }
            if (refundAmount > orderInfo.getAmount()) {
                return Result.error("退货币额不能大于订单金额");
            }

            statusStr = "审核通过，退货申请原因：" + applyInfo.getReason();
        } else { // 驳回
            if (remark == null || "".equals(remark)) {
                return Result.error("请填写审核意见");
            }

            statusStr = "审核驳回，驳回原因：" + remark;
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + orderInfo.getUserId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("订单用户正在操作，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            ExchangeOrderRefundApply updateApply = new ExchangeOrderRefundApply();
            updateApply.setId(applyId);
            updateApply.setStatus(status);
            updateApply.setAdminId(authJwtData.getAdminId());
            updateApply.setAccount(authJwtData.getAccount());
            updateApply.setName(authJwtData.getName());
            updateApply.setRefundAmount(refundAmount);
            updateApply.setAuditTime(now);
            updateApply.setAuditRemark(remark);
            exchangeOrderRefundApplyMapper.update(updateApply);

            if (status == 2) { // 通过
                ExchangeOrder updateOrder = new ExchangeOrder();
                updateOrder.setId(orderId);
                updateOrder.setStatus(6);
                updateOrder.setRefundAmount(refundAmount);
                updateOrder.setRefundCashAmount(orderInfo.getCashAmount());
                updateOrder.setRefundTime(now);
                updateOrder.setRefundRemark(remark);
                exchangeOrderMapper.update(updateOrder);

                //更新商品库存数和兑换数
                productMapper.updateStockOrExchangeNum(orderInfo.getProductId(), orderInfo.getNum(),
                        -orderInfo.getNum());

                //更新用户金币数
                casUserMapper.incCoin(orderInfo.getUserId(), refundAmount, null);
                List<CasUserCoinLog> logList = new ArrayList<>();
                CasUserCoinLog goldCoinLog = new CasUserCoinLog();
                goldCoinLog.setTxnType(5);
                goldCoinLog.setTxnId(orderId);
                goldCoinLog.setCoinType(2);
                goldCoinLog.setNumType(1);
                goldCoinLog.setCoinNum(refundAmount);
                goldCoinLog.setUserId(orderInfo.getUserId());
                goldCoinLog.setPhone(orderInfo.getPhone());
                goldCoinLog.setNickName(orderInfo.getNickName());
                goldCoinLog.setTeamId(0);
                goldCoinLog.setTeamName("");
                goldCoinLog.setTeamType(0);
                goldCoinLog.setRemark("退货审核通过");
                goldCoinLog.setCreateTime(now);
                logList.add(goldCoinLog);
                casUserCoinLogMapper.insertAll(logList);

                if (orderInfo.getCashAmount() != null && orderInfo.getCashAmount() > 0
                        && orderInfo.getUpOrderId() != null && !orderInfo.getUpOrderId().isEmpty()) {
                    Result refundResult = tgyPayUtil.refund(orderInfo.getUpOrderId(), orderInfo.getCashAmount());
                    if (!refundResult.success()) {
                        throw new RuntimeException(refundResult.getMsg());
                    }
                }
            } else {
                ExchangeOrder updateOrder = new ExchangeOrder();
                updateOrder.setId(orderId);
                updateOrder.setStatus(2);
                exchangeOrderMapper.update(updateOrder);
            }

            ExchangeOrderLog orderLog = new ExchangeOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(authJwtData.getAdminId());
            orderLog.setUserName(authJwtData.getName());
            orderLog.setHandle("后台退货审核");
            orderLog.setDetails(authJwtData.getAccount() + "  " + authJwtData.getName() + statusStr);
            orderLog.setCreateTime(now);
            exchangeOrderLogMapper.insert(orderLog);

            // 事务提交
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            e.printStackTrace();

            return Result.error("操作失败");
        }

        //发送订阅消息
        Map<String, Object> msgData = new HashMap<>();
        String templateId = "";
        if (status == 2) {
            msgData.put("thing1", orderInfo.getProductName());
            msgData.put("amount2", refundAmount);
            msgData.put("short_thing3", "线上退款");
            msgData.put("character_string4", orderInfo.getOrderNo());
            msgData.put("thing5", "退款成功2小时内到账");

            templateId = wechatConfig.getProductRefundTemplateId();
        } else {
            msgData.put("character_string1", orderInfo.getOrderNo());
            msgData.put("amount2", orderInfo.getAmount());
            msgData.put("phrase4", "失败");
            msgData.put("time3", LocalDateUtil.formatTime(applyInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            msgData.put("thing5", "如有问题请联系客服");

            templateId = wechatConfig.getProductRejectionTemplateId();
        }
        CasUser userInfo = casUserMapper.selectById(orderInfo.getUserId());
        sendSubscribeMsgAsync.asyncSendSubscribeMsg(userInfo.getOpenId(), templateId, null, msgData);

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }
}
