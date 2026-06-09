package com.zemcho.ddql.service.app.impl;

import com.zemcho.ddql.common.Result;
import com.zemcho.ddql.common.dto.AppJwtData;
import com.zemcho.ddql.common.param.SearchParam;
import com.zemcho.ddql.config.jwt.JWTUtil;
import com.zemcho.ddql.config.other.OtherConfig;
import com.zemcho.ddql.controller.app.param.AppAuthParam;
import com.zemcho.ddql.controller.app.param.AppVersionUpdateParam;
import com.zemcho.ddql.controller.wechat.shop.param.WechatShopParam;
import com.zemcho.ddql.entity.app.AppVersion;
import com.zemcho.ddql.entity.checkInSettings.CheckInPlace;
import com.zemcho.ddql.entity.checkInSettings.CheckInSettings;
import com.zemcho.ddql.entity.checkInSettings.CheckInType;
import com.zemcho.ddql.entity.equipment.Equipment;
import com.zemcho.ddql.entity.equipment.EquipmentLog;
import com.zemcho.ddql.entity.equipment.EquipmentPoster;
import com.zemcho.ddql.mapper.appVersion.AppVersionMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInPlaceMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInSettingsMapper;
import com.zemcho.ddql.mapper.checkInSettings.CheckInTypeMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentLogMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentMapper;
import com.zemcho.ddql.mapper.equipment.EquipmentPosterMapper;
import com.zemcho.ddql.service.app.AppService;
import com.zemcho.ddql.service.wechat.index.WechatService;
import com.zemcho.ddql.service.wechat.shop.WechatShopService;
import com.zemcho.ddql.util.Constant;
import com.zemcho.ddql.util.LocalDateUtil;
import com.zemcho.ddql.util.decode.Md5Util;
import com.zemcho.ddql.util.redis.RedisUtil;
import com.zemcho.ddql.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class IAppService implements AppService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentPosterMapper equipmentPosterMapper;

    @Autowired
    private EquipmentLogMapper equipmentLogMapper;

    @Autowired
    private CheckInPlaceMapper checkInPlaceMapper;

    @Autowired
    private CheckInTypeMapper checkInTypeMapper;

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Autowired
    private CheckInSettingsMapper checkInSettingsMapper;

    @Autowired
    OtherConfig otherConfig;

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

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private WechatShopService wechatShopService;

    @Autowired
    WechatService wechatService;

    /**
     * app端获取AccessToken
     *
     * @param param
     * @return
     */
    @Override
    public Result getAccessToken(AppAuthParam param) {
        Long singTime = param.getSingTime();
        Long nowTime = LocalDateUtil.getSecondsByTime(LocalDateTime.now());
        if (singTime + otherConfig.getAppSignExpires() < nowTime) {
            return Result.error("签名已过期");
        }

        String sign = param.getSign();
        if (!sign.equals(buildSign(param))) {
            return Result.error("签名错误");
        }

        String serialNumber = param.getSerialNumber();

        //判断调用频率是否已达到上限
        if (!checkAccessTokenRequestNumLimit(serialNumber)) {
            return Result.error("获取AccessToken接口调用频率已达到上限");
        }

        Equipment equipmentInfo = equipmentMapper.selectBySerialNumber(serialNumber);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }
        if (!equipmentInfo.getEnableStatus().equals(0)) {
            return Result.error("设备未启用");
        }

        //上次签发的access_token
        String lastAccessTokenCacheKey = Constant.APP_LAST_ACCESS_TOKEN_CACHE_PREFIX + serialNumber;
        if (redisUtil.hasKey(lastAccessTokenCacheKey)) {
            String lastAccessToken = (String) redisUtil.get(lastAccessTokenCacheKey);
            Object lastAccessTokenData = redisUtil.get(lastAccessToken);
            redisUtil.set(lastAccessToken, lastAccessTokenData, 5, TimeUnit.MINUTES);  //给个5分钟平滑过渡时间
        }

        //签发access_token
        Integer expiresIn = otherConfig.getAppAccessTokenExpiresIn();
        AppJwtData appJwtData = new AppJwtData();
        appJwtData.setId(equipmentInfo.getId());
        appJwtData.setSerialNumber(equipmentInfo.getSerialNumber());
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        String accessToken = JWTUtil.appSign(appJwtData, currentTimeMillis);
        redisUtil.set(accessToken, appJwtData, expiresIn, TimeUnit.SECONDS);
        redisUtil.set(lastAccessTokenCacheKey, accessToken, expiresIn, TimeUnit.SECONDS);

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentId", equipmentInfo.getId());
        result.put("accessToken", accessToken);
        result.put("expiresIn", expiresIn);

        return Result.success("SUCCESS", result);
    }

    /**
     * 生成签名
     *
     * @param param
     * @return
     */
    public String buildSign(AppAuthParam param) {
        String str = String.format("serialNumber=%s&singTime=%s%s", param.getSerialNumber(),
                param.getSingTime(), otherConfig.getAppSignSecret());
        return Md5Util.MD5(str);
    }

    /**
     * 验证调用AccessToken接口频率
     *
     * @param serialNumber
     * @return
     */
    public Boolean checkAccessTokenRequestNumLimit(String serialNumber) {
        String key = Constant.APP_REQUEST_NUM_LIMIT_CACHE_PREFIX + serialNumber;

        if (redisUtil.hasKey(key)) {
            Integer requestNum = (Integer) redisUtil.get(key);
            if (requestNum != null && requestNum >= otherConfig.getAppAccessTokenRequireNumLimit()) {
                return false;
            }

            redisUtil.inc(key, 1L);
        } else {
            redisUtil.set(key, 1, otherConfig.getAppAccessTokenRequireTimeLimit(), TimeUnit.HOURS);
        }

        return true;
    }

    /**
     * 获取设备海报列表
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getPosterLists(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        SearchParam param = new SearchParam();
        param.setSearchId(equipmentId);
        param.setSearchIntStatus(1);
        List<EquipmentPoster> lists = equipmentPosterMapper.selectLists(param);

        LocalDateTime now = LocalDateTime.now();

        Equipment equipmentUpdate = new Equipment();
        equipmentUpdate.setId(equipmentId);
        equipmentUpdate.setAppRequireTime(now);
        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            equipmentUpdate.setOnlineStatus(0);
        }
        equipmentMapper.update(equipmentUpdate);

        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            EquipmentLog equipmentLog = new EquipmentLog();
            equipmentLog.setEquipmentId(equipmentId);
            equipmentLog.setStatus(1);
            equipmentLog.setCreatedTime(now);
            equipmentLogMapper.insert(equipmentLog);
        }

        return Result.success("获取成功", lists);
    }

    /**
     * 获取设备详情
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getEquipmentInfo(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        CheckInPlace placeInfo = checkInPlaceMapper.selectById(equipmentInfo.getCheckInPlaceId());

        CheckInType placeTypeInfo = null;
        if (placeInfo != null) {
            placeTypeInfo = checkInTypeMapper.selectById(placeInfo.getCheckInTypeId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentInfo", equipmentInfo);
        result.put("placeInfo", placeInfo);
        result.put("placeTypeInfo", placeTypeInfo);

        LocalDateTime now = LocalDateTime.now();

        Equipment equipmentUpdate = new Equipment();
        equipmentUpdate.setId(equipmentId);
        equipmentUpdate.setAppRequireTime(now);
        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            equipmentUpdate.setOnlineStatus(0);
        }
        equipmentMapper.update(equipmentUpdate);

        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            EquipmentLog equipmentLog = new EquipmentLog();
            equipmentLog.setEquipmentId(equipmentId);
            equipmentLog.setStatus(1);
            equipmentLog.setCreatedTime(now);
            equipmentLogMapper.insert(equipmentLog);
        }

        return Result.success("获取成功", result);
    }

    /**
     * 获取最新版本信息
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result latestVersionInfo(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        AppVersion versionInfo = appVersionMapper.selectLatestVersion(equipmentInfo.getRelease());

        Map<String, Object> result = new HashMap<>();
        result.put("versionId", equipmentInfo.getVersionId());
        result.put("versionSerialNumber", equipmentInfo.getVersionSerialNumber());
        result.put("release", equipmentInfo.getRelease());
        result.put("latestVersionInfo", versionInfo);

        LocalDateTime now = LocalDateTime.now();

        Equipment equipmentUpdate = new Equipment();
        equipmentUpdate.setId(equipmentId);
        equipmentUpdate.setAppRequireTime(now);
        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            equipmentUpdate.setOnlineStatus(0);
        }
        equipmentMapper.update(equipmentUpdate);

        if (!equipmentInfo.getOnlineStatus().equals(0)) {
            EquipmentLog equipmentLog = new EquipmentLog();
            equipmentLog.setEquipmentId(equipmentId);
            equipmentLog.setStatus(1);
            equipmentLog.setCreatedTime(now);
            equipmentLogMapper.insert(equipmentLog);
        }

        return Result.success("获取成功", result);
    }

    /**
     * 更新设备版本信息
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result updateVersion(AppVersionUpdateParam param, String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        AppVersion versionInfo = appVersionMapper.selectById(param.getVersionId());
        if (versionInfo == null) {
            return Result.error("版本不存在");
        }

        Equipment equipmentUpdate = new Equipment();
        equipmentUpdate.setId(equipmentInfo.getId());
        equipmentUpdate.setVersionId(versionInfo.getId());
        equipmentUpdate.setVersionSerialNumber(versionInfo.getSerialNumber());
        equipmentUpdate.setRelease(versionInfo.getRelease());
        equipmentMapper.update(equipmentUpdate);


        return Result.success("操作成功");
    }

    /**
     * 获取签到配置信息
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getCheckInConfig(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        //判断小程序码是否已生成，未生成则生成对应的小程序码
        String scene = equipmentId + "_" + equipmentInfo.getCheckInPlaceId();
        String fileName = scene + "_" + Md5Util.MD5(equipmentInfo.getSerialNumber()) + ".png";
        String filePath = localFilePath + uploadFilePath + "/check_in_qr/";
        WechatUtil.generateAndSaveQrCode(scene, otherConfig.getAppCheckInQRPage(), 430,
                otherConfig.getAppCheckInQREnv(), filePath, fileName);

        CheckInSettings checkInSettings = checkInSettingsMapper.get();
        String checkInInstruction = "";
        if (checkInSettings != null) {
            checkInInstruction = checkInSettings.getCheckInInstruction();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("filePath", uploadFilePath + "/check_in_qr/" + fileName);
        result.put("checkInInstruction", checkInInstruction);

        return Result.success("获取成功", result);
    }

    /**
     * 获取打卡店铺列表
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result getShopLists(SearchParam param, String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Equipment equipmentInfo = equipmentMapper.selectById(equipmentId);
        if (equipmentInfo == null) {
            return Result.error("设备不存在");
        }

        CheckInPlace placeInfo = checkInPlaceMapper.selectById(equipmentInfo.getCheckInPlaceId());
        if (placeInfo == null) {
            return Result.error("场地不存在");
        }

        WechatShopParam shopParam = new WechatShopParam();
        shopParam.setLocation(placeInfo.getLocation());
        shopParam.setPageNum(param.getPageNum());
        shopParam.setPageSize(param.getPageSize());

        return wechatShopService.selectList(shopParam);
    }

    /**
     * 获取用户打卡信息
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getUserCheckInInfo(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        //从redis队列里获取打卡id
        Object value = redisUtil.leftPop(Constant.EQUIPMENT_CHECK_IN_RESULT_QUEUE_PREFIX + equipmentId);
        if (value == null) {
            return Result.success("暂没有打卡信息");
        }
        Integer checkInRecordId = Integer.parseInt(value.toString());

        SearchParam checkInParam = new SearchParam();
        checkInParam.setSearchId(checkInRecordId);

        return wechatService.userCheckInInfo(checkInParam);
    }
}
