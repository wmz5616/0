package com.zemcho.ddql.service.order.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wechat.pay.java.service.refund.model.Refund;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AuthJwtData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.controller.order.param.OrderRefundParam;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderCountVo;
import com.zemcho.ddql.entity.order.RechargeOrder;
import com.zemcho.ddql.entity.order.RechargeOrderLog;
import com.zemcho.ddql.mapper.order.RechargeOrderLogMapper;
import com.zemcho.ddql.mapper.order.RechargeOrderMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.service.order.RechargeOrderService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.excel.ExcelUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.uuid.OrderNoUtil;
import com.zemcho.ddql.util.wechatpay.WechatPayUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @title: IRechargeOrderService
 * @Description:
 * @Date: 2025/10/24 14:46
 */
@Service
public class IRechargeOrderService implements RechargeOrderService {
    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private RechargeOrderLogMapper receiptOrderLogMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private WechatPayUtil wechatPayUtil;

    /**
     * 充值订单列表
     *
     * @param param
     * @return
     */
    @Override
    public Result orderLists(SearchParam param) {
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<RechargeOrder> list = rechargeOrderMapper.selectLists(param);
        PageInfo<RechargeOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 充值订单统计信息
     *
     * @param param
     * @return
     */
    @Override
    public Result orderStat(SearchParam param) {
        //统计总的数据
        RechargeOrderCountVo totalStatData = rechargeOrderMapper.selectCount(param);

        //统计今日内的数据
        param.setStartTime(LocalDateUtil.strToLDT(LocalDateUtil.getStartTime()));
        param.setEndTime(LocalDateUtil.strToLDT(LocalDateUtil.getEndTime()));
        RechargeOrderCountVo todayStatData = rechargeOrderMapper.selectCount(param);

        Map<String, Object> result = new HashMap<>();
        result.put("totalStatData", totalStatData);
        result.put("todayStatData", todayStatData);

        return Result.success("获取成功", result);
    }

    /**
     * 充值订单数据导出
     *
     * @param param
     * @param response
     */
    @Override
    public void orderExport(SearchParam param, HttpServletResponse response) {
        List<RechargeOrder> list = rechargeOrderMapper.selectLists(param);
        ExcelUtil.exportToWeb(response, list, "充值订单信息", "充值订单信息", RechargeOrder.class);
    }

    /**
     * 充值订单详情
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

        RechargeOrder orderInfo = rechargeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        List<RechargeOrderLog> logList = receiptOrderLogMapper.selectByOrderId(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("logList", logList);

        return Result.success("获取成功", result);
    }

    /**
     * 充值订单退款
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result orderRefund(OrderRefundParam param, String token) {
        AuthJwtData authJwtData = JWTUtil.getAuthJwtData(token);
        if (authJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getOrderId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        RechargeOrder orderInfo = rechargeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (orderInfo.getStatus() != 2) {
            return Result.error("该订单状态不可操作退款");
        }

        Integer refundAmount = param.getRefundAmount();
        if (refundAmount == null || refundAmount <= 0) {
            return Result.error("退款金额必须大于0");
        }
        if (refundAmount > orderInfo.getAmount()) {
            return Result.error("退款金额不能大于订单金额");
        }

        // 上锁
        String lockKey = Constant.TEAM_RECHARGE_ORDER_PREFIX + orderInfo.getTeamId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("该团体健康币正在使用，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();
        String refundNo = OrderNoUtil.generateNo(authJwtData.getAdminId());

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        try {
            // 更新订单信息
            RechargeOrder updateOrder = new RechargeOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(4);
            updateOrder.setRefundNo(refundNo);
            updateOrder.setRefundAmount(refundAmount);
            updateOrder.setRefundTime(now);
            updateOrder.setRefundRemark(param.getRefundReason());
            rechargeOrderMapper.update(updateOrder);

            // 添加订单日志
            RechargeOrderLog orderLog = new RechargeOrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setOrderNo(orderInfo.getOrderNo());
            orderLog.setUserId(orderInfo.getUserId());
            orderLog.setUserName(orderInfo.getNickName());
            orderLog.setHandle("订单退款");
            orderLog.setDetails(authJwtData.getAccount() + "  " + authJwtData.getName() + "操作退款，退款金额：" + (refundAmount / 100) + "元");
            orderLog.setCreateTime(now);
            receiptOrderLogMapper.insert(orderLog);

            //更新团体健康币数量
            Integer healthCoin = refundAmount / 100;
            teamMapper.decCoin(orderInfo.getTeamId(), healthCoin);

            //微信退款
            Refund result = wechatPayUtil.refund(orderInfo.getOrderNo(), refundNo, orderInfo.getAmount(),
                    refundAmount, null);
            if (result == null) {
                throw new RuntimeException("微信退款失败");
            }

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

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }
}
