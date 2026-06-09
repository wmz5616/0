package com.zemcho.ddql.service.wechat.index.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itextpdf.kernel.geom.PageSize;
import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.tgy_pay.MerchantConfig;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.wechat.WechatConfig;
import com.zemcho.ddql.config.wechat.WechatPayConfig;
import com.zemcho.ddql.controller.cas.param.UserCoinLogParam;
import com.zemcho.ddql.controller.checkInSettings.vo.CheckInPlaceVo;
import com.zemcho.ddql.controller.order.vo.ExpressOrderVo;
import com.zemcho.ddql.controller.team.param.TeamUserSearchParam;
import com.zemcho.ddql.controller.team.vo.TeamUserVo;
import com.zemcho.ddql.controller.wechat.index.dto.CheckInExportData;
import com.zemcho.ddql.controller.wechat.index.dto.DepartmentStat;
import com.zemcho.ddql.controller.wechat.index.excelhandle.CheckInRankExcelHandler;
import com.zemcho.ddql.controller.wechat.index.param.*;
import com.zemcho.ddql.controller.wechat.index.vo.*;
import com.zemcho.ddql.entity.cas.*;
import com.zemcho.ddql.entity.checkInSettings.CheckInPlace;
import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;
import com.zemcho.ddql.entity.checkInSettings.CheckInType;
import com.zemcho.ddql.entity.equipment.Equipment;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.entity.order.ExchangeOrderAddress;
import com.zemcho.ddql.entity.order.ExchangeOrderLog;
import com.zemcho.ddql.entity.order.ExchangeOrderRefundApply;
import com.zemcho.ddql.entity.personalCenter.DeliveryAddress;
import com.zemcho.ddql.entity.product.Product;
import com.zemcho.ddql.entity.product.ProductTicket;
import com.zemcho.ddql.entity.team.Team;
import com.zemcho.ddql.entity.team.TeamCheckInSettings;
import com.zemcho.ddql.entity.team.TeamDepartment;
import com.zemcho.ddql.entity.team.TeamFeedback;
import com.zemcho.ddql.entity.team.TeamUser;
import com.zemcho.ddql.mapper.cas.*;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInSettingsMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInTypeMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentMapper;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderAddressMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderLogMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderRefundApplyMapper;
import com.zemcho.ddql.mapper.personalCenter.DeliveryAddressMapper;
import com.zemcho.ddql.mapper.product.ProductCheckAdminMapper;
import com.zemcho.ddql.mapper.product.ProductMapper;
import com.zemcho.ddql.mapper.product.ProductTicketMapper;
import com.zemcho.ddql.mapper.team.TeamCheckInSettingsMapper;
import com.zemcho.ddql.mapper.team.TeamDepartmentMapper;
import com.zemcho.ddql.mapper.team.TeamFeedbackMapper;
import com.zemcho.ddql.mapper.team.TeamMapper;
import com.zemcho.ddql.mapper.team.TeamUserMapper;
import com.zemcho.ddql.service.personalCenter.RegionService;
import com.zemcho.ddql.service.wechat.index.WechatService;
import com.zemcho.ddql.service.wechat.notice.async.SendSubscribeMsgAsync;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.FormatCheckUtils;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.file.FileUploadUtil;
import com.zemcho.ddql.util.location.DistanceCalculator;
import com.zemcho.ddql.util.mail.MailUtil;
import com.zemcho.ddql.util.pdf.PdfUtil;
import com.zemcho.ddql.util.pdf.ThymeleafUtil;
import com.zemcho.ddql.util.redis.RedisLockUtil;
import com.zemcho.ddql.util.redis.RedisUtil;
import com.zemcho.ddql.util.tgy.SignUtil;
import com.zemcho.ddql.util.tgy.TgyPayUtil;
import com.zemcho.ddql.util.tgy.dto.WxJsPayCallBackResponse;
import com.zemcho.ddql.util.uuid.OrderNoUtil;
import com.zemcho.ddql.util.wechatpay.WechatPayUtil;
import com.zemcho.ddql.util.wechatpay.dto.InitiateBatchTransferRequestNew;
import com.zemcho.ddql.util.wechatpay.dto.InitiateBatchTransferResponseNew;
import com.zemcho.ddql.util.wechatpay.dto.TransferSceneReportInfoNew;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IWechatService implements WechatService {
    @Autowired
    CasUserSportRecordMapper casUserSportRecordMapper;

    @Autowired
    CasUserCheckInRecordMapper casUserCheckInRecordMapper;

    @Autowired
    CheckInPlaceMapper checkInPlaceMapper;

    @Autowired
    CasUserMapper casUserMapper;

    @Autowired
    TeamUserMapper teamUserMapper;

    @Autowired
    TeamMapper teamMapper;

    @Autowired
    TeamCheckInSettingsMapper teamCheckInSettingsMapper;

    @Autowired
    TeamDepartmentMapper teamDepartmentMapper;

    @Autowired
    TeamFeedbackMapper teamFeedbackMapper;

    @Autowired
    private CheckInSettingsMapper checkInSettingsMapper;

    @Autowired
    private CasUserCoinLogMapper casUserCoinLogMapper;

    @Autowired
    private CasUserCheckInTeamRecordMapper casUserCheckInTeamRecordMapper;

    @Autowired
    private CasUserWithdrawalMapper casUserWithdrawalMapper;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    ProductCheckAdminMapper productCheckAdminMapper;

    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private ExchangeOrderAddressMapper exchangeOrderAddressMapper;

    @Autowired
    private ExchangeOrderLogMapper exchangeOrderLogMapper;

    @Autowired
    private ExchangeOrderRefundApplyMapper exchangeOrderRefundApplyMapper;

    @Autowired
    private ExpressOrderMapper expressOrderMapper;

    @Autowired
    private CheckInTypeMapper checkInTypeMapper;

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
    private WechatConfig wechatConfig;

    @Autowired
    private TgyPayUtil tgyPayUtil;

    @Autowired
    private MerchantConfig merchantConfig;

    @Autowired
    private RegionService regionService;

    @Autowired
    ThymeleafUtil thymeleafUtil;

    @Autowired
    private SendSubscribeMsgAsync sendSubscribeMsgAsync;

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
     * 获取用户今日运动信息
     *
     * @param token
     * @return
     */
    @Override
    public Result getUserTodaySportInfo(String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        LocalDate date = LocalDate.now();

        CasUserSportRecord info = casUserSportRecordMapper.selectByUserIdAndDate(userId, date);

        return Result.success("获取成功", info);
    }

    /**
     * 更新用户今日步数
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result userTodayStepNumUpdate(StepNumUpdateParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer stepNum = param.getStepNum();
        if (stepNum < 0) {
            return Result.error("步数不能小于0");
        }

        LocalDate date = LocalDate.now();

        CasUserSportRecord info = casUserSportRecordMapper.selectByUserIdAndDate(userId, date);
        if (info != null) {
            if (stepNum < info.getStepNum()) {
                return Result.error("步数不能小于今日当前的步数");
            }
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }
        if (userInfo.getPhone() == null || "".equals(userInfo.getPhone())) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您暂未绑定手机号，请先绑定手机");
        }

        CheckInSettings checkInSettings = checkInSettingsMapper.get();

        //获取用户所属团队信息及团队打卡配置信息
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, 0);
        Map<Integer, Team> teamMap = new HashMap<>();
        List<TeamCheckInSettings> teamCheckInSettingsList = new ArrayList<>();
        if (teamUserList != null && !teamUserList.isEmpty()) {
            List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).toList();
            List<Team> teamList = teamMapper.selectByIds(teamIds, 0);
            if (teamList != null && !teamList.isEmpty()) {
                teamMap = teamList.stream().collect(Collectors.toMap(Team::getId, team -> team));
                teamIds = teamList.stream().map(Team::getId).toList();
                teamCheckInSettingsList = teamCheckInSettingsMapper.selectByTeamIds(teamIds);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        List<CasUserCoinLog> logList = new ArrayList<>();
        try {
            if (info != null) {
                CasUserSportRecord sportUpdate = new CasUserSportRecord();
                sportUpdate.setId(info.getId());
                sportUpdate.setStepNum(stepNum);
                casUserSportRecordMapper.update(sportUpdate);
            } else {
                CasUserSportRecord sportInsert = new CasUserSportRecord();
                sportInsert.setUserId(userId);
                sportInsert.setDate(date);
                sportInsert.setStepNum(stepNum);
                sportInsert.setCheckInTime(0);
                sportInsert.setCreateTime(LocalDateTime.now());
                casUserSportRecordMapper.insert(sportInsert);
            }

            // 判断步数是否已达标,达标则领取对应的金币和健康币
            Integer goldCoin = 0;
            // 今日如果领了金币则不可再领取
//            String coinRedisKey =
//                    Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 6;
            //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
            String coinRedisKey =
                    Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 10;
            if (!redisUtil.hasKey(coinRedisKey)) {
                if (checkInSettings != null && checkInSettings.getTargetSteps() != null
                        && checkInSettings.getTargetSteps() != 0 && stepNum >= checkInSettings.getTargetSteps()) {
                    goldCoin = checkInSettings.getStepsGoldCoin();
                    String remark = "步数达标" + checkInSettings.getTargetSteps() + "步";

                    CasUserCoinLog goldCoinLog = new CasUserCoinLog();
                    goldCoinLog.setTxnType(6);
                    goldCoinLog.setTxnId(0);
                    goldCoinLog.setCoinType(2);
                    goldCoinLog.setNumType(1);
                    goldCoinLog.setCoinNum(goldCoin);
                    goldCoinLog.setUserId(userId);
                    goldCoinLog.setPhone(userInfo.getPhone());
                    goldCoinLog.setNickName(userInfo.getNickname());
                    goldCoinLog.setTeamId(0);
                    goldCoinLog.setTeamName("");
                    goldCoinLog.setTeamType(0);
                    goldCoinLog.setRemark(remark);
                    goldCoinLog.setCreateTime(now);
                    logList.add(goldCoinLog);
                }
            }

            Integer healthCoin = 0;
            if (teamCheckInSettingsList != null && !teamCheckInSettingsList.isEmpty()) {
                for (TeamCheckInSettings teamCheckInSettings : teamCheckInSettingsList) {
                    Team teamInfo = teamMap.get(teamCheckInSettings.getTeamId());
                    Integer teamNowHealthyCoin = teamInfo.getHealthyCoin();

                    Integer teamHealthCoin = 0;
                    // 今日如果领了健康币则不可再领取
                    String coinRedisPrefix =
                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":health:" + teamInfo.getId() + ":";
                    //2025-12-18需求调整成：一天只能获得一次健康币奖励
//                    if (!redisUtil.hasKey(coinRedisPrefix + "2") &&
                    if (!redisUtil.hasKey(coinRedisPrefix + "10") &&
                            teamCheckInSettings.getStepsOpen() == 0 && teamCheckInSettings.getTargetSteps() <= stepNum
                            && teamNowHealthyCoin >= teamCheckInSettings.getStepsHealthyCoin()) {
                        teamNowHealthyCoin = teamNowHealthyCoin - teamCheckInSettings.getStepsHealthyCoin();
                        teamHealthCoin = teamHealthCoin + teamCheckInSettings.getStepsHealthyCoin();

                        String remark = "步数达标" + teamCheckInSettings.getTargetSteps() + "步";
                        CasUserCoinLog healthCoinLog = new CasUserCoinLog();
                        healthCoinLog.setTxnType(6);
                        healthCoinLog.setTxnId(0);
                        healthCoinLog.setCoinType(1);
                        healthCoinLog.setNumType(1);
                        healthCoinLog.setCoinNum(teamCheckInSettings.getStepsHealthyCoin());
                        healthCoinLog.setUserId(userId);
                        healthCoinLog.setPhone(userInfo.getPhone());
                        healthCoinLog.setNickName(userInfo.getNickname());
                        healthCoinLog.setTeamId(teamInfo.getId());
                        healthCoinLog.setTeamName(teamInfo.getName());
                        healthCoinLog.setTeamType(teamInfo.getType());
                        healthCoinLog.setRemark(remark);
                        healthCoinLog.setCreateTime(now);
                        logList.add(healthCoinLog);
                    }

                    if (teamHealthCoin > 0) {
                        //更新用户团体健康币信息
                        teamUserMapper.incCoin(userId, teamInfo.getId(), teamHealthCoin);

                        //团体健康币余额更新
                        teamMapper.decCoin(teamInfo.getId(), teamHealthCoin);

                        healthCoin = healthCoin + teamHealthCoin;
                    }
                }
            }

            if (goldCoin > 0 || healthCoin > 0) {
                casUserMapper.incCoin(userId, goldCoin, healthCoin);

                //添加用户金币、健康币变更记录
                if (!logList.isEmpty()) {
                    casUserCoinLogMapper.insertAll(logList);
                }
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

        //添加今日打卡币获取redis标志
        if (!logList.isEmpty()) {
            for (CasUserCoinLog coinLog : logList) {
                if (coinLog.getCoinNum() > 0) {
                    if (coinLog.getCoinType().equals(2)) {
//                        String coinRedisKey = Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 6;
                        //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
                        String coinRedisKey =
                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 10;
                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);

                        //发送订阅消息
                        Map<String, Object> msgData = new HashMap<>();
                        msgData.put("thing1", "今日步数达标奖励");
                        msgData.put("short_thing2", coinLog.getCoinNum() + "金币");
                        msgData.put("time3", LocalDateUtil.formatTime(coinLog.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                        sendSubscribeMsgAsync.asyncSendSubscribeMsg(userInfo.getOpenId(),
                                wechatConfig.getStepGoldTemplateId(), null, msgData);
                    } else {
//                        String coinRedisKey =
//                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":health:" + coinLog
//                                .getTeamId() + ":2";
                        //2025-12-18需求调整成：一天只能获得一次健康币奖励
                        String coinRedisKey =
                                Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":health:" + coinLog.getTeamId() + ":10";
                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
                    }
                }
            }
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    /**
     * 获取打卡场地列表
     *
     * @param param
     * @return
     */
    @Override
    public Result getPlaceLists(SearchParam param) {
        List<CheckInPlaceVo> list = checkInPlaceMapper.select(param);
        if (list != null && !list.isEmpty()) {
            // 用户经纬度
            String userLocation = param.getSearchStrField2();
            if (userLocation != null && !"".equals(userLocation)) {
                String[] split1 = userLocation.split(",");
                double lat1 = Double.parseDouble(split1[0]);
                double lon1 = Double.parseDouble(split1[1]);

                for (CheckInPlaceVo item : list) {
                    // 门店经纬度
                    String location = item.getLocation();
                    String[] split2 = location.split(",");
                    double lat2 = Double.parseDouble(split2[0]);
                    double lon2 = Double.parseDouble(split2[1]);
                    double distance = DistanceCalculator.getDistance(lat1, lon1, lat2, lon2);
                    item.setDistance(distance);
                }

                //根据距离排序
                list = list.stream()
                        .sorted(Comparator.comparing(CheckInPlaceVo::getDistance))
                        .toList();
            }
        }

        return Result.success("获取成功", list);
    }

    /**
     * 获取打卡场地信息
     *
     * @param param
     * @return
     */
    @Override
    public Result getPlaceInfo(SearchParam param) {
        Integer placeId = param.getSearchId();
        if (placeId == null) {
            return Result.error("参数异常");
        }

        CheckInPlace info = checkInPlaceMapper.selectById(placeId);
        if (info == null) {
            return Result.error("场地不存在");
        }

        CheckInPlaceVo placeVo = new CheckInPlaceVo();
        BeanUtils.copyProperties(info, placeVo);

        CheckInType placeTypeInfo = checkInTypeMapper.selectById(info.getCheckInTypeId());
        if (placeTypeInfo != null) {
            placeVo.setCheckInTypeName(placeTypeInfo.getName());
            placeVo.setCheckInTypeImages(placeTypeInfo.getImages());
        }

        // 用户经纬度
        String userLocation = param.getSearchStrField2();
        if (userLocation != null && !"".equals(userLocation)) {
            String[] split1 = userLocation.split(",");
            double lat1 = Double.parseDouble(split1[0]);
            double lon1 = Double.parseDouble(split1[1]);

            // 门店经纬度
            String location = placeVo.getLocation();
            String[] split2 = location.split(",");
            double lat2 = Double.parseDouble(split2[0]);
            double lon2 = Double.parseDouble(split2[1]);
            double distance = DistanceCalculator.getDistance(lat1, lon1, lat2, lon2);
            placeVo.setDistance(distance);
        }

        return Result.success("获取成功", placeVo);
    }

    /**
     * 获取设备信息
     *
     * @param param
     * @return
     */
    @Override
    public Result getEquipmentInfo(SearchParam param) {
        Integer equipmentId = param.getSearchId();
        if (equipmentId == null) {
            return Result.error("参数异常");
        }

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);

        return Result.success("获取成功", equipmentInfo);
    }

    /**
     * 获取打卡配置信息
     *
     * @return
     */
    @Override
    public Result getCheckInSetting() {
        CheckInSettings checkInSettings = checkInSettingsMapper.get();

        if (checkInSettings != null) {
            List<String> withdrawalPictureList = new ArrayList<>();
            if (checkInSettings.getWithdrawalPicture() != null && !checkInSettings.getWithdrawalPicture().isEmpty()) {
                withdrawalPictureList = JSON.parseArray(checkInSettings.getWithdrawalPicture(), String.class);
            }
            checkInSettings.setWithdrawalPictureList(withdrawalPictureList);
            checkInSettings.setWithdrawalPicture(null);
        }

        return Result.success("获取成功", checkInSettings);
    }

    /**
     * 用户打卡
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result userCheckIn(CheckInParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer placeId = param.getPlaceId();
        CheckInPlace placeInfo = checkInPlaceMapper.selectById(placeId);
        if (placeInfo == null) {
            return Result.error("打卡场地不存在");
        }

        Integer checkInMethod = param.getCheckInMethod();
        Integer equipmentId = param.getEquipmentId();
        if (!placeInfo.getCheckInMethod().equals(checkInMethod)) {
            return Result.error("打卡方式错误");
        }

        //用户经纬度
        String userLocation = param.getUserLocation();
        if (userLocation == null || "".equals(userLocation)) {
            return Result.error("用户位置不能为空");
        }
        String[] split1 = userLocation.split(",");
        double lat1 = Double.parseDouble(split1[0]);
        double lon1 = Double.parseDouble(split1[1]);

        //场地经纬度
        String location = placeInfo.getLocation();
        String[] split2 = location.split(",");
        double lat2 = Double.parseDouble(split2[0]);
        double lon2 = Double.parseDouble(split2[1]);
        double distance = DistanceCalculator.getDistance(lat1, lon1, lat2, lon2);
        if (distance * 1000 > placeInfo.getCheckInDistance()) {
            return Result.error("超出打卡范围，目前您的距离是" + distance + "km");
        }

        if (checkInMethod != 1) {
            if (equipmentId == null || equipmentId == 0) {
                return Result.error("设备ID不能为空");
            }

            Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
            if (equipmentInfo == null) {
                return Result.error("设备不存在");
            }
            if (!equipmentInfo.getCheckInPlaceId().equals(placeId)) {
                return Result.error("设备不属于该打卡场地");
            }
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        // 判断是否存在打卡中的记录，存在则本次为离场打卡、否则为入场打卡
        CasUserCheckInRecord userCheckInRecord = casUserCheckInRecordMapper.selectByUserIdAndStatus(userId, 1);

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }
        if (userInfo.getPhone() == null || "".equals(userInfo.getPhone())) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您暂未绑定手机号，请先绑定手机");
        }

        //获取用户所属团队信息及团队打卡配置信息
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, 0);
        if (teamUserList == null || teamUserList.isEmpty()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您暂未加入团队，不可打卡");
        }
        List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).toList();
        List<Team> teamList = teamMapper.selectByIds(teamIds, 0);
        if (teamList == null || teamList.isEmpty()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您所属的团队已禁用");
        }
        Map<Integer, Team> teamMap = teamList.stream().collect(Collectors.toMap(Team::getId, team -> team));
        teamIds = teamList.stream().map(Team::getId).toList();
        List<TeamCheckInSettings> teamCheckInSettingsList = teamCheckInSettingsMapper.selectByTeamIds(teamIds);
        if (teamCheckInSettingsList == null || teamCheckInSettingsList.isEmpty()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您所属的团队暂未设置打卡信息");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();

        //离场打卡
        CasUserSportRecord userSportRecord = null;
        CheckInSettings checkInSettings = null;
        if (userCheckInRecord != null) {
            userSportRecord = casUserSportRecordMapper.selectByUserIdAndDate(userId, date);
            checkInSettings = checkInSettingsMapper.get();
        }

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer checkInRecordId;
        List<CasUserCoinLog> logList = new ArrayList<>();
        List<CasUserCheckInTeamRecord> checkInTeamRecordList = new ArrayList<>();
        Integer goldCoin = 0;
        Integer totalCheckInTime = 0;
        try {
            if (userCheckInRecord == null) { // 入场打卡
                CasUserCheckInRecord checkInRecord = new CasUserCheckInRecord();
                checkInRecord.setUserId(userId);
                checkInRecord.setNickname(userInfo.getNickname());
                checkInRecord.setPhone(userInfo.getPhone());
                checkInRecord.setAvatar(userInfo.getAvatar());
                checkInRecord.setPlaceId(placeId);
                checkInRecord.setPlaceName(placeInfo.getName());
                checkInRecord.setPlaceAddress(placeInfo.getAddress());
                checkInRecord.setLocation(placeInfo.getLocation());
                checkInRecord.setCheckInMethod(checkInMethod);
                checkInRecord.setEquipmentId(equipmentId);
                checkInRecord.setDate(date);
                checkInRecord.setStartTime(now);
                checkInRecord.setEndTime(null);
                checkInRecord.setCheckInTime(0);
                checkInRecord.setStatus(1);
                checkInRecord.setCreateTime(now);
                casUserCheckInRecordMapper.insert(checkInRecord);

                checkInRecordId = checkInRecord.getId();
            } else { // 离场打卡
                checkInRecordId = userCheckInRecord.getId();
                LocalDateTime startTime = userCheckInRecord.getStartTime();
                Long checkInTimeLong = LocalDateUtil.getSecondsByTime(now) - LocalDateUtil.getSecondsByTime(startTime);
                Integer checkInTime = checkInTimeLong.intValue();
                totalCheckInTime = checkInTime;

                //更新用户运动记录信息
                if (userSportRecord != null) {
                    totalCheckInTime = checkInTime + userSportRecord.getCheckInTime();
                    CasUserSportRecord sportUpdate = new CasUserSportRecord();
                    sportUpdate.setId(userSportRecord.getId());
                    sportUpdate.setCheckInTime(totalCheckInTime);
                    casUserSportRecordMapper.update(sportUpdate);
                } else {
                    CasUserSportRecord sportInsert = new CasUserSportRecord();
                    sportInsert.setUserId(userId);
                    sportInsert.setDate(date);
                    sportInsert.setStepNum(0);
                    sportInsert.setCheckInTime(totalCheckInTime);
                    sportInsert.setCreateTime(now);
                    casUserSportRecordMapper.insert(sportInsert);
                }

                //用户团队打卡信息更新
                Integer healthCoin = 0;
                for (TeamCheckInSettings teamCheckInSettings : teamCheckInSettingsList) {
                    Team teamInfo = teamMap.get(teamCheckInSettings.getTeamId());
                    Integer teamNowHealthyCoin = teamInfo.getHealthyCoin();

                    Integer teamHealthCoin = 0;
                    // 今日如果领了健康币则不可再领取
                    String coinRedisPrefix =
                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":health:" + teamInfo.getId() + ":";
                    Integer obtainType = 0;
                    //2025-12-18需求调整成：一天只能获得一次健康币奖励
//                    if (!redisUtil.hasKey(coinRedisPrefix + ":1")
                    if (!redisUtil.hasKey(coinRedisPrefix + "10")
                            && teamCheckInSettings.getScanCodeTime() != null
                            && teamCheckInSettings.getScanCodeTime() > 0
                            && totalCheckInTime >= (teamCheckInSettings.getScanCodeTime() * 60)
                            && teamNowHealthyCoin >= teamCheckInSettings.getScanCodeHealthyCoin()) {
                        teamNowHealthyCoin = teamNowHealthyCoin - teamCheckInSettings.getScanCodeHealthyCoin();
                        teamHealthCoin = teamCheckInSettings.getScanCodeHealthyCoin();
                        obtainType = 1;

                        String checkInMethodText = checkInMethod == 1 ? "场地打卡" : "扫码打卡";
                        String remark = checkInMethodText + " " + placeInfo.getName() + " 离场打卡";
                        CasUserCoinLog healthCoinLog = new CasUserCoinLog();
                        healthCoinLog.setTxnType(1);
                        healthCoinLog.setTxnId(checkInRecordId);
                        healthCoinLog.setCoinType(1);
                        healthCoinLog.setNumType(1);
                        healthCoinLog.setCoinNum(teamCheckInSettings.getScanCodeHealthyCoin());
                        healthCoinLog.setUserId(userId);
                        healthCoinLog.setPhone(userInfo.getPhone());
                        healthCoinLog.setNickName(userInfo.getNickname());
                        healthCoinLog.setTeamId(teamInfo.getId());
                        healthCoinLog.setTeamName(teamInfo.getName());
                        healthCoinLog.setTeamType(teamInfo.getType());
                        healthCoinLog.setRemark(remark);
                        healthCoinLog.setCreateTime(now);
                        logList.add(healthCoinLog);
                    }
//                    if (!redisUtil.hasKey(coinRedisPrefix + ":2") &&
//                            teamCheckInSettings.getStepsOpen() == 0 && userSportRecord != null &&
//                            teamCheckInSettings.getTargetSteps() <= userSportRecord.getStepNum()
//                            && teamNowHealthyCoin >= teamCheckInSettings.getStepsHealthyCoin()) {
//                        teamNowHealthyCoin = teamNowHealthyCoin - teamCheckInSettings.getStepsHealthyCoin();
//                        teamHealthCoin = teamHealthCoin + teamCheckInSettings.getStepsHealthyCoin();
//                        obtainType = obtainType == 1 ? 3 : 2;
//
//                        String checkInMethodText = checkInMethod == 1 ? "距离打卡" : "扫码打卡";
//                        String remark = checkInMethodText + " 达到步数" + teamCheckInSettings.getTargetSteps() + "步";
//                        CasUserCoinLog healthCoinLog = new CasUserCoinLog();
//                        healthCoinLog.setTxnType(1);
//                        healthCoinLog.setTxnId(checkInRecordId);
//                        healthCoinLog.setCoinType(1);
//                        healthCoinLog.setNumType(1);
//                        healthCoinLog.setCoinNum(teamCheckInSettings.getStepsHealthyCoin());
//                        healthCoinLog.setUserId(userId);
//                        healthCoinLog.setPhone(userInfo.getPhone());
//                        healthCoinLog.setNickName(userInfo.getNickname());
//                        healthCoinLog.setTeamId(teamInfo.getId());
//                        healthCoinLog.setTeamName(teamInfo.getName());
//                        healthCoinLog.setTeamType(teamInfo.getType());
//                        healthCoinLog.setRemark(remark);
//                        healthCoinLog.setCreateTime(now);
//                        logList.add(healthCoinLog);
//                    }

                    if (teamHealthCoin > 0) {
                        //更新用户团体健康币信息
                        teamUserMapper.incCoin(userId, teamInfo.getId(), teamHealthCoin);

                        //团体健康币余额更新
                        teamMapper.decCoin(teamInfo.getId(), teamHealthCoin);
                    }

                    CasUserCheckInTeamRecord checkInTeamRecord = new CasUserCheckInTeamRecord();
                    checkInTeamRecord.setRecordId(checkInRecordId);
                    checkInTeamRecord.setTeamId(teamInfo.getId());
                    checkInTeamRecord.setTeamName(teamInfo.getName());
                    checkInTeamRecord.setTeamType(teamInfo.getType());
                    checkInTeamRecord.setUserId(userId);
                    checkInTeamRecord.setPlaceId(placeId);
                    checkInTeamRecord.setCheckInMethod(checkInMethod);
                    checkInTeamRecord.setDate(date);
                    checkInTeamRecord.setObtainType(obtainType);
                    checkInTeamRecord.setHealthCoin(teamHealthCoin);
                    checkInTeamRecord.setRank(0);
                    checkInTeamRecord.setCreateTime(now);
                    checkInTeamRecordList.add(checkInTeamRecord);

                    healthCoin = healthCoin + teamHealthCoin;
                }
                if (!checkInTeamRecordList.isEmpty()) {
                    casUserCheckInTeamRecordMapper.insertAll(checkInTeamRecordList);
                }

                //更新用户金币、健康币信息
                if (checkInSettings != null && userSportRecord != null
                        && checkInSettings.getScanCodeGoldCoin() != null
                        && checkInSettings.getScanCodeGoldCoin() != 0) {
                    // 今日如果领了金币则不可再领取
//                    String coinRedisKey =
//                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + checkInMethod;
                    //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
                    String coinRedisKey =
                            Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 10;
                    if (!redisUtil.hasKey(coinRedisKey)) {
                        String remark = "";
                        if (checkInMethod == 1) {
//                            if (userSportRecord.getStepNum() >= checkInSettings.getTargetSteps()) {
//                                goldCoin = checkInSettings.getStepsGoldCoin();
//                                remark = "步数达标" + checkInSettings.getTargetSteps() + "步";
//                            }
                            goldCoin = checkInSettings.getScanCodeGoldCoin();
                            remark = "场地打卡";
                        } else {
                            goldCoin = checkInSettings.getScanCodeGoldCoin();
                            remark = "扫码打卡";
                        }

                        if (goldCoin > 0) {
                            CasUserCoinLog goldCoinLog = new CasUserCoinLog();
                            goldCoinLog.setTxnType(1);
                            goldCoinLog.setTxnId(checkInRecordId);
                            goldCoinLog.setCoinType(2);
                            goldCoinLog.setNumType(1);
                            goldCoinLog.setCoinNum(goldCoin);
                            goldCoinLog.setUserId(userId);
                            goldCoinLog.setPhone(userInfo.getPhone());
                            goldCoinLog.setNickName(userInfo.getNickname());
                            goldCoinLog.setTeamId(0);
                            goldCoinLog.setTeamName("");
                            goldCoinLog.setTeamType(0);
                            goldCoinLog.setRemark(remark);
                            goldCoinLog.setCreateTime(now);
                            logList.add(goldCoinLog);
                        }
                    }
                }
                if (goldCoin > 0 || healthCoin > 0) {
                    casUserMapper.incCoin(userId, goldCoin, healthCoin);

                    //添加用户金币、健康币变更记录
                    if (!logList.isEmpty()) {
                        casUserCoinLogMapper.insertAll(logList);
                    }
                }

                //更新用户打卡记录信息
                CasUserCheckInRecord checkInUpdate = new CasUserCheckInRecord();
                checkInUpdate.setId(userCheckInRecord.getId());
                checkInUpdate.setEndTime(now);
                checkInUpdate.setCheckInTime(checkInTime);
                checkInUpdate.setStatus(2);
                checkInUpdate.setHealthCoin(healthCoin);
                checkInUpdate.setGoldCoin(goldCoin);
                casUserCheckInRecordMapper.update(checkInUpdate);
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

        //添加今日打卡币获取redis标志
        if (goldCoin > 0) {
//            String coinRedisKey = Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + checkInMethod;
            //2025-11-25需求调整成：距离打卡、扫码打卡、步数打卡都能获得金币，但是只能一天只能获得一次金币奖励
            String coinRedisKey =
                    Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":gold:" + 10;
            redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);

            //发送订阅消息
            Map<String, Object> msgData = new HashMap<>();
            msgData.put("thing1", "今日团体打卡奖励");
            msgData.put("short_thing2", goldCoin + "金币");
            msgData.put("time3", LocalDateUtil.formatTime(now, "yyyy-MM-dd HH:mm:ss"));
            sendSubscribeMsgAsync.asyncSendSubscribeMsg(userInfo.getOpenId(),
                    wechatConfig.getStepGoldTemplateId(), null, msgData);
        }
        if (!checkInTeamRecordList.isEmpty()) {
            for (CasUserCheckInTeamRecord checkInTeamRecord : checkInTeamRecordList) {
                String coinRedisPrefix =
                        Constant.USER_CHECK_IN_COIN_PREFIX + date + ":" + userId + ":health:" + checkInTeamRecord.getTeamId() + ":";
                if (checkInTeamRecord.getHealthCoin() > 0) {
//                    if (checkInTeamRecord.getObtainType() != 1) {
//                        String coinRedisKey = coinRedisPrefix + ":2";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
//                    if (checkInTeamRecord.getObtainType() != 2) {
//                        String coinRedisKey = coinRedisPrefix + ":1";
//                        redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
//                    }
                    //2025-12-18需求调整成：一天只能获得一次健康币奖励
                    String coinRedisKey = coinRedisPrefix + "10";
                    redisUtil.set(coinRedisKey, true, 1, TimeUnit.DAYS);
                }
            }
        }

        // 更新用户团体打卡排名分数
        if (userCheckInRecord != null) {
            String userTeamRankPrefix = Constant.USER_CHECK_IN_RANK_PREFIX + date + ":";
            for (TeamCheckInSettings teamCheckInSettings : teamCheckInSettingsList) {
                redisUtil.zSetAdd(userTeamRankPrefix + teamCheckInSettings.getTeamId(), userId, totalCheckInTime);
            }
        }

        //扫码打卡时添加到设备扫码打卡队列里，目前先用redis处理，暂时不维护多一个mq队列
        if (checkInMethod == 0) {
            redisUtil.rightPush(Constant.EQUIPMENT_CHECK_IN_RESULT_QUEUE_PREFIX + equipmentId, checkInRecordId);
        }

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功", checkInRecordId);
    }

    /**
     * 获取用户打卡中的数据
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCheckInRunningInfo(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUserCheckInRecord userCheckInRecord = casUserCheckInRecordMapper.selectByUserIdAndStatus(userId, 1);

        return Result.success("操作成功", userCheckInRecord);
    }

    /**
     * 取消用户打卡
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCheckInCancel(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 10, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        CasUserCheckInRecord userCheckInRecord = casUserCheckInRecordMapper.selectByUserIdAndStatus(userId, 1);
        if (userCheckInRecord == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("当前没有打卡中的记录，无需取消");
        }

        //更新打卡状态
        CasUserCheckInRecord checkInUpdate = new CasUserCheckInRecord();
        checkInUpdate.setId(userCheckInRecord.getId());
        checkInUpdate.setStatus(3);
        casUserCheckInRecordMapper.update(checkInUpdate);

        // 解锁
        redisLockUtil.unlock(lockKey);

        return Result.success("操作成功");
    }

    /**
     * 获取用户打卡详情
     *
     * @param param
     * @return
     */
    @Override
    public Result userCheckInInfo(SearchParam param) {
        Integer recordId = param.getSearchId();
        if (recordId == null) {
            return Result.error("参数异常");
        }

        CasUserCheckInRecord record = casUserCheckInRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("打卡记录不存在");
        }

        SearchParam searchParam = new SearchParam();
        searchParam.setSearchId(recordId);
        List<CasUserCheckInTeamRecord> teamData = casUserCheckInTeamRecordMapper.selectLists(searchParam);
        if (teamData != null && !teamData.isEmpty()) {
            LocalDate date = LocalDateTime.now().toLocalDate();
            if (record.getDate().equals(date)) {
                String userTeamRankPrefix = Constant.USER_CHECK_IN_RANK_PREFIX + date + ":";
                for (CasUserCheckInTeamRecord teamItem : teamData) {
                    Long rank = redisUtil.zSetRank(userTeamRankPrefix + teamItem.getTeamId(), teamItem.getUserId());
                    if (rank != null) {
                        teamItem.setRank(rank.intValue() + 1);
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("recordInfo", record);
        result.put("teamData", teamData);

        return Result.success("获取成功", result);
    }

    /**
     * 获取用户打卡列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCheckInList(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        if (param.getIsReturnPermsData()) {
            param.setSearchField1(userId);
        } else {
            if (param.getSearchIntList() == null || param.getSearchIntList().isEmpty()) {
                return Result.error("请选择查看的团体");
            }
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<UserCheckInVo> list = casUserCheckInRecordMapper.selectList(param);
        if (list != null && !list.isEmpty()) {
            List<Integer> ids = list.stream().map(UserCheckInVo::getId).collect(Collectors.toList());

            SearchParam searchParam = new SearchParam();
            searchParam.setSearchIds(ids);
            if (!param.getIsReturnPermsData()) {
                searchParam.setSearchIntList(param.getSearchIntList());
            }
            List<CasUserCheckInTeamRecord> teamData = casUserCheckInTeamRecordMapper.selectLists(searchParam);
            Map<Integer, List<CasUserCheckInTeamRecord>> teamDataMap = new HashMap<>();
            if (teamData != null && !teamData.isEmpty()) {
                LocalDate date = LocalDateTime.now().toLocalDate();
                String userTeamRankPrefix = Constant.USER_CHECK_IN_RANK_PREFIX + date + ":";
                for (CasUserCheckInTeamRecord teamItem : teamData) {
                    if (teamItem.getDate().equals(date)) {
                        Long rank = redisUtil.zSetRank(userTeamRankPrefix + teamItem.getTeamId(), teamItem.getUserId());
                        if (rank != null) {
                            teamItem.setRank(rank.intValue() + 1);
                        }
                    }
                }
                teamDataMap = teamData.stream().collect(Collectors.groupingBy(CasUserCheckInTeamRecord::getRecordId));
            }

            for (UserCheckInVo item : list) {
                item.setTeamData(teamDataMap.get(item.getId()));
            }
        }
        PageInfo<UserCheckInVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取用户打卡统计信息
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCheckInCount(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }
        if (param.getIsReturnPermsData()) {
            param.setSearchField1(userId);
        } else {
            if (param.getSearchIntList() == null || param.getSearchIntList().isEmpty()) {
                return Result.error("请选择查看的团体");
            }
        }

        UserCheckInCountVo data = casUserCheckInRecordMapper.selectCount(param);

        return Result.success("获取成功", data);
    }

    /**
     * 用户打卡数据导出到邮箱
     *
     * @param param
     * @param token
     * @param request
     * @return
     */
    @Override
    public Result userCheckInExportToEmail(SearchParam param, String token, HttpServletRequest request) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        String teamName = "";
        if (param.getIsReturnPermsData()) {
            param.setSearchField1(userId);
        } else {
            if (param.getSearchIntList() == null || param.getSearchIntList().isEmpty()) {
                return Result.error("请选择查看的团体");
            }
            List<Team> teams = teamMapper.selectByIds(param.getSearchIntList(), null);
            if (teams == null || teams.isEmpty()) {
                return Result.error("团队不存在");
            }
            List<String> teamNames = teams.stream().map(Team::getName).collect(Collectors.toList());
            teamName = String.join("、", teamNames);
        }

        String email = param.getSearchStrField3();
        if (email == null || email.isEmpty()) {
            return Result.error("请输入邮箱");
        }
        if (!FormatCheckUtils.isEmail(email)) {
            return Result.error("邮箱格式错误");
        }

        List<UserCheckInVo> list = casUserCheckInRecordMapper.selectList(param);

        // 生成pdf
        Map<String, Object> params = new HashMap<>();
        params.put("teamName", teamName);
        params.put("list", list);
        List<String> pageHtml = new ArrayList<>();
        String html = thymeleafUtil.getThymeleafTemHtml("CheckInExportToMailSendTmpl", params, request);
        pageHtml.add(html);
        String pdfFilePath = localFilePath + uploadFilePath + "/check_in_pdf/";
        String pdfFileName = "check_in_export_" + LocalDateUtil.getMilliByTime(LocalDateTime.now());
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
        helper.setSubject("打卡记录");
        helper.addAttachment("打卡记录.pdf", new FileSystemResource(new File(watermarkPdfPath)));
        Map<String, Object> EmailMap = new HashMap<>();
        EmailMap.put("teamName", teamName);
        MailUtil.send(helper, "CheckInExportEmailTmpl.ftl", EmailMap);

        //删除pdf文件
        FileUploadUtil.delFile(pdfPath);
        FileUploadUtil.delFile(watermarkPdfPath);

        return Result.success("操作成功");
    }

    /**
     * 用户提现
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result userWithdrawal(UserWithdrawalParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = param.getTeamId();
        TeamUser teamUser = teamUserMapper.selectByTeamIdAndUserId(teamId, userId, false);
        if (teamUser == null) {
            return Result.error("您无权选择该团体");
        }
        Team teamInfo = teamMapper.selectById(teamId);
        if (teamInfo == null) {
            return Result.error("团队不存在");
        }

        Integer amount = param.getAmount();
        if (amount < 1) {
            return Result.error("提现金额错误");
        }
        TeamCheckInSettings teamCheckInSettingsList = teamCheckInSettingsMapper.selectByTeamId(teamId);
        if (teamCheckInSettingsList == null) {
            return Result.error("该团体暂未设置提现配置，请联系管理员");
        }
        if (teamCheckInSettingsList.getLowestWithdrawalMoney() > amount) {
            return Result.error("提现金额不能低于" + teamCheckInSettingsList.getLowestWithdrawalMoney() + "元");
        }
        if (amount > teamUser.getHealthyCoin()) {
            return Result.error("提现金额不能大于健康币数据");
        }
//        if (amount > teamInfo.getHealthyCoin()) {
//            return Result.error("该团队健康币余额不足，请联系管理员");
//        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("用户不存在");
        }
        if (userInfo.getOpenId() == null || userInfo.getOpenId().isEmpty()) {
            // 解锁
            redisLockUtil.unlock(lockKey);
            return Result.error("您暂未绑定微信，请先绑定微信");
        }
//        if (!userInfo.getHasCertification().equals(1)) {
//            // 解锁
//            redisLockUtil.unlock(lockKey);
//            return Result.error("您暂未实名认证，请先实名认证");
//        }

        LocalDateTime now = LocalDateTime.now();

        // 开启事务
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);

        Integer withdrawalId;
        String state = "";
        InitiateBatchTransferResponseNew wxResponse;
        try {
            String outBillNo = OrderNoUtil.generateNo(userId);
            Integer transferAmount = amount * 100;

            //发起转账
            TransferSceneReportInfoNew transferSceneReportInfo = new TransferSceneReportInfoNew();
            transferSceneReportInfo.setInfoType("活动名称");
            transferSceneReportInfo.setInfoContent("活动奖励");
            TransferSceneReportInfoNew transferSceneReportInfo2 = new TransferSceneReportInfoNew();
            transferSceneReportInfo2.setInfoType("奖励说明");
            transferSceneReportInfo2.setInfoContent("健康币提现");
            InitiateBatchTransferRequestNew request = new InitiateBatchTransferRequestNew();
            request.setAppid(wechatPayConfig.getAppId());
            request.setOutBillNo(outBillNo);
            request.setTransferSceneId("1000");
            request.setOpenid(userInfo.getOpenId());
            request.setUserName(userInfo.getName());
            request.setTransferAmount(transferAmount);
            request.setTransferRemark("健康币提现");
//            request.setNotifyUrl("");
            request.setTransferSceneReportInfos(Arrays.asList(transferSceneReportInfo, transferSceneReportInfo2));
            wxResponse = wechatPayUtil.initiateBatchTransferNew(request);

            String wxTransactionNo = "";
            String wxResult = "";
            if (wxResponse != null) {
                wxTransactionNo = wxResponse.getTransferBillNo();
                state = wxResponse.getState();
                wxResult = JSON.toJSONString(wxResponse);
            }

            // 添加提现记录
            CasUserWithdrawal data = new CasUserWithdrawal();
            data.setUserId(userId);
            data.setNickname(userInfo.getNickname());
            data.setPhone(userInfo.getPhone());
            data.setTeamId(teamId);
            data.setTeamName(teamInfo.getName());
            data.setTeamType(teamInfo.getType());
            data.setAmount(amount);
            data.setOutBillNo(outBillNo);
            data.setWxTransactionNo(wxTransactionNo);
            data.setState(state);
            data.setWxResult(wxResult);
            data.setCreateTime(now);
            casUserWithdrawalMapper.insert(data);
            withdrawalId = data.getId();

            //如果提现申请成功，则更新用户健康币
            if (!state.equals("") && !state.equals("FAIL")) {
                //用户团体健康币更新
                teamUserMapper.decCoin(userId, teamId, amount);

                //用户健康币更新
                casUserMapper.decCoin(userId, null, amount);

                //团体健康币余额更新
//                teamMapper.decCoin(teamId, amount);

                //添加用户健康币变更记录
                List<CasUserCoinLog> logList = new ArrayList<>();
                CasUserCoinLog healthCoinLog = new CasUserCoinLog();
                healthCoinLog.setTxnType(4);
                healthCoinLog.setTxnId(withdrawalId);
                healthCoinLog.setCoinType(1);
                healthCoinLog.setNumType(2);
                healthCoinLog.setCoinNum(amount);
                healthCoinLog.setUserId(userId);
                healthCoinLog.setPhone(userInfo.getPhone());
                healthCoinLog.setNickName(userInfo.getNickname());
                healthCoinLog.setTeamId(teamInfo.getId());
                healthCoinLog.setTeamName(teamInfo.getName());
                healthCoinLog.setTeamType(teamInfo.getType());
                healthCoinLog.setRemark("健康币提现" + amount + "元");
                healthCoinLog.setCreateTime(now);
                logList.add(healthCoinLog);
                casUserCoinLogMapper.insertAll(logList);
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

        Map<String, Object> result = new HashMap<>();
        result.put("withdrawalId", withdrawalId);
        result.put("state", state);
        result.put("wxResponse", wxResponse);

        return Result.success("操作成功", result);
    }

    /**
     * 获取用户提现详情
     *
     * @param param
     * @return
     */
    @Override
    public Result userWithdrawalInfo(SearchParam param) {
        Integer id = param.getSearchId();
        if (id == null) {
            return Result.error("参数异常");
        }

        CasUserWithdrawal info = casUserWithdrawalMapper.selectById(id);

        return Result.success("获取成功", info);
    }

    /**
     * 用户提现数据导出到邮箱
     *
     * @param param
     * @param token
     * @param request
     * @return
     */
    @Override
    public Result userWithdrawalExportToEmail(UserCoinLogParam param, String token, HttpServletRequest request) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        String email = param.getEmail();
        if (email == null || email.isEmpty()) {
            return Result.error("请输入邮箱");
        }
        if (!FormatCheckUtils.isEmail(email)) {
            return Result.error("邮箱格式错误");
        }

        param.setTxnType(4);
        param.setCoinType(1);
        param.setUserId(userId);

        //只能查看自己团体下的数据
        List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, null);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.error("您无权查看该团体信息!");
        }
        List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toList());
        Integer searchTeamId = param.getTeamId();
        String teamName = "";
        if (searchTeamId != null) {
            if (!teamIds.contains(searchTeamId)) {
                return Result.error("您无权查看该团体信息");
            }

            Team teamInfo = teamMapper.selectById(searchTeamId);
            if (teamInfo == null) {
                return Result.error("该团队不存在");
            }
            teamName = teamInfo.getName();
        } else {
            param.setTeamIds(teamIds);
        }

        List<CasUserCoinLog> list = casUserCoinLogMapper.selectLists(param);

        // 生成pdf
        Map<String, Object> params = new HashMap<>();
        params.put("teamName", teamName);
        params.put("list", list);
        List<String> pageHtml = new ArrayList<>();
        String html = thymeleafUtil.getThymeleafTemHtml("WithdrawalExportToMailSendTmpl", params, request);
        pageHtml.add(html);
        String pdfFilePath = localFilePath + uploadFilePath + "/withdrawal_pdf/";
        String pdfFileName = "withdrawal_export_" + LocalDateUtil.getMilliByTime(LocalDateTime.now());
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
        helper.setSubject("提现记录");
        helper.addAttachment("提现记录.pdf", new FileSystemResource(new File(watermarkPdfPath)));
        Map<String, Object> EmailMap = new HashMap<>();
        EmailMap.put("teamName", teamName);
        MailUtil.send(helper, "WithdrawalExportEmailTmpl.ftl", EmailMap);

        //删除pdf文件
        FileUploadUtil.delFile(pdfPath);
        FileUploadUtil.delFile(watermarkPdfPath);

        return Result.success("操作成功");
    }

    /**
     * 获取币变更日志列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result coinLogLists(UserCoinLogParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        if (param.getCoinType() == null) {
            return Result.error("参数异常");
        }

        if (param.getOnlySelf()) {
            param.setUserId(userId);
        }

        //只能查看自己团体下的数据
        if (param.getCoinType().equals(1)) { //健康币记录才有团体id
            List<TeamUser> teamUserList = teamUserMapper.selectByUserIdAndStatus(userId, null);
            if (teamUserList == null || teamUserList.isEmpty()) {
                return Result.success("获取成功", new PageInfo<>());
            }
            List<Integer> teamIds = teamUserList.stream().map(TeamUser::getTeamId).collect(Collectors.toList());
            Integer searchTeamId = param.getTeamId();
            if (searchTeamId != null) {
                if (!teamIds.contains(searchTeamId)) {
                    return Result.error("您无权查看该团体信息");
                }
            } else {
                param.setTeamIds(teamIds);
            }
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<CasUserCoinLog> list = casUserCoinLogMapper.selectLists(param);
        PageInfo<CasUserCoinLog> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取用户打卡排行榜列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userCheckInRankLists(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = param.getSearchField1();
        if (teamId == null) {
            return Result.error("请选择团体");
        }
        Team teamInfo = teamMapper.selectById(teamId);
        if (teamInfo == null) {
            return Result.error("团体错误");
        }

        //获取团队下的所有成员
        TeamUserSearchParam searchParam = new TeamUserSearchParam();
        searchParam.setTeamId(teamId);
        if (param.getSearchField2() != null) {
            searchParam.setDepartmentId(param.getSearchField2());
        }
        List<TeamUserVo> teamUserList = teamUserMapper.selectList(searchParam);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>());
        }

        //统计团队下的所有成员打卡数据
        List<UserCheckInRankCountVo> userCheckInCountList = casUserCheckInTeamRecordMapper.selectUserCount(param);
        Map<Integer, UserCheckInRankCountVo> userCheckInCountMap = new HashMap<>();
        if (userCheckInCountList != null && !userCheckInCountList.isEmpty()) {
            userCheckInCountMap =
                    userCheckInCountList.stream().collect(Collectors.toMap(UserCheckInRankCountVo::getUserId, v -> v));
        }

        //统计团队下的所有成员步数领健康币数据
        UserCoinLogParam coinLogParam = new UserCoinLogParam();
        coinLogParam.setTeamId(teamId);
        coinLogParam.setCoinType(1);
        coinLogParam.setNumType(1);
        coinLogParam.setTxnType(6);
        coinLogParam.setStartTime(param.getStartTime());
        coinLogParam.setEndTime(param.getEndTime());
        List<UserCoinCountVo> coinCountList = casUserCoinLogMapper.selectUserCount(coinLogParam);
        Map<Integer, UserCoinCountVo> coinCountMap = new HashMap<>();
        if (coinCountList != null && !coinCountList.isEmpty()) {
            coinCountMap = coinCountList.stream().collect(Collectors.toMap(UserCoinCountVo::getUserId, v -> v));
        }

        List<UserCheckInRankVo> rankList = new ArrayList<>();
        for (TeamUserVo teamUser : teamUserList) {
            UserCheckInRankVo rankVo = new UserCheckInRankVo();

            // 设置用户基本信息
            rankVo.setUserId(teamUser.getUserId());
            rankVo.setNickName(teamUser.getNickName());
            rankVo.setAvatar(teamUser.getAvatar());
            rankVo.setUserName(teamUser.getUserName());
            rankVo.setUserPhone(teamUser.getUserPhone());
            rankVo.setType(teamUser.getType());
            rankVo.setJoinType(teamUser.getJoinType());
            rankVo.setStatus(teamUser.getStatus());

            // 设置打卡数据
            UserCheckInRankCountVo countVo = userCheckInCountMap.get(teamUser.getUserId());
            if (countVo != null) {
                rankVo.setHealthCoin(countVo.getHealthCoin());
                rankVo.setCheckInTime(countVo.getCheckInTime());
                rankVo.setCheckInNum(countVo.getCheckInNum());
                rankVo.setPlaceIds(countVo.getPlaceIds());
            } else {
                // 如果没有打卡数据，设为0
                rankVo.setHealthCoin(0);
                rankVo.setCheckInTime(0);
                rankVo.setCheckInNum(0);
            }

            // 获取步数领健康币数据
            UserCoinCountVo coinCountVo = coinCountMap.get(teamUser.getUserId());
            if (coinCountVo != null) {
                rankVo.setHealthCoin(rankVo.getHealthCoin() + coinCountVo.getCoinNum());
                rankVo.setHasStepCoin(true);
            }

            rankList.add(rankVo);
        }

        // 根据健康币和打卡时长进行排序
        rankList.sort((o1, o2) -> {
            // 首先按健康币降序排列
            int coinCompare = Integer.compare(o2.getHealthCoin(), o1.getHealthCoin());
            if (coinCompare != 0) {
                return coinCompare;
            }
            // 健康币相同的情况下，按打卡时长降序排列
            return Integer.compare(o2.getCheckInTime(), o1.getCheckInTime());
        });

        // 设置排名
        int rank = 1;
        int sameRankCount = 1;
        Integer previousCoin = null;
        Integer previousTime = null;
        UserCheckInRankVo selfRankData = null;
        for (int i = 0; i < rankList.size(); i++) {
            UserCheckInRankVo current = rankList.get(i);

            if (i == 0) {
                // 第一个用户排名为1
                current.setRank(rank);
                previousCoin = current.getHealthCoin();
                previousTime = current.getCheckInTime();
            } else {
                // 如果健康币和打卡时长都相同，则排名相同
                if (current.getHealthCoin().equals(previousCoin) &&
                        current.getCheckInTime().equals(previousTime)) {
                    current.setRank(rank);
                    sameRankCount++;
                } else {
                    // 不同则排名递增
                    rank += sameRankCount;
                    current.setRank(rank);
                    sameRankCount = 1;
                    previousCoin = current.getHealthCoin();
                    previousTime = current.getCheckInTime();
                }
            }

            if (current.getUserId().equals(userId)) {
                selfRankData = current;
            }
        }

        //分页处理
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();
        PageInfo<UserCheckInRankVo> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        Integer listTotal = rankList.size();
        rankList = rankList.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        if (!rankList.isEmpty()) {
            List<Integer> placeIds = rankList.stream()
                    .flatMap(vo -> vo.getPlaceIdList().stream())
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, CheckInPlaceVo> placeMap = new HashMap<>();
            if (!placeIds.isEmpty()) {
                List<CheckInPlaceVo> placeList = checkInPlaceMapper.selectByIds(placeIds);
                placeMap = placeList.stream().collect(Collectors.toMap(CheckInPlaceVo::getId, v -> v));
            }

            for (UserCheckInRankVo item : rankList) {
                List<CheckInPlaceVo> placeTemp = new ArrayList<>();
                Map<Integer, UserCheckInPlaceTypeStatVo> placeTypeStatMap = new HashMap<>();
                List<Integer> placeIdTemp = item.getPlaceIdList();
                List<Integer> addPlaceIdTemp = new ArrayList<>();
                if (placeIdTemp != null && !placeIdTemp.isEmpty()) {
                    for (Integer placeId : placeIdTemp) {
                        CheckInPlaceVo place = placeMap.get(placeId);
                        if (place != null) {
                            if (!addPlaceIdTemp.contains(placeId)) {
                                placeTemp.add(place);
                                addPlaceIdTemp.add(placeId);
                            }

                            Integer checkInTypeId = place.getCheckInTypeId();
                            UserCheckInPlaceTypeStatVo placeTypeStat;
                            if (!placeTypeStatMap.containsKey(checkInTypeId)) {
                                placeTypeStat = new UserCheckInPlaceTypeStatVo();
                                placeTypeStat.setCheckInTypeId(checkInTypeId);
                                placeTypeStat.setCheckInTypeName(place.getCheckInTypeName());
                                placeTypeStat.setCheckInTypeImages(place.getCheckInTypeImages());
                                placeTypeStat.setCheckInNum(1);
                            } else {
                                placeTypeStat = placeTypeStatMap.get(checkInTypeId);
                                placeTypeStat.setCheckInNum(placeTypeStat.getCheckInNum() + 1);
                            }
                            placeTypeStatMap.put(checkInTypeId, placeTypeStat);
                        }
                    }
                }
                item.setPlaceList(placeTemp);
                item.setPlaceTypeStatList(new ArrayList<>(placeTypeStatMap.values()));
            }
        }
        pageInfo.setTotal(listTotal);
        pageInfo.setList(rankList);

        Map<String, Object> result = new HashMap<>();
        result.put("checkInNumLimit", teamInfo.getCheckInNumLimit());
        result.put("selfRankData", selfRankData);
        result.put("pageInfo", pageInfo);

        return Result.success("获取成功", result);
    }

    /**
     * 导出用户打卡排行榜（月榜）
     *
     * @param param
     * @param token
     * @param response
     */
    @Override
    public void userCheckInRankExport(SearchParam param, String token, HttpServletResponse response) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            throw new IllegalArgumentException("token无效");
        }

        Integer teamId = param.getSearchField1();
        if (teamId == null) {
            throw new IllegalArgumentException("请选择团体");
        }

        // 判断是否导出意见反馈（searchField4: 1=导出，0/空=不导出）
        boolean exportFeedback = param.getSearchField4() != null && param.getSearchField4() == 1;

        // 获取团队信息
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new IllegalArgumentException("团体不存在");
        }

        // 校验用户是否属于该团队
        if (!teamUserMapper.ifExist(teamId, userId, false)) {
            throw new IllegalArgumentException("无权导出该团体的数据");
        }

        // 获取部门列表（判断是否为多部门）
        List<TeamDepartment> departmentList = teamDepartmentMapper.selectByTeamId(teamId);
        boolean isMultiDepartment = departmentList != null && departmentList.size() > 1;

        // 设置时间范围（默认本月，只支持月榜导出）
        LocalDateTime startDateTime = param.getStartTime();
        LocalDateTime endDateTime = param.getEndTime();
        
        if (startDateTime == null || endDateTime == null) {
            LocalDate now = LocalDate.now();
            startDateTime = now.withDayOfMonth(1).atStartOfDay();
            endDateTime = now.atTime(23, 59, 59);
        }

        // 更新param中的时间范围
        param.setStartTime(startDateTime);
        param.setEndTime(endDateTime);
        
        // 格式化为字符串用于导出显示
        String startTime = startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 统计数据
        CheckInExportData exportData = new CheckInExportData();
        exportData.setTeamName(team.getName());
        exportData.setStartTime(startTime);
        exportData.setEndTime(endTime);

        // 统计整体数据（含TOP3和运动类型）
        statOverallData(exportData, teamId, param);

        // 统计部门数据（多部门模式，使用已查询的数据在内存中分组）
        if (isMultiDepartment && exportData.getTeamUserList() != null) {
            statDepartmentData(exportData, departmentList);
        }

        // 获取意见反馈
        if (exportFeedback) {
            exportData.setFeedbackList(teamFeedbackMapper.selectByTeamId(teamId));
        }

        // 生成Excel
        CheckInRankExcelHandler.export(response, exportData, isMultiDepartment);
    }

    /**
     * 统计整体打卡数据
     */
    private void statOverallData(CheckInExportData exportData, Integer teamId, SearchParam param) {
        // 获取团队成员列表
        TeamUserSearchParam searchParam = new TeamUserSearchParam();
        searchParam.setTeamId(teamId);
        List<TeamUserVo> teamUserList = teamUserMapper.selectList(searchParam);
        exportData.setTotalMemberCount(teamUserList != null ? teamUserList.size() : 0);
        exportData.setTeamUserList(teamUserList);

        // 统计打卡数据
        List<UserCheckInRankCountVo> checkInCountList = casUserCheckInTeamRecordMapper.selectUserCount(param);
        
        int totalCheckInNum = 0;
        int totalHealthCoin = 0;
        int totalCheckInTime = 0;
        int activeMemberCount = 0;

        // 构建 userId -> countVo 映射，用于O(1)查找
        Map<Integer, UserCheckInRankCountVo> countVoMap = new HashMap<>();
        if (checkInCountList != null) {
            for (UserCheckInRankCountVo vo : checkInCountList) {
                totalCheckInNum += vo.getCheckInNum();
                totalHealthCoin += vo.getHealthCoin();
                totalCheckInTime += vo.getCheckInTime();
                if (vo.getCheckInNum() > 0) {
                    activeMemberCount++;
                }
                countVoMap.put(vo.getUserId(), vo);
            }
        }

        exportData.setTotalCheckInNum(totalCheckInNum);
        exportData.setTotalHealthCoin(totalHealthCoin);
        exportData.setTotalCheckInTime(totalCheckInTime);
        exportData.setActiveMemberCount(activeMemberCount);
        exportData.setCheckInCountList(checkInCountList);

        // 计算平均每人打卡次数
        if (exportData.getTotalMemberCount() > 0) {
            exportData.setAvgCheckInNum(Math.round((float) totalCheckInNum / exportData.getTotalMemberCount() * 10) / 10f);
        }

        // 计算团员活跃率
        if (exportData.getTotalMemberCount() > 0) {
            exportData.setActiveRate(Math.round((float) activeMemberCount / exportData.getTotalMemberCount() * 10000) / 100f);
        }

        // 获取TOP3用户
        List<UserCheckInRankVo> top3List = new ArrayList<>();
        if (teamUserList != null && !teamUserList.isEmpty()) {
            for (TeamUserVo user : teamUserList) {
                UserCheckInRankVo vo = new UserCheckInRankVo();
                vo.setUserId(user.getUserId());
                vo.setNickName(user.getNickName());
                vo.setUserName(user.getUserName());
                
                UserCheckInRankCountVo countVo = countVoMap.get(user.getUserId());
                if (countVo != null) {
                    vo.setCheckInNum(countVo.getCheckInNum());
                    vo.setHealthCoin(countVo.getHealthCoin());
                } else {
                    vo.setCheckInNum(0);
                    vo.setHealthCoin(0);
                }
                top3List.add(vo);
            }

            // 排序取TOP3（null-safe）
            top3List.sort((o1, o2) -> {
                int c1 = o1.getCheckInNum() != null ? o1.getCheckInNum() : 0;
                int c2 = o2.getCheckInNum() != null ? o2.getCheckInNum() : 0;
                int h1 = o1.getHealthCoin() != null ? o1.getHealthCoin() : 0;
                int h2 = o2.getHealthCoin() != null ? o2.getHealthCoin() : 0;
                int coinCompare = Integer.compare(h2, h1);
                return coinCompare != 0 ? coinCompare : Integer.compare(c2, c1);
            });
            
            exportData.setTop3List(top3List.size() > 3 ? top3List.subList(0, 3) : top3List);
        }

        // 统计运动类型
        statExerciseTypeData(exportData, checkInCountList);
    }

    /**
     * 统计部门数据（在内存中按部门分组，避免N+1查询）
     */
    private void statDepartmentData(CheckInExportData exportData, List<TeamDepartment> departmentList) {
        List<TeamUserVo> teamUserList = exportData.getTeamUserList();
        List<UserCheckInRankCountVo> checkInCountList = exportData.getCheckInCountList();

        // 构建 userId -> countVo 映射
        Map<Integer, UserCheckInRankCountVo> countVoMap = new HashMap<>();
        if (checkInCountList != null) {
            for (UserCheckInRankCountVo vo : checkInCountList) {
                countVoMap.put(vo.getUserId(), vo);
            }
        }

        // 按部门分组团队成员
        Map<Integer, List<TeamUserVo>> deptUserMap = new HashMap<>();
        if (teamUserList != null) {
            for (TeamUserVo user : teamUserList) {
                Integer deptId = user.getDepartmentId();
                if (deptId == null) {
                    continue;
                }
                deptUserMap.computeIfAbsent(deptId, k -> new ArrayList<>()).add(user);
            }
        }

        List<DepartmentStat> departmentStats = new ArrayList<>();
        for (TeamDepartment dept : departmentList) {
            DepartmentStat stat = new DepartmentStat();
            stat.setDepartmentId(dept.getId());
            stat.setDepartmentName(dept.getName());

            List<TeamUserVo> deptUsers = deptUserMap.getOrDefault(dept.getId(), Collections.emptyList());
            stat.setMemberCount(deptUsers.size());

            int deptCheckInNum = 0;
            int deptHealthCoin = 0;
            int activeCount = 0;

            for (TeamUserVo user : deptUsers) {
                UserCheckInRankCountVo countVo = countVoMap.get(user.getUserId());
                if (countVo != null) {
                    deptCheckInNum += countVo.getCheckInNum();
                    deptHealthCoin += countVo.getHealthCoin();
                    if (countVo.getCheckInNum() > 0) {
                        activeCount++;
                    }
                }
            }

            stat.setCheckInNum(deptCheckInNum);
            stat.setHealthCoin(deptHealthCoin);
            stat.setActiveMemberCount(activeCount);

            // 计算部门活跃率
            if (stat.getMemberCount() > 0) {
                stat.setActiveRate(Math.round((float) activeCount / stat.getMemberCount() * 10000) / 100f);
            }

            // 计算部门打卡贡献占比
            if (exportData.getTotalCheckInNum() > 0) {
                stat.setContributionRate(Math.round((float) deptCheckInNum / exportData.getTotalCheckInNum() * 10000) / 100f);
            }

            departmentStats.add(stat);
        }

        // 按活跃率排序
        departmentStats.sort((o1, o2) -> Double.compare(o2.getActiveRate(), o1.getActiveRate()));
        exportData.setDepartmentStats(departmentStats);
    }

    /**
     * 统计运动类型数据
     */
    private void statExerciseTypeData(CheckInExportData exportData, List<UserCheckInRankCountVo> checkInCountList) {
        Map<String, Integer> exerciseTypeMap = new LinkedHashMap<>();
        
        if (checkInCountList != null && !checkInCountList.isEmpty()) {
            // 收集所有打卡的placeId
            Set<Integer> allPlaceIds = new HashSet<>();
            for (UserCheckInRankCountVo vo : checkInCountList) {
                if (StringUtils.isNotEmpty(vo.getPlaceIds())) {
                    for (String idStr : vo.getPlaceIds().split(",")) {
                        try {
                            allPlaceIds.add(Integer.parseInt(idStr.trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            // 查询打卡场地信息，获取运动类型名称
            if (!allPlaceIds.isEmpty()) {
                List<CheckInPlaceVo> placeList = checkInPlaceMapper.selectByIds(new ArrayList<>(allPlaceIds));
                if (placeList != null) {
                    for (CheckInPlaceVo place : placeList) {
                        String typeName = StringUtils.isNotEmpty(place.getCheckInTypeName()) 
                                ? place.getCheckInTypeName() : "其他";
                        exerciseTypeMap.merge(typeName, 1, Integer::sum);
                    }
                }
            }
        }
        
        // 兜底：至少返回空数据
        if (exerciseTypeMap.isEmpty()) {
            exerciseTypeMap.put("暂无数据", 0);
        }

        exportData.setExerciseTypeMap(exerciseTypeMap);
    }

    /**
     * 获取用户提现排行榜列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result userWithdrawalRankLists(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer teamId = param.getSearchField2();
        if (teamId == null) {
            return Result.error("请选择团体");
        }

        //获取团队下的所有成员
        TeamUserSearchParam searchParam = new TeamUserSearchParam();
        searchParam.setTeamId(teamId);

        // 添加部门筛选
        if (param.getSearchField3() != null) {
            searchParam.setDepartmentId(param.getSearchField3());
        }

        List<TeamUserVo> teamUserList = teamUserMapper.selectList(searchParam);
        if (teamUserList == null || teamUserList.isEmpty()) {
            return Result.success("获取成功", new PageInfo<>());
        }

        //获取团队下的所有成员提现数据
        param.setSearchStrList(Arrays.asList("ACCEPTED", "PROCESSING", "WAIT_USER_CONFIRM", "TRANSFERING", "SUCCESS"));
        List<UserWithdrawalCountVo> userWithdrawalCountList = casUserWithdrawalMapper.selectUserCount(param);
        Map<Integer, UserWithdrawalCountVo> userWithdrawalCountMap = new HashMap<>();
        if (userWithdrawalCountList != null && !userWithdrawalCountList.isEmpty()) {
            userWithdrawalCountMap =
                    userWithdrawalCountList.stream().collect(Collectors.toMap(UserWithdrawalCountVo::getUserId,
                            v -> v));
        }

        List<UserWithdrawalRankVo> rankList = new ArrayList<>();
        for (TeamUserVo teamUser : teamUserList) {
            UserWithdrawalRankVo rankVo = new UserWithdrawalRankVo();
            rankVo.setUserId(teamUser.getUserId());
            rankVo.setNickName(teamUser.getNickName());
            rankVo.setAvatar(teamUser.getAvatar());
            rankVo.setUserName(teamUser.getUserName());
            rankVo.setUserPhone(teamUser.getUserPhone());
            rankVo.setType(teamUser.getType());
            rankVo.setJoinType(teamUser.getJoinType());
            rankVo.setStatus(teamUser.getStatus());

            // 提现金额获取
            UserWithdrawalCountVo countVo = userWithdrawalCountMap.get(teamUser.getUserId());
            if (countVo != null) {
                rankVo.setAmount(countVo.getAmount());
            } else {
                // 如果数据，设为0
                rankVo.setAmount(0);
            }

            rankList.add(rankVo);
        }

        // 根据提现金额进行排序
        rankList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));

        // 设置排名
        int rank = 1;
        int sameRankCount = 1;
        Integer previousAmount = null;
        UserWithdrawalRankVo selfRankData = null;
        for (int i = 0; i < rankList.size(); i++) {
            UserWithdrawalRankVo current = rankList.get(i);

            if (i == 0) {
                // 第一个用户排名为1
                current.setRank(rank);
                previousAmount = current.getAmount();
            } else {
                // 如果提现金额相同，则排名相同
                if (current.getAmount().equals(previousAmount)) {
                    current.setRank(rank);
                    sameRankCount++;
                } else {
                    // 不同则排名递增
                    rank += sameRankCount;
                    current.setRank(rank);
                    sameRankCount = 1;
                    previousAmount = current.getAmount();
                }
            }

            if (current.getUserId().equals(userId)) {
                selfRankData = current;
            }
        }

        //分页处理
        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();
        PageInfo<UserWithdrawalRankVo> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        Integer listTotal = rankList.size();
        rankList = rankList.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        pageInfo.setTotal(listTotal);
        pageInfo.setList(rankList);

        Map<String, Object> result = new HashMap<>();
        result.put("selfRankData", selfRankData);
        result.put("pageInfo", pageInfo);

        return Result.success("获取成功", result);
    }

    /**
     * 商品兑换
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Result productExchange(ProductExchangeParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        LocalDateTime now = LocalDateTime.now();

        Integer productId = param.getProductId();
        Product productInfo = productMapper.selectById(productId);
        if (productInfo == null) {
            return Result.error("商品不存在");
        }
        if (productInfo.getStatus() == 2) {
            return Result.error("商品未上架");
        }
        if (productInfo.getScheduledTime() == null || productInfo.getScheduledTime().isAfter(now)) {
            return Result.error("商品未上架!");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }

        Integer num = param.getNum();
        Integer amount = param.getAmount();
        Integer payWay = productInfo.getPayWay() == null ? 0 : productInfo.getPayWay();
        Integer cashAmount = getProductCashAmount(productInfo, num, payWay);
        Integer goldAmount = getProductGoldAmount(productInfo, num, payWay);
        if (productInfo.getStock() < num) { //目前时间不够暂时不使用redis预热
            return Result.error("商品库存不足");
        }
        if (!goldAmount.equals(amount)) {
            return Result.error("支付总金币错误");
        }
        if (goldAmount > userInfo.getGoldCoin()) {
            return Result.error("金币不足");
        }
        if (cashAmount < 0) {
            return Result.error("商品现金金额配置错误");
        }

        Integer addressId = param.getAddressId();
        DeliveryAddress addressInfo = null;
        LocalDateTime deadline = null;
        if (productInfo.getIsVirtual() == 0) {
            if (addressId == null || addressId <= 0) {
                return Result.error("请选择收货地址");
            }

            addressInfo = deliveryAddressMapper.selectById(addressId);
            if (addressInfo == null) {
                return Result.error("收货地址不存在");
            }
            if (!addressInfo.getUserId().equals(userId)) {
                return Result.error("您无权选择该收货地址");
            }
        } else {
            if (productInfo.getTimeLimit() <= 0) {
                return Result.error("该商品暂未设置有效期，请联系管理员");
            }
            deadline = now.plusDays(productInfo.getTimeLimit());
        }

        // 上锁
        String lockKey = Constant.USER_OPERATION_PREFIX + userId;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return Result.error("上次操作未处理完，请稍后再试");
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
            // 先创建订单记录，纯金币订单继续沿用原有“下单即完成”逻辑；
            // 组合支付/现金支付则先创建待支付订单，再通过通莞支付回调完成后续扣减。
            ExchangeOrder orderData = buildExchangeOrderData(orderNo, userId, userInfo, productInfo, num,
                    param.getRemark(), goldAmount, cashAmount, payWay, deadline, now);
            exchangeOrderMapper.insert(orderData);
            orderId = orderData.getId();

            if (productInfo.getIsVirtual() == 0) {
                ExchangeOrderAddress orderAddress = new ExchangeOrderAddress();
                orderAddress.setOrderId(orderId);
                orderAddress.setAddressId(addressId);
                orderAddress.setName(addressInfo.getName());
                orderAddress.setPhone(addressInfo.getPhone());
                orderAddress.setRegionId(addressInfo.getRegionId());
                orderAddress.setAddress(addressInfo.getAddress());
                orderAddress.setLocation(addressInfo.getLocation());
                orderAddress.setCreateTime(now);
                exchangeOrderAddressMapper.insert(orderAddress);
            }

            ExchangeOrderLog orderLog = new ExchangeOrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setOrderNo(orderData.getOrderNo());
            orderLog.setUserId(userId);
            orderLog.setUserName(userInfo.getNickname());
            orderLog.setHandle("创建订单");
            orderLog.setDetails(userInfo.getNickname() + "  " + userInfo.getPhone() + "创建订单");
            orderLog.setCreateTime(now);
            exchangeOrderLogMapper.insert(orderLog);

            // 纯金币订单保持原有逻辑，创建后立即扣减并进入已支付后的业务状态。
            if (payWay == 0) {
                Result completeResult = completeExchangeOrder(orderData, userInfo, productInfo, now, orderNo,
                        "金币支付完成", "金币支付完成");
                if (!completeResult.success()) {
                    throw new RuntimeException(completeResult.getMsg());
                }
            } else {
                // 组合支付和现金支付挂入超时未支付监控，到期后自动取消。
                addExchangeOrderPayMonitor(orderId);
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

        return Result.success("操作成功", orderId);
    }

    /**
     * 获取商品订单支付配置信息
     *
     * @param param  订单参数
     * @param token  用户token
     * @return 支付配置
     */
    @Override
    public Result productExchangeOrderPayConfig(ProductExchangeOrderPayParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getOrderId();
        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            return Result.error("您无权操作该订单");
        }
        if (!orderInfo.getStatus().equals(0)) {
            return Result.error("该订单不可支付");
        }
        if (orderInfo.getCashAmount() == null || orderInfo.getCashAmount() <= 0) {
            return Result.error("该订单无需现金支付");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        if (userInfo.getOpenId() == null || "".equals(userInfo.getOpenId())) {
            return Result.error("用户未绑定openId");
        }

        // 商品订单的现金支付走通莞支付，由前端拿到 payInfo 后直接拉起支付。
        Result payResult = tgyPayUtil.getWxJsPayConfig(userInfo.getOpenId(), orderInfo.getCashAmount(),
                orderInfo.getOrderNo(), "商品兑换", merchantConfig.getProductExchangePayNotifyUrl(), false);
        if (!payResult.success()) {
            return payResult;
        }

        Object data = payResult.getData();
        if (data instanceof Map) {
            Map resultMap = (Map) data;
            Object upOrderId = resultMap.get("upOrderId");
            if (upOrderId != null) {
                ExchangeOrder orderUpdate = new ExchangeOrder();
                orderUpdate.setId(orderId);
                orderUpdate.setUpOrderId(String.valueOf(upOrderId));
                exchangeOrderMapper.update(orderUpdate);
            }
        }

        return payResult;
    }

    /**
     * 商品订单支付回调
     *
     * @param callbackData 回调参数
     * @return 回调结果
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public String productExchangeOrderPayCallback(WxJsPayCallBackResponse callbackData) {
        if (callbackData == null) {
            return "fail";
        }
        if (!SignUtil.verifySign(merchantConfig.getKey(), callbackData.getSign(), callbackData)) {
            return "fail";
        }
        if (!"0".equals(callbackData.getState())) {
            return "fail";
        }

        String orderNo = callbackData.getLowOrderId();
        if (orderNo == null || "".equals(orderNo)) {
            return "fail";
        }

        ExchangeOrder orderInfo = exchangeOrderMapper.selectByOrderNo(orderNo);
        if (orderInfo == null) {
            return "fail";
        }
        if (!orderInfo.getStatus().equals(0)) {
            return "success";
        }

        Integer callbackAmount = getTgyPayAmount(callbackData.getPayMoney());
        if (!Objects.equals(callbackAmount, orderInfo.getCashAmount())) {
            return "fail";
        }

        String lockKey = Constant.ORDER_PAY_CALLBACK_LOCK_PREFIX + orderNo;
        Boolean lockFlag = redisLockUtil.tryLock(lockKey, 25, TimeUnit.SECONDS);
        if (!lockFlag) {
            return "fail";
        }

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            orderInfo = exchangeOrderMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                platformTransactionManager.rollback(transactionStatus);
                redisLockUtil.unlock(lockKey);
                return "fail";
            }
            if (!orderInfo.getStatus().equals(0)) {
                platformTransactionManager.commit(transactionStatus);
                redisLockUtil.unlock(lockKey);
                return "success";
            }

            CasUser userInfo = casUserMapper.selectById(orderInfo.getUserId());
            Product productInfo = productMapper.selectById(orderInfo.getProductId());
            if (userInfo == null || productInfo == null) {
                throw new RuntimeException("用户或商品不存在");
            }

            LocalDateTime payTime = LocalDateTime.now();
            if (callbackData.getPayTime() != null && !"".equals(callbackData.getPayTime())) {
                payTime = LocalDateUtil.strToLDT(callbackData.getPayTime());
            }
            orderInfo.setUpOrderId(callbackData.getUpOrderId());

            Result completeResult = completeExchangeOrder(orderInfo, userInfo, productInfo, payTime, orderNo,
                    "通莞支付成功", "通莞支付成功");
            if (!completeResult.success()) {
                // 若回调到达时金币不足或库存被占用，则取消订单并原路退回现金，避免影响用户资金。
                handleExchangeOrderPayFail(orderInfo, callbackData.getUpOrderId(), payTime, completeResult.getMsg());
            }

            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            platformTransactionManager.rollback(transactionStatus);
            redisLockUtil.unlock(lockKey);
            return "fail";
        }

        redisUtil.zSetRemove(Constant.EXCHANGE_ORDER_UNPAY_MONITOR, orderInfo.getId());
        redisLockUtil.unlock(lockKey);
        return "success";
    }

    private ExchangeOrder buildExchangeOrderData(String orderNo, Integer userId, CasUser userInfo, Product productInfo,
                                                 Integer num, String remark, Integer goldAmount, Integer cashAmount,
                                                 Integer payWay, LocalDateTime deadline, LocalDateTime now) {
        ExchangeOrder orderData = new ExchangeOrder();
        orderData.setOrderNo(orderNo);
        orderData.setUserId(userId);
        orderData.setPhone(userInfo.getPhone());
        orderData.setNickName(userInfo.getNickname());
        orderData.setProductId(productInfo.getId());
        orderData.setProductNo(productInfo.getProductNo());
        orderData.setCoverImage(productInfo.getCoverImage());
        orderData.setProductName(productInfo.getName());
        orderData.setSpecification(productInfo.getSpecification());
        orderData.setUnit(productInfo.getUnit());
        orderData.setIsVirtual(productInfo.getIsVirtual());
        orderData.setDeadline(deadline);
        orderData.setSingleAmount(productInfo.getExchangeAmount());
        orderData.setNum(num);
        orderData.setRemark(remark);
        orderData.setPayWay(payWay);
        orderData.setAmount(goldAmount);
        orderData.setCashAmount(cashAmount);
        orderData.setStatus(payWay == 0 ? getPaidExchangeOrderStatus(productInfo.getIsVirtual()) : 0);
        orderData.setExpressStatus(-2);
        orderData.setCreateTime(now);
        orderData.setPayTime(payWay == 0 ? now : null);
        return orderData;
    }

    /**
     * 完成商品兑换订单的实际扣减逻辑。
     */
    private Result completeExchangeOrder(ExchangeOrder orderInfo, CasUser userInfo, Product productInfo,
                                         LocalDateTime now, String orderNo, String handle, String detailPrefix) {
        if (productInfo.getStock() < orderInfo.getNum()) {
            return Result.error("商品库存不足");
        }
        if (orderInfo.getAmount() != null && orderInfo.getAmount() > userInfo.getGoldCoin()) {
            return Result.error("金币不足");
        }

        List<String> ticketList = null;
        try {
            // 支付成功后再真正扣减库存、兑换次数和金币，避免影响原有纯金币之外的流程。
            productMapper.updateStockOrExchangeNum(orderInfo.getProductId(), -orderInfo.getNum(), orderInfo.getNum());

            if (orderInfo.getAmount() != null && orderInfo.getAmount() > 0) {
                casUserMapper.decCoin(orderInfo.getUserId(), orderInfo.getAmount(), null);
                List<CasUserCoinLog> logList = new ArrayList<>();
                CasUserCoinLog goldCoinLog = new CasUserCoinLog();
                goldCoinLog.setTxnType(3);
                goldCoinLog.setTxnId(orderInfo.getId());
                goldCoinLog.setCoinType(2);
                goldCoinLog.setNumType(2);
                goldCoinLog.setCoinNum(orderInfo.getAmount());
                goldCoinLog.setUserId(orderInfo.getUserId());
                goldCoinLog.setPhone(orderInfo.getPhone());
                goldCoinLog.setNickName(orderInfo.getNickName());
                goldCoinLog.setTeamId(0);
                goldCoinLog.setTeamName("");
                goldCoinLog.setTeamType(0);
                goldCoinLog.setRemark("兑换" + productInfo.getName() + " x" + orderInfo.getNum());
                goldCoinLog.setCreateTime(now);
                logList.add(goldCoinLog);
                casUserCoinLogMapper.insertAll(logList);
            }

            if (!Objects.equals(productInfo.getIsVirtual(), 0)) {
                ticketList = getProductUnUsedTicket(orderInfo.getProductId(), orderInfo.getNum());
                if (ticketList == null) {
                    return Result.error("商品库存不足");
                }
                productTicketMapper.updateByProductIdAndTicket(orderInfo.getProductId(), ticketList, 2, orderInfo.getId());
            }

            ExchangeOrder orderUpdate = new ExchangeOrder();
            orderUpdate.setId(orderInfo.getId());
            orderUpdate.setStatus(getPaidExchangeOrderStatus(productInfo.getIsVirtual()));
            orderUpdate.setPayTime(now);
            orderUpdate.setUpOrderId(orderInfo.getUpOrderId());
            exchangeOrderMapper.update(orderUpdate);

            ExchangeOrderLog orderLog = new ExchangeOrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setOrderNo(orderNo);
            orderLog.setUserId(orderInfo.getUserId());
            orderLog.setUserName(orderInfo.getNickName());
            orderLog.setHandle(handle);
            orderLog.setDetails(detailPrefix + "，订单号：" + orderNo);
            orderLog.setCreateTime(now);
            exchangeOrderLogMapper.insert(orderLog);

            return Result.success("操作成功");
        } catch (Exception e) {
            if (ticketList != null && !ticketList.isEmpty()) {
                String key = Constant.PRODUCT_TICKET_LIST + orderInfo.getProductId();
                redisUtil.rightPushAll(key, ticketList);
            }
            throw e;
        }
    }

    private void handleExchangeOrderPayFail(ExchangeOrder orderInfo, String upOrderId, LocalDateTime now,
                                            String failReason) {
        ExchangeOrder orderUpdate = new ExchangeOrder();
        orderUpdate.setId(orderInfo.getId());
        orderUpdate.setStatus(8);
        orderUpdate.setUpOrderId(upOrderId);
        orderUpdate.setPayTime(now);
        orderUpdate.setRefundRemark(failReason);
        exchangeOrderMapper.update(orderUpdate);

        if (orderInfo.getCashAmount() != null && orderInfo.getCashAmount() > 0 && upOrderId != null && !"".equals(upOrderId)) {
            Result refundResult = tgyPayUtil.refund(upOrderId, orderInfo.getCashAmount());
            if (refundResult.success()) {
                ExchangeOrder refundUpdate = new ExchangeOrder();
                refundUpdate.setId(orderInfo.getId());
                refundUpdate.setRefundCashAmount(orderInfo.getCashAmount());
                refundUpdate.setRefundTime(now);
                refundUpdate.setRefundRemark(failReason);
                exchangeOrderMapper.update(refundUpdate);
            }
        }

        ExchangeOrderLog orderLog = new ExchangeOrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setOrderNo(orderInfo.getOrderNo());
        orderLog.setUserId(orderInfo.getUserId());
        orderLog.setUserName(orderInfo.getNickName());
        orderLog.setHandle("支付失败关闭");
        orderLog.setDetails("支付完成后订单校验失败，已关闭订单，原因：" + failReason);
        orderLog.setCreateTime(now);
        exchangeOrderLogMapper.insert(orderLog);
    }

    private Integer getPaidExchangeOrderStatus(Integer isVirtual) {
        return Objects.equals(isVirtual, 0) ? 2 : 1;
    }

    private Integer getProductGoldAmount(Product productInfo, Integer num, Integer payWay) {
        if (payWay == 2) {
            return 0;
        }
        return num * productInfo.getExchangeAmount();
    }

    private Integer getProductCashAmount(Product productInfo, Integer num, Integer payWay) {
        Long payAmount = productInfo.getPayAmount() == null ? 0L : productInfo.getPayAmount();
        if (payWay == 0) {
            return 0;
        }
        return Math.toIntExact(payAmount * num);
    }

    private void addExchangeOrderPayMonitor(Integer orderId) {
        Integer timeLimit = 30;
        long timestamp = LocalDateTime.now().plusMinutes(timeLimit)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        redisUtil.zSetAdd(Constant.EXCHANGE_ORDER_UNPAY_MONITOR, orderId, timestamp);
    }

    private Integer getTgyPayAmount(String payMoney) {
        if (payMoney == null || "".equals(payMoney)) {
            return null;
        }
        return new java.math.BigDecimal(payMoney).movePointRight(2).intValue();
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
     * 获取商品兑换列表
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    public Result productExchangeLists(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        param.setSearchField1(userId);

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<ExchangeOrder> list = exchangeOrderMapper.selectLists(param);
        PageInfo<ExchangeOrder> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商品兑换详情
     *
     * @param param
     * @return
     */
    @Override
    public Result productExchangeInfo(SearchParam param) {
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

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("refundApplyInfo", refundApplyInfo);
        result.put("orderAddress", orderAddress);
        result.put("expressOrder", expressOrder);
        result.put("ticketList", ticketList);

        return Result.success("获取成功", result);
    }

    /**
     * 商品兑换退货申请
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional
    public Result productExchangeRefund(ProductExchangeRefundParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        Integer orderId = param.getOrderId();
        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            return Result.error("您无权操作该订单");
        }
        if (!orderInfo.getIsVirtual().equals(0)) {
            return Result.error("该订单不支持退货");
        }
        if (orderInfo.getStatus() != 2 && orderInfo.getStatus() != 3 && orderInfo.getStatus() != 4) {
            return Result.error("该订单不可退货");
        }

        List<String> images = param.getImages();
        String refundImg = "";
        if (images != null && !images.isEmpty()) {
            refundImg = String.join(",", images);
        }

        LocalDateTime now = LocalDateTime.now();

        ExchangeOrderRefundApply refundApply = new ExchangeOrderRefundApply();
        refundApply.setOrderId(orderId);
        refundApply.setReason(param.getRefundReason());
        refundApply.setImages(refundImg);
        refundApply.setStatus(1);
        refundApply.setCreateTime(now);
        exchangeOrderRefundApplyMapper.insert(refundApply);

        ExchangeOrder orderUpdate = new ExchangeOrder();
        orderUpdate.setId(orderId);
        orderUpdate.setStatus(5);
        exchangeOrderMapper.update(orderUpdate);

        ExchangeOrderLog orderLog = new ExchangeOrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setOrderNo(orderInfo.getOrderNo());
        orderLog.setUserId(userId);
        orderLog.setUserName(orderInfo.getNickName());
        orderLog.setHandle("申请退货");
        orderLog.setDetails(orderInfo.getNickName() + "  " + orderInfo.getPhone() + "申请退货");
        orderLog.setCreateTime(now);
        exchangeOrderLogMapper.insert(orderLog);

        return Result.success("操作成功");
    }

    /**
     * 兑换券码核销
     *
     * @param param
     * @param token
     * @return
     */
    @Override
    @Transactional
    public Result productExchangeTicketCheck(SearchParam param, String token) {
        Integer userId = JWTUtil.getIntClaim(token, Constant.MINI_USER_ID);
        if (userId == null) {
            return new Result(10006, "token无效");
        }

        CasUser userInfo = casUserMapper.selectById(userId);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
//        if (userInfo.getAdminId() <= 0) {
//            return Result.error("您无权操作");
//        }
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

        LocalDateTime now = LocalDateTime.now();

        Integer orderId = ticketInfo.getOrderId();
        ExchangeOrder orderInfo = exchangeOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单信息异常");
        }
//        if (!orderInfo.getUserId().equals(userId)) {
//            return Result.error("您无权操作该订单");
//        }
        if (orderInfo.getStatus() != 1) {
            return Result.error("该订单不可核销");
        }
        if (orderInfo.getDeadline() == null || now.isAfter(orderInfo.getDeadline())) {
            return Result.error("该券码已失效");
        }

        productTicketMapper.updateStatusById(ticketId, 3);

        // 虚拟商品订单下的券码全部核销后，订单流转为已完成。
        List<ProductTicket> orderTicketList = productTicketMapper.selectByOrderId(orderId);
        boolean allChecked = orderTicketList != null && !orderTicketList.isEmpty() &&
                orderTicketList.stream().allMatch(item -> Objects.equals(item.getStatus(), 3));
        if (allChecked) {
            ExchangeOrder orderUpdate = new ExchangeOrder();
            orderUpdate.setId(orderId);
            orderUpdate.setStatus(4);
            exchangeOrderMapper.update(orderUpdate);
        }

        ExchangeOrderLog orderLog = new ExchangeOrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setOrderNo(orderInfo.getOrderNo());
        orderLog.setUserId(userId);
        orderLog.setUserName(orderInfo.getNickName());
        orderLog.setHandle("券码核销");
        orderLog.setDetails(orderInfo.getNickName() + "  " + orderInfo.getPhone() + "核销券码(" + ticketInfo.getTicket() +
                ")");
        orderLog.setCreateTime(now);
        exchangeOrderLogMapper.insert(orderLog);

        return Result.success("操作成功");
    }

    /**
     * 获取场所打卡记录列表
     *
     * @param param
     * @return
     */
    @Override
    public Result placeCheckInList(SearchParam param) {
        Integer placeId = param.getSearchField3();
        if (placeId == null) {
            return Result.error("参数异常");
        }

        //这里只筛选打卡成功的数据
        param.setSearchIntStatus(2);

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<UserCheckInVo> list = casUserCheckInRecordMapper.selectList(param);
        PageInfo<UserCheckInVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }
}
