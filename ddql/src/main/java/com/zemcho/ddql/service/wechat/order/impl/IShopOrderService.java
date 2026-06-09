package com.zemcho.ddql.service.wechat.order.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.tgy_pay.MerchantConfig;
import com.zemcho.ddql.config.wechat.WechatPayConfig;
import com.zemcho.ddql.controller.wechat.order.param.WechatShopOrderParam;
import com.zemcho.ddql.controller.wechat.order.vo.WechatShopOrderVo;
import com.zemcho.ddql.controller.wechat.personalCenter.param.WechatShopOrderListParam;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderListVo;
import com.zemcho.ddql.controller.wechat.personalCenter.vo.WechatShopOrderStatVo;
import com.zemcho.ddql.controller.wechat.shop.param.OrderItem;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateParam;
import com.zemcho.ddql.controller.wechat.shop.param.ShopOrderCreateVo;
import com.zemcho.ddql.entity.business.BusinessCircle;
import com.zemcho.ddql.entity.business.CoinRule;
import com.zemcho.ddql.entity.cas.CasUser;
import com.zemcho.ddql.entity.cas.CasUserCoinLog;
import com.zemcho.ddql.entity.product.Product;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.business.*;
import com.zemcho.ddql.mapper.cas.CasUserCoinLogMapper;
import com.zemcho.ddql.mapper.cas.CasUserMapper;
import com.zemcho.ddql.mapper.product.ProductMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.product.ProductService;
import com.zemcho.ddql.service.wechat.personalCenter.impl.WechatShopOrderServiceImpl;
import com.zemcho.ddql.util.Constant;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.controller.order.vo.ExpressOrderVo;
import com.zemcho.ddql.controller.wechat.index.vo.ExchangeOrderAddressVo;
import com.zemcho.ddql.controller.wechat.order.param.ShopOrderRefundParam;
import com.zemcho.ddql.controller.wechat.shop.vo.BusinessDataVO;
import com.zemcho.ddql.controller.wechat.shop.vo.ShopOrderExportVo;
import com.zemcho.ddql.entity.business.Shop;
import com.zemcho.ddql.entity.express.ExpressOrder;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.personalCenter.ShopOrder;
import com.zemcho.ddql.entity.personalCenter.ShopOrderDetail;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderAddressMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.order.ShopOrderDetailMapper;
import com.zemcho.ddql.mapper.order.ShopOrderMapper;
import com.zemcho.ddql.service.wechat.order.ShopOrderService;
import com.zemcho.ddql.util.KeyUtil;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import com.zemcho.ddql.util.tgy.SignUtil;
import com.zemcho.ddql.util.tgy.TgyPayUtil;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import com.zemcho.ddql.util.uuid.OrderNoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author HXH
 */
@Slf4j
@Service
public class IShopOrderService implements ShopOrderService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopOrderMapper shopOrderMapper;
    @Autowired
    private ShopOrderDetailMapper shopOrderDetailMapper;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;
    @Autowired
    private CasUserMapper casUserMapper;
    @Autowired
    private TeamUserMapper teamUserMapper;
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private TgyPayUtil tgyPayUtil;
    @Autowired
    private OrderNoUtil orderNoUtil;
    @Autowired
    private BusinessCircleShopMapper businessCircleShopMapper;
    @Autowired
    private BusinessCircleMapper businessCircleMapper;
    @Autowired
    private MerchantConfig merchantConfig;
    @Autowired
    private CoinRuleMapper coinRuleMapper;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private ShopManagerMapper shopManagerMapper;


    @Override
    public Result select(WechatShopOrderParam param, String token) {
        // 先从小程序 token 中解析当前登录用户。
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return new Result(10006, "用户不存在");
        }
        //校验用户权限
        String phone = casUser.getPhone();
        if(shopManagerMapper.selectIfExitByShopIdAndPhone(param.getId(), phone)==null){
            return new Result(10006, "无权访问");
        }
        // 前端未传时间时，默认补齐最近 6 个月的查询区间。
        fillDefaultTime(param);
        if (param.getStartTime().isAfter(param.getEndTime())) {
            return Result.error("开始时间不能晚于结束时间");
        }
        // 日期入参转换成查询用的完整时间范围，覆盖整天数据。
        buildQueryTime(param);

        // 分页查询当前用户的门店订单列表。
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<WechatShopOrderVo> list = shopOrderMapper.selectWechatShopOrderList(param);
        PageInfo<WechatShopOrderVo> pageInfo = new PageInfo<>(list);

        // 同步查询顶部统计信息，给前端渲染“总笔数/总金额”区域。
        WechatShopOrderStatVo statVo = shopOrderMapper.selectShopOrderStat(param);
        if (statVo == null) {
            statVo = new WechatShopOrderStatVo();
            statVo.setTotalNum(0);
            statVo.setTotalPayAmount(0);
        }

        return Result.success("获取成功", pageInfo, statVo);
    }

    @Override
    public void businessDataExport(WechatShopOrderParam param, HttpServletResponse response, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            throw new IllegalArgumentException("token无效");
        }
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        String phone = casUser.getPhone();
        if (shopManagerMapper.selectIfExitByShopIdAndPhone(param.getId(), phone)==null) {
            throw new IllegalArgumentException("无权访问");
        }
        fillDefaultTime(param);
        if (param.getStartTime().isAfter(param.getEndTime())) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        buildQueryTime(param);
        Shop shop = shopMapper.selectById(param.getId());
        if (shop == null) {
            throw new IllegalArgumentException("该商家不存在");
        }
        List<WechatShopOrderVo> shopOrderList = shopOrderMapper.selectWechatShopOrderList(param);
        WechatShopOrderStatVo statVo = shopOrderMapper.selectShopOrderStat(param);
        if (statVo == null) {
            statVo = new WechatShopOrderStatVo();
            statVo.setTotalNum(0);
            statVo.setTotalPayAmount(0);
        }
        int totalOrderCount = shopOrderList.size();
        int totalPayAmount = shopOrderList.stream()
                .filter(order -> order.getPayAmount() != null)
                .mapToInt(WechatShopOrderVo::getPayAmount)
                .sum();
        int totalAmount = shopOrderList.stream()
                .filter(order -> order.getTotalAmount() != null)
                .mapToInt(WechatShopOrderVo::getTotalAmount)
                .sum();
        int totalDeductCoin = shopOrderList.stream()
                .filter(order -> order.getDeductCoin() != null)
                .mapToInt(WechatShopOrderVo::getDeductCoin)
                .sum();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ShopOrderExportVo> exportList = new ArrayList<>();
        for (WechatShopOrderVo order : shopOrderList) {
            ShopOrderExportVo vo = new ShopOrderExportVo();
            vo.setOrderNo(order.getOrderNo());
            if (order.getOrderTime() != null) {
                vo.setOrderTime(order.getOrderTime().format(formatter));
            }
            if (order.getTotalAmount() != null) {
                vo.setTotalAmount(new BigDecimal(order.getTotalAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
            }
            if (order.getPayAmount() != null) {
                vo.setPayAmount(new BigDecimal(order.getPayAmount()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
            }
            vo.setDeductCoin(order.getDeductCoin());
            vo.setOrderType(order.getStatus() != null && order.getStatus() == 1 ? "收款" : "退款");
            String userInfo = "";
            if (order.getNickName() != null) {
                userInfo = order.getNickName();
            }
            if (order.getPhone() != null) {
                userInfo += order.getPhone();
            }
            vo.setUserInfo(userInfo);
            vo.setRefundReason(order.getRefundReason());
            if (order.getRefundTime() != null) {
                vo.setRefundTime(order.getRefundTime().format(formatter));
            }
            exportList.add(vo);
        }
        ShopOrderExportVo summaryVo = new ShopOrderExportVo();
        summaryVo.setOrderNo("合计");
        summaryVo.setTotalAmount(new BigDecimal(totalAmount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
        summaryVo.setPayAmount(new BigDecimal(totalPayAmount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
        summaryVo.setDeductCoin(totalDeductCoin);
        exportList.add(summaryVo);
        try {
            String fileName = shop.getName() + "交易记录";
            String dateRange = "";
            if (param.getStartTime() != null && param.getEndTime() != null) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                dateRange = param.getStartTime().format(dateFormatter) + "-" + param.getEndTime().format(dateFormatter);
            }
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            com.alibaba.excel.ExcelWriter excelWriter = EasyExcel.write(byteArrayOutputStream)
                    .registerWriteHandler(new HorizontalCellStyleStrategy(getHeadStyle(), getDataStyle()))
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet("交易记录")
                    .head(ShopOrderExportVo.class)
                    .needHead(true)
                    .build();
            excelWriter.write(exportList, writeSheet);
            excelWriter.finish();
            org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(
                    new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            sheet.shiftRows(0, sheet.getLastRowNum(), 3, true, false);
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(25);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(fileName);
            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setFontName("宋体");
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));
            org.apache.poi.ss.usermodel.Row dateRow = sheet.createRow(1);
            org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(dateRange);
            org.apache.poi.ss.usermodel.CellStyle dateStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font dateFont = workbook.createFont();
            dateFont.setFontName("宋体");
            dateFont.setFontHeightInPoints((short) 11);
            dateStyle.setFont(dateFont);
            dateCell.setCellStyle(dateStyle);
            org.apache.poi.ss.usermodel.Row summaryRow = sheet.createRow(2);
            org.apache.poi.ss.usermodel.Cell countCell = summaryRow.createCell(0);
            countCell.setCellValue("共" + totalOrderCount + "笔");
            org.apache.poi.ss.usermodel.CellStyle summaryStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font summaryFont = workbook.createFont();
            summaryFont.setFontName("宋体");
            summaryFont.setFontHeightInPoints((short) 11);
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            countCell.setCellStyle(summaryStyle);
            org.apache.poi.ss.usermodel.Cell amountCell = summaryRow.createCell(1);
            amountCell.setCellValue("实付总金额： " + new BigDecimal(totalPayAmount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
            amountCell.setCellStyle(summaryStyle);
            workbook.write(response.getOutputStream());
            workbook.close();
            byteArrayOutputStream.close();
            log.info("商家交易记录导出成功 - 商家：{}, 总订单数：{}, 总实付金额：{}元",
                    shop.getName(), totalOrderCount, new BigDecimal(totalPayAmount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
            log.error("导出交易记录失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }

    @Override
    public Result selectDetail(SearchParam param, String token) {
        Integer searchId = param.getSearchId();
        if (searchId == null) {
            return Result.error("参数错误");
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(searchId);
        if (shopOrder == null) {
            return Result.error("订单不存在");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("shopOrder", shopOrder);

        return Result.success("查询成功", map);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result refund(ShopOrderRefundParam param, String token) {
        //校验用户权限
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        Integer orderId = param.getOrderId();
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null) {
            return Result.error("订单不存在");
        }
        //1.校验输入金额是否大于实付金额
        Integer payAmount = shopOrder.getPayAmount();//实付金额
        if (param.getRefundAmount() > payAmount) {
            return Result.error("退款金额不能大于实付金额");
        }
        //2.校验退回金币数不得大于抵扣金币数
        Integer deductCoin = shopOrder.getDeductCoin();//退回金币数
        if (param.getRefundCoin() > deductCoin) {
            return Result.error("退回币额不能大于抵扣金币数");
        }
        //3.校验订单状态
        Integer status = shopOrder.getStatus();
        if (status != 1) {
            return Result.error("该订单状态不可操作退款");
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + "productOrderRefund:" + shopOrder.getUserId();
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("订单用户正在操作，请稍后再试");
        }

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            LocalDateTime now = LocalDateTime.now();
            String refundReason = param.getRefundReason();
            //更新订单总表
            ShopOrder updateOrder = new ShopOrder();
            updateOrder.setId(orderId);
            updateOrder.setStatus(2);//设置为已退款
            updateOrder.setRefundReason(refundReason);//退款原因
            updateOrder.setRefundTime(now);
            shopOrderMapper.update(updateOrder);
            if (param.getRefundCoin() > 0) {
                //更新用户金币数量
                CasUserCoinLog coinLog = new CasUserCoinLog();
                coinLog.setTxnType(5);//门店退款
                coinLog.setTxnId(orderId);
                coinLog.setCoinType(2);//金币
                coinLog.setNumType(1);//增加
                coinLog.setCoinNum(param.getRefundCoin());
                coinLog.setUserId(shopOrder.getUserId());
                coinLog.setPhone(shopOrder.getPhone());
                coinLog.setNickName(shopOrder.getNickName());
                extracted(shopOrder, coinLog);
                coinLog.setRemark("门店订单退款");
                coinLog.setCreateTime(now);
                ArrayList<CasUserCoinLog> casUserCoinLogs = new ArrayList<>();
                casUserCoinLogs.add(coinLog);
                casUserCoinLogMapper.insertAll(casUserCoinLogs);

                //更新用户的金币数量
                casUserMapper.incCoin(shopOrder.getUserId(), param.getRefundCoin(), null);
            }
            //退款处理,在有退款金额再做退款处理
            if (param.getRefundAmount() > 0) {
                Result tgyResult = tgyPayUtil.refund(shopOrder.getOrderNo(), param.getRefundAmount());
                if (!tgyResult.success()) {
                    throw new Exception("通莞退款失败");
                }
            }

            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);

            // 解锁
            redisLockUtil.unlock(lockKey);

            return Result.error("操作失败");
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    //记录用户的团队信息
    private void extracted(ShopOrder shopOrder, CasUserCoinLog coinLog) {
        TeamUser teamUser = teamUserMapper.selectByPhone(shopOrder.getPhone());
        if (teamUser != null && teamUser.getTeamId() != null) {
            Team team = teamMapper.selectById(teamUser.getTeamId());
            if (team != null) {
                coinLog.setTeamId(team.getId());
                coinLog.setTeamName(team.getName());
                coinLog.setTeamType(team.getType());
            } else {
                coinLog.setTeamId(0);
                coinLog.setTeamName("");
                coinLog.setTeamType(0);
            }
        } else {
            coinLog.setTeamId(0);
            coinLog.setTeamName("");
            coinLog.setTeamType(0);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result createOrder(ShopOrderCreateParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        //校验用户身份
        CasUser casUser = casUserMapper.selectById(userId);
        if (casUser == null) {
            return Result.error("用户不存在");
        }
        String phone = casUser.getPhone();
        String nickname = casUser.getNickname();
        Integer shopId = param.getShopId();
        Integer totalAmount = param.getTotalAmount();//订单总金额
        Integer deductCoin = param.getDeductCoin() != null ? param.getDeductCoin() : 0;//抵扣金币数
        Integer deductAmount=param.getTotalAmount()-param.getPayAmount();
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return Result.error("店铺不存在");
        }

        String lockKey = Constant.USER_OPERATION_PREFIX + "shopOrderCreate:" + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("操作过于频繁，请稍后再试");
        }

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            LocalDateTime now = LocalDateTime.now();
            //记录到shop_order表中
            //生成订单编号
            String orderNo = orderNoUtil.generateNo(userId);

            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setOrderNo(orderNo);
            shopOrder.setShopId(shopId);
            shopOrder.setShopName(shop.getName());
            //获取商圈id
            List<Integer> circleIds = businessCircleShopMapper.selectCircleIdsByShopId(shopId);
            shopOrder.setBusinessCircleId(circleIds.get(0) != null ? circleIds.get(0) : 0);
            BusinessCircle businessCircle = businessCircleMapper.selectById(circleIds.get(0));
            if (businessCircle != null) {
                shopOrder.setBusinessCircleName(businessCircle.getName());
            } else {
                shopOrder.setBusinessCircleName("");
            }
            shopOrder.setTotalAmount(totalAmount);
            shopOrder.setDeductCoin(deductCoin);

            shopOrder.setDeductAmount(param.getDeductCoin());
            shopOrder.setPayAmount(param.getPayAmount());
            shopOrder.setDeductAmount(deductAmount);
            shopOrder.setOrderTime(now);
            shopOrder.setUserId(userId);
            shopOrder.setNickName(nickname);
            shopOrder.setStatus(0);//未支付
            shopOrder.setDivideStatus(0);//未分账
            shopOrder.setDivideAmount(0);
            shopOrder.setEquipmentId(0);
            shopOrder.setPhone(phone);
            shopOrder.setOrderTime(now);
            shopOrder.setCreateTime(now);
            shopOrder.setUpdateTime(now);
            //插入到订单明细表shop_order表
            shopOrderMapper.insert(shopOrder);

            //有抵扣金币
            if (deductCoin > 0) {
                casUserMapper.incCoin(userId, -deductCoin, null);

                CasUserCoinLog coinLog = new CasUserCoinLog();
                coinLog.setTxnType(3);//兑换
                coinLog.setTxnId(shopOrder.getId());
                coinLog.setCoinType(2);//金币
                coinLog.setNumType(2);//减
                coinLog.setCoinNum(deductCoin);
                coinLog.setUserId(userId);
                extracted(shopOrder, coinLog);
                coinLog.setRemark("门店扫码支付抵扣金币");
                coinLog.setCreateTime(now);

                List<CasUserCoinLog> coinLogList = new ArrayList<>();
                coinLogList.add(coinLog);
                casUserCoinLogMapper.insertAll(coinLogList);
            }

            ShopOrderCreateVo resultVo = new ShopOrderCreateVo();
            resultVo.setOrderId(shopOrder.getId());
            resultVo.setOrderNo(orderNo);
            resultVo.setTotalAmount(totalAmount);
            resultVo.setDeductCoin(deductCoin);
            resultVo.setDeductAmount(deductAmount);
            resultVo.setPayAmount(param.getPayAmount());
            resultVo.setCreateTime(now);

            platformTransactionManager.commit(transactionStatus);
            redisLockUtil.unlock(lockKey);

            return Result.success("订单创建成功", resultVo);
        } catch (Exception e) {
            platformTransactionManager.rollback(transactionStatus);
            redisLockUtil.unlock(lockKey);
            log.error("创建门店订单失败 userId:{}", userId, e);
            return Result.error("创建订单失败：" + e.getMessage());
        }

    }
    @Override
    public Result pay(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getSearchId();
        if (orderId == null) {
            return Result.error("参数异常");
        }

        // 上锁开始操作
        String lockKey = Constant.USER_OPERATION_PREFIX + "shopOrderPayConfig:" + orderId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("订单不存在");
        }

        if (!shopOrder.getUserId().equals(userId)) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您无权操作该订单");
        }

        if (!shopOrder.getStatus().equals(0)) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("该订单不可支付");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }

        String orderNo = shopOrder.getOrderNo();
        Integer payAmount = shopOrder.getPayAmount();

        if (payAmount == null || payAmount <= 0) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("订单支付金额异常");
        }

        String notifyUrl = merchantConfig.getProductOrderCallBackUrl();
        String body = "扫码消费-" + shopOrder.getShopName();

        Result payResult = tgyPayUtil.getWxJsPayConfig(
                userInfo.getOpenId(),
                payAmount,
                orderNo,
                body,
                notifyUrl,
                true
        );

        if (!payResult.success()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("获取微信支付配置失败：");
        }

        Map<String, Object> resultData = (Map<String, Object>) payResult.getData();
        String upOrderId = resultData.get("upOrderId").toString();
        Map<String, Object> payInfo = (Map<String, Object>) resultData.get("payInfo");

        // 将通莞订单号保存到数据库中
        if (shopOrder.getOrderNo() == null || !shopOrder.getOrderNo().equals(upOrderId)) {
            ShopOrder updateOrder = new ShopOrder();
            updateOrder.setId(shopOrder.getId());
            updateOrder.setUpOrderId(upOrderId);
            updateOrder.setStatus(1);//已支付
            shopOrderMapper.update(updateOrder);
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("获取成功", payInfo);
    }

    @Override
    public String shopOrderPayCallBack(WxJsPayCallBackResponse response) {
        if (response == null) {
            return "fail";
        }
        if (!SignUtil.verifySign(merchantConfig.getKey(), response.getSign(), response)) {
            return "fail";
        }
        if (!"0".equals(response.getState())) {
            return "fail";
        }
        //获取订单号
        String orderNo = response.getLowOrderId();
        if (orderNo == null || "".equals(orderNo)) {
            return "fail";
        }

        ShopOrder shopOrder = shopOrderMapper.selectByOrderNo(orderNo);
        if(shopOrder==null){
            return "fail";
        }
        //不是待支付状态表示付款成功
        if(!shopOrder.getStatus().equals(0)){
            return "success";
        }
        //判断支付金额是否一致
        Integer callbackAmount = getTgyPayAmount(response.getPayMoney());
        if (!Objects.equals(callbackAmount, shopOrder.getPayAmount())) {
            return "fail";
        }

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        String state = response.getState();
        try {
            shopOrder = shopOrderMapper.selectByOrderNo(orderNo);
            if (shopOrder == null) {
                platformTransactionManager.rollback(transactionStatus);
                return "fail";
            }
            if (!shopOrder.getStatus().equals(0)) {
                platformTransactionManager.commit(transactionStatus);
                return "success";
            }

            CasUser userInfo = casUserMapper.selectById(shopOrder.getUserId());
            if (userInfo == null) {
                throw new RuntimeException("用户不存在");
            }

            LocalDateTime payTime = LocalDateTime.now();
            if (response.getPayTime() != null && !"".equals(response.getPayTime())) {
                payTime = LocalDateUtil.strToLDT(response.getPayTime());
            }
            if(state.equals("0")){
                //支付成功
                ShopOrder shopOrder1 = new ShopOrder();
                shopOrder1.setId(shopOrder.getId());
                shopOrder1.setUpOrderId(response.getUpOrderId());//通莞订单号
                shopOrder1.setStatus(1);//支付成功
                shopOrder1.setPayTime(payTime);
                shopOrderMapper.update(shopOrder1);

                // 事务提交
                platformTransactionManager.commit(transactionStatus);
            }

         }catch (Exception e) {
            // 事务回滚
            platformTransactionManager.rollback(transactionStatus);
            log.error("商品订单支付回调处理失败，订单号：{}", response.getLowOrderId(), e);
            return "fail";
        }
        return "success";
    }

    private WriteCellStyle getHeadStyle() {
        WriteCellStyle headStyle = new WriteCellStyle();
        WriteFont headFont = new WriteFont();
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 11);
        headFont.setBold(true);
        headStyle.setWriteFont(headFont);
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return headStyle;
    }

    private List<WriteCellStyle> getDataStyle() {
        List<WriteCellStyle> list = new ArrayList<>();
        WriteCellStyle dataStyle = new WriteCellStyle();
        WriteFont dataFont = new WriteFont();
        dataFont.setFontName("宋体");
        dataFont.setFontHeightInPoints((short) 10);
        dataStyle.setWriteFont(dataFont);
        list.add(dataStyle);
        return list;
    }

    private Integer getTgyPayAmount(String payMoney) {
        if (payMoney == null || "".equals(payMoney)) {
            return null;
        }
        return new java.math.BigDecimal(payMoney).movePointRight(2).intValue();
    }


    private void fillDefaultTime(WechatShopOrderParam param) {
        // 开始和结束时间都存在时，直接使用前端传值。
        if (param.getStartTime() != null && param.getEndTime() != null) {
            return;
        }

        // 其余情况统一回退到“当前时间往前 6 个月”。
        LocalDate endDate = LocalDate.now();
        param.setEndTime(endDate);
        param.setStartTime(endDate.minusMonths(6));
    }

    private void buildQueryTime(WechatShopOrderParam param) {
        param.setQueryStartTime(param.getStartTime().atStartOfDay());
        param.setQueryEndTime(LocalDateTime.of(param.getEndTime(), LocalTime.MAX));
    }
}
