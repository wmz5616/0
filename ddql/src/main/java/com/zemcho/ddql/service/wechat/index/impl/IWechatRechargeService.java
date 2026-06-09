package com.zemcho.ddql.service.wechat.index.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.kernel.geom.PageSize;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.wechat.WechatPayConfig;
import com.zemcho.ddql.controller.wechat.index.param.RechargeOrderParam;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderActCountVo;
import com.zemcho.ddql.controller.wechat.index.vo.RechargeOrderCountVo;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.order.RechargeOrder;
import com.zemcho.ddql.entity.order.RechargeOrderLog;
import com.zemcho.ddql.entity.recharge.RechargeActivity;
import com.zemcho.ddql.entity.recharge.RechargeConfig;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.order.RechargeOrderLogMapper;
import com.zemcho.ddql.mapper.order.RechargeOrderMapper;
import com.zemcho.ddql.mapper.recharge.RechargeActivityMapper;
import com.zemcho.ddql.mapper.recharge.RechargeConfigMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.wechat.index.WechatRechargeService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.FormatCheckUtils;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.file.FileUploadUtil;
import com.zemcho.ddql.util.mail.MailUtil;
import com.zemcho.ddql.util.pdf.PdfUtil;
import com.zemcho.ddql.util.pdf.ThymeleafUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import com.zemcho.ddql.util.uuid.OrderNoUtil;
import com.zemcho.ddql.util.wechatpay.WechatPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @title: IWechatRechargeService
 * @Description:
 * @Date: 2025/10/10 17:52
 */
@Service
@Slf4j
public class IWechatRechargeService implements WechatRechargeService {
    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private RechargeOrderLogMapper receiptOrderLogMapper;

    @Autowired
    private RechargeConfigMapper rechargeConfigMapper;

    @Autowired
    private RechargeActivityMapper rechargeActivityMapper;

    @Autowired
    CasUserMapper casUserMapper;

    @Autowired
    TeamUserMapper teamUserMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatPayUtil wechatPayUtil;

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    ThymeleafUtil thymeleafUtil;

    /**
     * 文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 上传文件存储在本地的路径
     */
    @Value("${file.upload-path}")
    private String uploadFilePath;

    /**
     * 获取充值活动列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result selectActivityList(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        List<RechargeActivity> rechargeActivityList = rechargeActivityMapper.selectList(param);
        List<RechargeActivity> list = new ArrayList<>();
        if (rechargeActivityList != null && !rechargeActivityList.isEmpty()) {
            SearchParam numCheckParam = new SearchParam();
            numCheckParam.setSearchField1(userId);
            numCheckParam.setSearchStatusList(Arrays.asList(1, 2, 4));
            List<RechargeOrderActCountVo> actCountList = rechargeOrderMapper.selectActCount(numCheckParam);
            Map<Integer, Integer> actCountMap = new HashMap<>();
            if (actCountList != null && !actCountList.isEmpty()) {
                actCountMap = actCountList.stream().collect(Collectors.toMap(RechargeOrderActCountVo::getActId,
                        RechargeOrderActCountVo::getOrderNum));
            }

            for (RechargeActivity rechargeActivity : rechargeActivityList) {
                if (rechargeActivity.getRechargeCount() > 0) {
                    if (actCountMap.get(rechargeActivity.getId()) != null &&
                            actCountMap.get(rechargeActivity.getId()) >= rechargeActivity.getRechargeCount()) {
                        continue;
                    }
                }

                list.add(rechargeActivity);
            }
        }

        return Result.success("获取成功", list);
    }

    /**
     * 添加充值订单
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result addOrder(RechargeOrderParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = param.getTeamId();
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, true);
        if (teamUser == null) {
            return Result.error("您无权选择该团体");
        }
        if (teamUser.getType() != 0 && teamUser.getType() != 1) {
            return Result.error("您非该团体管理者，不可充值");
        }
        Team teamInfo = teamMapper.selectById(teamId);
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        RechargeConfig rechargeConfig = rechargeConfigMapper.select();
        if (rechargeConfig == null) {
            return Result.error("暂未开启充值服务");
        }

        Integer amount = param.getAmount();
        if (amount <= 0) {
            return Result.error("充值金额必须大于0");
        }
        Integer giveAmount = 0;
        Integer actId = param.getActId();
        if (actId != 0) {
            RechargeActivity rechargeActivity = rechargeActivityMapper.selectById(actId);
            if (rechargeActivity == null) {
                return Result.error("活动不存在");
            }
            if (!amount.equals(rechargeActivity.getRechargeAmount())) {
                return Result.error("充值金额错误");
            }
            if (rechargeActivity.getEnableGift() == 1) {
                giveAmount = rechargeActivity.getGiftAmount();
            }
            if (rechargeActivity.getRechargeCount() > 0) {
                //充值次数限制
                SearchParam numCheckParam = new SearchParam();
                numCheckParam.setSearchField1(userId);
                numCheckParam.setSearchField3(actId);
                numCheckParam.setSearchStatusList(Arrays.asList(1, 2, 4));
                List<RechargeOrder> numCheckList = rechargeOrderMapper.selectLists(numCheckParam);
                if (numCheckList != null && numCheckList.size() >= rechargeActivity.getRechargeCount()) {
                    return Result.error("该活动的充值次数已达到限制，暂不可选择");
                }
            }
        } else {
            if (amount < rechargeConfig.getMinAmount()) {
                return Result.error("充值金额不能小于" + (rechargeConfig.getMinAmount() / 100));
            }
        }

        // 上锁
        String lockKey = Constant.TEAM_RECHARGE_ORDER_PREFIX + teamId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("该团体健康币正在使用，请稍后再试");
        }

        LocalDateTime now = LocalDateTime.now();
        String orderNo = OrderNoUtil.generateNo(userId);

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer orderId;
        try {
            RechargeOrder orderData = new RechargeOrder();
            orderData.setOrderNo(orderNo);
            orderData.setUserId(userId);
            orderData.setPhone(userInfo.getPhone());
            orderData.setNickName(userInfo.getNickname());
            orderData.setTeamId(teamId);
            orderData.setTeamName(teamInfo.getName());
            orderData.setTeamType(teamInfo.getType());
            orderData.setActId(actId);
            orderData.setAmount(amount);
            orderData.setGiveAmount(giveAmount);
            orderData.setPayType(1);
            orderData.setStatus(1);
            orderData.setCreateTime(now);
            rechargeOrderMapper.insert(orderData);
            orderId = orderData.getId();

            RechargeOrderLog orderLog = new RechargeOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderData.getOrderNo());
            orderLog.setUserId(userId);
            orderLog.setUserName(userInfo.getNickname());
            orderLog.setHandle("创建订单");
            orderLog.setDetails(userInfo.getNickname() + userInfo.getPhone() + "创建订单");
            orderLog.setCreateTime(now);
            receiptOrderLogMapper.insert(orderLog);

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

        // 添加到订单超时未支付监控
        Integer timeLimit = 30; // 目前系统没有配置的功能，先暂时写死30分钟
        long timestamp = LocalDateTime.now().plusMinutes(timeLimit)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String key = Constant.ORDER_UNPAY_MONITOR;
        redisUtil.zSetAdd(key, orderId, timestamp);

        return Result.success("操作成功", orderId);
    }

    /**
     * 获取充值订单详情
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

        return Result.success("获取成功", orderInfo);
    }

    /**
     * 获取充值订单支付配置信息
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result orderPayConfig(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        RechargeOrder orderInfo = rechargeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("记录不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            return Result.error("您无权操作该订单");
        }
        if (!orderInfo.getStatus().equals(1)) {
            return Result.error("该订单不可支付");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        Map<String, String> payConfig = wechatPayUtil.jsapiPay(orderInfo.getOrderNo(), "充值购币",
                orderInfo.getAmount(), userInfo.getOpenId(), wechatPayConfig.getRechargeOrderPayNotifyUrl());

        if (payConfig == null) {
            return Result.error("获取微信支付配置失败，请联系管理员");
        }

        return Result.success("获取成功", payConfig);
    }

    /**
     * 充值订单支付回调
     *
     * @param body
     * @param headers
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public ResponseEntity<String> orderPayCallback(String body, Map<String, String> headers) {
        Transaction transaction = wechatPayUtil.wxNotifyCallback(body, headers, Transaction.class);
        if (transaction == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "FAIL");
            response.put("message", "回调解析处理失败");
            return wechatPayUtil.wxNotifyFailResponse(response);
        }

        //支付成功
        if ("SUCCESS".equals(transaction.getTradeState().name())) {
            String orderNo = transaction.getOutTradeNo();
            String wxTransactionNo = transaction.getTransactionId();

            RechargeOrder orderInfo = rechargeOrderMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "FAIL");
                response.put("message", "订单不存在");
                return wechatPayUtil.wxNotifyFailResponse(response);
            }
            if (!orderInfo.getStatus().equals(1) && !orderInfo.getStatus().equals(3)) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "FAIL");
                response.put("message", "订单状态异常");
                return wechatPayUtil.wxNotifyFailResponse(response);
            }

            // 上锁
            String lockKey = Constant.ORDER_PAY_CALLBACK_LOCK_PREFIX + orderNo;
            Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
            if (!lockFlag) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", "FAIL");
                response.put("message", "请求频繁");
                return wechatPayUtil.wxNotifyFailResponse(response);
            }

            LocalDateTime now = LocalDateTime.now();

            // 开启事务
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

            try {
                // 更新订单信息
                RechargeOrder updateOrder = new RechargeOrder();
                updateOrder.setId(orderInfo.getId());
                updateOrder.setWxTransactionNo(wxTransactionNo);
                updateOrder.setStatus(2);
                updateOrder.setPayTime(now);
                rechargeOrderMapper.update(updateOrder);

                // 添加订单日志
                RechargeOrderLog orderLog = new RechargeOrderLog();
                orderLog.setOrderId(orderInfo.getId());
                orderLog.setOrderNo(orderInfo.getOrderNo());
                orderLog.setUserId(orderInfo.getUserId());
                orderLog.setUserName(orderInfo.getNickName());
                orderLog.setHandle("订单支付");
                orderLog.setDetails(orderInfo.getNickName() + orderInfo.getPhone() + "完成支付");
                orderLog.setCreateTime(now);
                receiptOrderLogMapper.insert(orderLog);

                //更新团体健康币数量
                Integer totalNum = orderInfo.getGiveAmount() + orderInfo.getAmount();
                Integer healthCoin = totalNum / 100;
                teamMapper.incCoin(orderInfo.getTeamId(), healthCoin);

                // 事务提交
                platformTransactionManager.commit(transactionStatus);
            } catch (Exception e) {
                // 事务回滚
                platformTransactionManager.rollback(transactionStatus);

                // 解锁
                redisLockUtil.unlock(lockKey);

                e.printStackTrace();

                Map<String, Object> response = new HashMap<>();
                response.put("code", "FAIL");
                response.put("message", "回调处理失败");
                return wechatPayUtil.wxNotifyFailResponse(response);
            }

            // 解锁
            redisLockUtil.unlock(lockKey);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", "SUCCESS");
        response.put("message", "成功");
        return wechatPayUtil.wxNotifySuccessResponse(response);
    }

    /**
     * 获取充值订单列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result orderLists(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        //只能查看自己团体下的数据
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, null);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>());
        }
        List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toList());
        Integer searchTeamId = param.getSearchField2();
        if (searchTeamId != null) {
            if (!teamIds.contains(searchTeamId)) {
                return Result.error("您无权查看该团体信息");
            }
        } else {
            param.setSearchIntList(teamIds);
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<RechargeOrder> list = rechargeOrderMapper.selectLists(param);
        PageInfo<RechargeOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取充值订单统计数据
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result orderCountData(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        //只能查看自己团体下的数据
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, null);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>());
        }
        List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toList());
        Integer searchTeamId = param.getSearchField2();
        if (searchTeamId != null) {
            if (!teamIds.contains(searchTeamId)) {
                return Result.error("您无权查看该团体信息");
            }
        } else {
            param.setSearchIntList(teamIds);
        }

        RechargeOrderCountVo data = rechargeOrderMapper.selectCount(param);

        return Result.success("获取成功", data);
    }

    /**
     * 充值订单数据导出到邮箱
     *
     * @param param
     * @param token
     * @param request
     * @return
     */
    @Override
    public Result orderExportToEmail(SearchParam param, String token, HttpServletRequest request) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        //只能查看自己团体下的数据
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, null);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>());
        }
        List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toList());
        Integer searchTeamId = param.getSearchField2();
        if (searchTeamId != null) {
            if (!teamIds.contains(searchTeamId)) {
                return Result.error("您无权查看该团体信息");
            }
        } else {
            param.setSearchIntList(teamIds);
        }

        String email = param.getSearchStrField3();
        if (email == null || email.isEmpty()) {
            return Result.error("请输入邮箱");
        }
        if (!FormatCheckUtils.isEmail(email)) {
            return Result.error("邮箱格式错误");
        }

        List<RechargeOrder> list = rechargeOrderMapper.selectLists(param);

        // 生成pdf
        Map<String, Object> params = new HashMap<>();
        params.put("list", list);
        List<String> pageHtml = new ArrayList<>();
        String html = thymeleafUtil.getThymeleafTemHtml("RechargeExportToMailSendTmpl", params, request);
        pageHtml.add(html);
        String pdfFilePath = localFilePath + uploadFilePath + "/recharge_pdf/";
        String pdfFileName = "recharge_export_" + LocalDateUtil.getMilliByTime(LocalDateTime.now());
        String pdfPath = pdfFilePath + pdfFileName + ".pdf";
        Double paperWidth = PdfUtil.mmToPt(210);
        Double paperHeight = PdfUtil.mmToPt(297);
        PageSize pageSize = new PageSize(paperWidth.floatValue(), paperHeight.floatValue());
        Double topMargins = PdfUtil.mmToPt(10);
        Double leftMargins = PdfUtil.mmToPt(20);
        Map<String, Double> marginsMap = new HashMap<>();
        marginsMap.put("top", topMargins);
        marginsMap.put("right", leftMargins);
        marginsMap.put("bottom", topMargins);
        marginsMap.put("left", leftMargins);
        PdfUtil.pageHtmlConvertToPdf(pageHtml, pdfFileName, pdfFilePath, pageSize, marginsMap);

        //添加水印
        String watermarkPdfPath = pdfFilePath + "watermark_" + pdfFileName + ".pdf";
        PdfUtil.addWatermarkToPdf(pdfPath, watermarkPdfPath, "都动起来");

        //发送邮件
        MailUtil.MessageHelper helper = new MailUtil.MessageHelper();
        helper.setTo(email);
        helper.setSubject("充值记录");
        helper.addAttachment("充值记录.pdf", new FileSystemResource(new File(watermarkPdfPath)));
        Map<String, Object> EmailMap = new HashMap<>();
        MailUtil.send(helper, "RechargeExportEmailTmpl.ftl", EmailMap);

        //删除pdf文件
        FileUploadUtil.delFile(pdfPath);
        FileUploadUtil.delFile(watermarkPdfPath);

        return Result.success("操作成功");
    }
}
