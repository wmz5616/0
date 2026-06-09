package com.zemcho.ddql.job.init_task;

import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.other.OtherConfig;
import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.product.param.ProductSearchParam;
import com.zemcho.ddql.entity.cas.CasAdmin;
import com.zemcho.ddql.entity.cas.CasUserCheckInRecord;
import com.zemcho.ddql.entity.cas.CasUserCheckInTeamRecord;
import com.zemcho.ddql.entity.cas.CasUserCoinLog;
import com.zemcho.ddql.entity.product.Product;
import com.zemcho.ddql.entity.product.ProductTicket;
import com.zemcho.ddql.mapper.cas.*;
import com.zemcho.ddql.mapper.product.ProductMapper;
import com.zemcho.ddql.mapper.product.ProductTicketMapper;
import com.zemcho.ddql.service.cas.async.AdminPerAsync;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
@Slf4j
public class InitDataTask {
    @Autowired
    CasRuleMapper casRuleMapper;

    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;

    @Autowired
    private CasUserCheckInTeamRecordMapper casUserCheckInTeamRecordMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private CasUserCheckInRecordMapper casUserCheckInRecordMapper;

    @Autowired
    OtherConfig otherConfig;

    @Autowired
    AdminPerAsync adminPerAsync;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        //初始化菜单数据
        initMenuRule();

        //初始化管理员权限缓存
        initAdminPerCache();

        //初始化用户打卡币获取redis标志
        initUserCheckInCoinCode();

        //初始化商品券码redis数据
        initProductTicketRedis();
    }

    /**
     * 初始化菜单数据
     */
    public void initMenuRule() {
        if (otherConfig.getIsUpdateRule()) {
            System.out.println("initMenuRule running");

            try {
                // 加载SQL脚本
                Resource resource = resourceLoader.getResource("classpath:db/seed/cas_rule_seed.sql");

                casRuleMapper.truncateTableData();

                // 创建并配置ResourceDatabasePopulator来执行SQL脚本
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
                populator.execute(dataSource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("initMenuRule end");
        }
    }

    /**
     * 初始化管理员权限缓存
     */
    public void initAdminPerCache() {
        List<CasAdmin> adminList = casAdminMapper.selectAll();
        if (adminList != null && !adminList.isEmpty()) {
            for (CasAdmin admin : adminList) {
                adminPerAsync.saveAdminPermissionCache(admin.getId());
            }
        }
    }

    /**
     * 初始化用户打卡币获取redis标志
     */
    public void initUserCheckInCoinCode() {
        LocalDate date = LocalDateTime.now().toLocalDate();
        LocalDateTime startTime = LocalDateUtil.strToLDT(LocalDateUtil.getStartTime());
        LocalDateTime endTime = LocalDateUtil.strToLDT(LocalDateUtil.getEndTime());

        UserCoinLogParam userCoinLogParam = new UserCoinLogParam();
        userCoinLogParam.setTxnTypes(Arrays.asList(1, 6));
        userCoinLogParam.setStartTime(startTime);
        userCoinLogParam.setEndTime(endTime);
        List<CasUserCoinLog> coinLogList = casUserCoinLogMapper.selectLists(userCoinLogParam);
        if (coinLogList != null && !coinLogList.isEmpty()) {
            for (CasUserCoinLog coinLog : coinLogList) {
                if (coinLog.getCoinNum() < 1) {
                    continue;
                }

                if (coinLog.getCoinType().equals(1)) {
                    //2025-12-18需求调整成：一天只能获得一次健康币奖励
                    String coinRedisKey =
                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":health:" + coinLog.getTeamId() + ":10";
                    redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
                } else {
                    //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
                    String coinRedisKey =
                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":gold:" + 10;
                    redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
                }

//                if (coinLog.getTxnType().equals(1)) {
//                    if (coinLog.getCoinType().equals(1)) {
//                        String coinRedisKey =
//                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":health:"
//                                + coinLog.getTeamId() + ":1";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    } else {
////                        CasUserCheckInRecord checkInRecord = casUserCheckInRecordMapper.selectById(coinLog
// .getTxnId());
////                        String coinRedisKey =
////                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":gold" +
////                                        ":" + checkInRecord.getCheckInMethod();
//                        //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
//                        String coinRedisKey =
//                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":gold:" + 10;
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
//                } else {
//                    if (coinLog.getCoinType().equals(2)) {
////                        String coinRedisKey =
////                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":gold:"
// + 6;
//                        //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
//                        String coinRedisKey =
//                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":gold:" + 10;
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    } else {
//                        String coinRedisKey =
//                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() + ":health:"
//                                + coinLog.getTeamId() + ":2";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
//                }
            }
        }
    }
//    public void initUserCheckInCoinCode() {
//        LocalDate date = LocalDateTime.now().toLocalDate();
//        LocalDateTime startTime = LocalDateUtil.strToLDT(LocalDateUtil.getStartTime());
//        LocalDateTime endTime = LocalDateUtil.strToLDT(LocalDateUtil.getEndTime());
//
//        UserCoinLogParam userCoinLogParam = new UserCoinLogParam();
//        userCoinLogParam.setTxnType(1);
//        userCoinLogParam.setCoinType(2);
//        userCoinLogParam.setStartTime(startTime);
//        userCoinLogParam.setEndTime(endTime);
//        List<CasUserCoinLog> coinLogList = casUserCoinLogMapper.selectLists(userCoinLogParam);
//        if (coinLogList != null && !coinLogList.isEmpty()) {
//            for (CasUserCoinLog coinLog : coinLogList) {
//                CasUserCheckInRecord checkInRecord = casUserCheckInRecordMapper.selectById(coinLog.getTxnId());
//                String coinRedisKey = Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + coinLog.getUserId() +
//                ":gold" +
//                        ":" + checkInRecord.getCheckInMethod();
//                redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//            }
//        }
//
//        SearchParam searchParam = new SearchParam();
//        searchParam.setStartTime(startTime);
//        searchParam.setEndTime(endTime);
//        List<CasUserCheckInTeamRecord> checkInTeamRecordList = casUserCheckInTeamRecordMapper.selectLists
//        (searchParam);
//        if (checkInTeamRecordList != null && !checkInTeamRecordList.isEmpty()) {
//            for (CasUserCheckInTeamRecord checkInTeamRecord : checkInTeamRecordList) {
//                String coinRedisPrefix =
//                        Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + checkInTeamRecord.getUserId() +
//                        ":health:" + checkInTeamRecord.getTeamId() + ":";
//                if (checkInTeamRecord.getHealthCoin() > 0) {
//                    if (checkInTeamRecord.getObtainType() != 1) {
//                        String coinRedisKey = coinRedisPrefix + ":2";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
//                    if (checkInTeamRecord.getObtainType() != 2) {
//                        String coinRedisKey = coinRedisPrefix + ":1";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
//                }
//            }
//        }
//    }

    /**
     * 初始化商品券码redis数据
     */
    public void initProductTicketRedis() {
        ProductSearchParam searchParam = new ProductSearchParam();
        List<Product> products = productMapper.selectList(searchParam);
        if (products != null && !products.isEmpty()) {
            for (Product product : products) {
                String key = Constant.PRODUCT_TICKET_LIST + product.getId();
                redisUtil.del(key);
                List<ProductTicket> productTickets = productTicketMapper.selectByProductId(product.getId(), 1);
                if (productTickets != null && !productTickets.isEmpty()) {
                    List<String> ticketList = productTickets.stream().map(ProductTicket::getTicket).toList();
                    redisUtil.rightPushAll(key, ticketList);
                }
            }
        }
    }
}
