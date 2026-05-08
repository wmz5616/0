package com.zemcho.guzhe.service.app.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.common.dto.AppJwtData;
import com.zemcho.guzhe.common.param.SearchParam;
import com.zemcho.guzhe.config.jwt.JWTUtil;
import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.config.wechat.WechatConfig;
import com.zemcho.guzhe.controller.app.param.AppAuthParam;
import com.zemcho.guzhe.controller.app.param.AppVersionUpdateParam;
import com.zemcho.guzhe.controller.app.param.ProductOrderQRParam;
import com.zemcho.guzhe.controller.app.vo.AppProductListVo;
import com.zemcho.guzhe.controller.app.vo.AppProductOrderVo;
import com.zemcho.guzhe.controller.app.vo.AppProductVo;
import com.zemcho.guzhe.controller.app.vo.AppShopListVo;
import com.zemcho.guzhe.controller.product.vo.CategoryVo;
import com.zemcho.guzhe.entity.app.AppVersion;
import com.zemcho.guzhe.entity.equipment.Equipment;
import com.zemcho.guzhe.entity.equipment.EquipmentLog;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductCategory;
import com.zemcho.guzhe.entity.shop.ShopPoster;
import com.zemcho.guzhe.entity.supermarket.SupermarketInfo;
import com.zemcho.guzhe.mapper.appVersion.AppVersionMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentLogMapper;
import com.zemcho.guzhe.mapper.equipment.EquipmentMapper;
import com.zemcho.guzhe.mapper.order.ProductOrderMapper;
import com.zemcho.guzhe.mapper.product.ProductCategoryMapper;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.screen.ScreenRentalOrderMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import com.zemcho.guzhe.mapper.shop.ShopPosterMapper;
import com.zemcho.guzhe.mapper.supermarket.SupermarketMapper;
import com.zemcho.guzhe.service.app.AppService;
import com.zemcho.guzhe.util.CommonUtils;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.LocalDateUtil;
import com.zemcho.guzhe.util.decode.Md5Util;
import com.zemcho.guzhe.util.redis.RedisUtil;
import com.zemcho.guzhe.util.wechat.WechatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IAppService implements AppService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentLogMapper equipmentLogMapper;

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Autowired
    private SupermarketMapper supermarketMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopPosterMapper shopPosterMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ScreenRentalOrderMapper screenRentalOrderMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private OtherConfig otherConfig;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private RedisUtil redisUtil;

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
        if (!equipmentInfo.getStatus().equals(1)) {
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
     * 更新设备请求相关信息
     *
     * @param equipmentId
     * @param nowOnlineStatus
     */
    public void updateEquipmentRequireData(Integer equipmentId, Integer nowOnlineStatus) {
        LocalDateTime now = LocalDateTime.now();

        Equipment equipmentUpdate = new Equipment();
        equipmentUpdate.setId(equipmentId);
        equipmentUpdate.setAppRequireTime(now);
        if (!nowOnlineStatus.equals(0)) {
            equipmentUpdate.setOnlineStatus(0);

            EquipmentLog equipmentLog = new EquipmentLog();
            equipmentLog.setEquipmentId(equipmentId);
            equipmentLog.setStatus(1);
            equipmentLog.setCreatedTime(now);
            equipmentLogMapper.insert(equipmentLog);
        }
        equipmentMapper.updateByEquipment(equipmentUpdate);
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

        SupermarketInfo supermarketInfo = supermarketMapper.selectById(equipmentInfo.getSupermarketId());

        Map<String, Object> result = new HashMap<>();
        result.put("equipmentInfo", equipmentInfo);
        result.put("supermarketInfo", supermarketInfo);

        updateEquipmentRequireData(equipmentId, equipmentInfo.getOnlineStatus());

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

        updateEquipmentRequireData(equipmentId, equipmentInfo.getOnlineStatus());

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
        equipmentMapper.updateByEquipment(equipmentUpdate);

        return Result.success("操作成功");
    }

    /**
     * 获取设备下的商家列表
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getShopList(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        //获取设备下的商家id
        List<Integer> shopIds = screenRentalOrderMapper.selectShopIdByEquipmentId(equipmentId);
        if (shopIds == null || shopIds.isEmpty()) {
            return Result.success("获取成功", new ArrayList<>());
        }

        List<AppShopListVo> shopList = shopMapper.selectAppShopListsByShopIds(shopIds);
        if (shopList != null && !shopList.isEmpty()) {
            List<ShopPoster> shopPosterList = shopPosterMapper.selectByShopIds(shopIds);
            Map<Integer, List<ShopPoster>> shopPosterMap = new HashMap<>();
            if (shopPosterList != null && !shopPosterList.isEmpty()) {
                shopPosterMap = shopPosterList.stream().collect(Collectors.groupingBy(ShopPoster::getShopId));
            }

            for (AppShopListVo shopInfo : shopList) {
                if (shopInfo.getGalleryImagesStr() != null && !shopInfo.getGalleryImagesStr().isEmpty()) {
                    shopInfo.setGalleryImages(JSON.parseArray(shopInfo.getGalleryImagesStr(), String.class));
                    shopInfo.setGalleryImagesStr(null);
                }

                shopInfo.setPosterList(shopPosterMap.get(shopInfo.getId()));
            }
        }

        return Result.success("获取成功", shopList);
    }

    /**
     * 获取设备下某商家的商品分类列表
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result getProductCategoryList(SearchParam param, String accessToken) {
        Integer shopId = param.getSearchField4();
        if (shopId == null) {
            return Result.error("参数异常");
        }

        List<ProductCategory> list = productCategoryMapper.selectList(param);

        return Result.success("获取成功", list);
    }

    /**
     * 获取设备下某商家的商品列表
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result getProductList(SearchParam param, String accessToken) {
        Integer shopId = param.getSearchId();
        if (shopId == null) {
            return Result.error("参数异常");
        }

        int pageNum = param.getPageNum();
        int pageSize = param.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<AppProductListVo> list = productMapper.selectListByApp(param);
        if (list != null && !list.isEmpty()) {
            for (AppProductListVo productInfo : list) {
                if (productInfo.getGalleryImagesStr() != null && !productInfo.getGalleryImagesStr().isEmpty()) {
                    productInfo.setGalleryImages(JSON.parseArray(productInfo.getGalleryImagesStr(), String.class));
                    productInfo.setGalleryImagesStr(null);
                }

                productInfo.setSaleNumText(CommonUtils.numberFormat(productInfo.getSaleNum(), true, true));
            }
        }
        PageInfo<AppProductListVo> pageInfo = new PageInfo<>(list);

        return Result.success("获取成功", pageInfo);
    }

    /**
     * 获取商品详情
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result getProductInfo(SearchParam param, String accessToken) {
        Integer productId = param.getSearchId();
        if (productId == null) {
            return Result.error("参数异常");
        }

        Product productInfo = productMapper.selectById(productId);
        if (productInfo == null) {
            return Result.error("商品不存在");
        }

        AppProductVo productVo = new AppProductVo();
        BeanUtils.copyProperties(productInfo, productVo, "galleryImages");
        if (productInfo.getGalleryImages() != null && !productInfo.getGalleryImages().isEmpty()) {
            productVo.setGalleryImages(JSON.parseArray(productInfo.getGalleryImages(), String.class));
        }
        productVo.setSaleNumText(CommonUtils.numberFormat(productVo.getSaleNum(), true, true));
        List<Integer> productIds = Arrays.asList(productId);
        List<CategoryVo> productCategoryList = productCategoryMapper.selectByProductIds(productIds);
        productVo.setCategoryList(productCategoryList);

        return Result.success("获取成功", productVo);
    }

    /**
     * 获取商品订单小程序码
     *
     * @param param
     * @param accessToken
     * @return
     */
    @Override
    public Result getProductOrderQRCode(ProductOrderQRParam param, String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        Integer productId = param.getProductId();
        Integer num = param.getNum();

        //判断小程序码是否已生成，未生成则生成对应的小程序码
        String scene = productId + "_" + num + "_" + equipmentId;
        String fileName = scene + "_" + Md5Util.MD5(equipmentId.toString()) + ".png";
        String filePath = localFilePath + uploadFilePath + "/product_order_qr/" + productId + "/";
        WechatUtil.generateAndSaveQrCode(scene, wechatConfig.getProductOrderQRPage(), 430,
                wechatConfig.getCodeQREnv(), filePath, fileName);

        Map<String, Object> result = new HashMap<>();
        result.put("filePath", uploadFilePath + "/product_order_qr/" + productId + "/" + fileName);

        return Result.success("获取成功", result);
    }

    /**
     * 获取设备屏幕店位租赁订单小程序码
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getScreenOrderQRCode(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        //判断小程序码是否已生成，未生成则生成对应的小程序码
        String scene = equipmentId.toString();
        String fileName = scene + "_" + Md5Util.MD5(appJwtData.getSerialNumber()) + ".png";
        String filePath = localFilePath + uploadFilePath + "/screen_order_qr/";
        WechatUtil.generateAndSaveQrCode(scene, wechatConfig.getScreenOrderQRPage(), 430,
                wechatConfig.getCodeQREnv(), filePath, fileName);

        Map<String, Object> result = new HashMap<>();
        result.put("filePath", uploadFilePath + "/screen_order_qr/" + fileName);

        return Result.success("获取成功", result);
    }

    /**
     * 获取设备下的商品订单详情
     *
     * @param accessToken
     * @return
     */
    @Override
    public Result getProductOrderInfo(String accessToken) {
        AppJwtData appJwtData = JWTUtil.getAppAuthJwtData(accessToken);
        if (appJwtData == null) {
            return new Result(10006, "token无效");
        }

        Integer equipmentId = appJwtData.getId();

        //从redis队列里获取订单id
        Object value = redisUtil.leftPop(Constant.PRODUCT_ORDER_RESULT_QUEUE_PREFIX + equipmentId);
        if (value == null) {
            return Result.success("暂没有订单信息");
        }
        Integer orderId = Integer.parseInt(value.toString());

        ProductOrder orderInfo = productOrderMapper.selectById(orderId);
        if (orderInfo == null) {
            return Result.error("订单不存在");
        }

        AppProductOrderVo productOrderVo = new AppProductOrderVo();
        BeanUtils.copyProperties(orderInfo, productOrderVo);

        return Result.success("获取成功", productOrderVo);
    }
}
