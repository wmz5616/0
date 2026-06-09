package com.zemcho.guzhe.util.wechat;

import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaVisitTrend;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.zemcho.guzhe.config.wechat.WechatConfig;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.file.FileUploadUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 微信工具类
 *
 * @title: WechatUtil
 * @Description:
 * @Date: 2024/7/5 14:35
 */
public class WechatUtil {
    private static final Logger log = LoggerFactory.getLogger(WechatUtil.class);

    // 使用AtomicReference来避免synchronized的性能瓶颈
    private static final AtomicReference<WxMaService> wxMaServiceRef = new AtomicReference<>();

    /**
     * 获取微信服务
     *
     * @return
     */
    public static WxMaService getWxCpService() {
        WxMaService service = wxMaServiceRef.get();
        if (service == null) {
            WxMaService newService = createWxCpService();
            if (wxMaServiceRef.compareAndSet(null, newService)) {
                service = newService;
            } else {
                // 再次获取，防止并发创建了多个实例
                service = wxMaServiceRef.get();
            }
        }
        return service;
    }

    /**
     * 创建微信服务
     *
     * @return
     */
    private static WxMaService createWxCpService() {
        WechatConfig wechatConfig = BeanUtil.getBean("wechatConfig", WechatConfig.class);

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wechatConfig.getAppId());
        config.setSecret(wechatConfig.getSecret());
        config.setMsgDataFormat("JSON");
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);

        return wxMaService;
    }

    /**
     * 根据code获取微信session
     *
     * @param code
     * @return
     */
    public static Map<String, String> getWechatSession(String code) {
        try {
            //获取openId、unionid、session_key
            WxMaJscode2SessionResult sessionInfo = getWxCpService().getUserService().getSessionInfo(code);
            if (sessionInfo == null) {
                return null;
            }

            Map<String, String> result = new HashMap<>();
            result.put("openId", sessionInfo.getOpenid());
            result.put("unionId", sessionInfo.getUnionid());
            result.put("sessionKey", sessionInfo.getSessionKey());

            return result;
        } catch (Exception e) {
            log.error("WechatUtil getWechatSession异常：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密用户信息
     *
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return
     */
    public static Map<String, String> getUserInfo(String sessionKey, String encryptedData, String iv) {
        try {
            //解密用户信息
            WxMaUserInfo wxUserInfo = getWxCpService().getUserService().getUserInfo(sessionKey, encryptedData, iv);
            if (wxUserInfo == null) {
                return null;
            }

            Map<String, String> result = new HashMap<>();
            result.put("nickName", wxUserInfo.getNickName());
            result.put("gender", wxUserInfo.getGender());
            result.put("language", wxUserInfo.getLanguage());
            result.put("city", wxUserInfo.getCity());
            result.put("province", wxUserInfo.getProvince());
            result.put("country", wxUserInfo.getCountry());
            result.put("avatarUrl", wxUserInfo.getAvatarUrl());
            result.put("unionId", wxUserInfo.getUnionId());

            return result;
        } catch (Exception e) {
            log.error("WechatUtil getUserInfo异常：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密手机号码信息
     *
     * @param sessionKey
     * @param encryptedData
     * @param iv
     * @return
     */
    public static String getWechatPhone(String sessionKey, String encryptedData, String iv) {
        try {
            // 解密手机号码信息
            WxMaPhoneNumberInfo phoneInfo = getWxCpService().getUserService().getPhoneNoInfo(sessionKey,
                    encryptedData, iv);
            if (phoneInfo == null) {
                return null;
            }

            return phoneInfo.getPhoneNumber();
        } catch (Exception e) {
            log.error("WechatUtil getWechatPhone异常：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成无数量限制的小程序码（通过 scene 携带参数）
     *
     * @param scene 场景值，最长32个字符
     * @param page  小程序页面路径
     * @param width 二维码宽度
     * @return
     */
    public static byte[] createUnlimitedQrCode(String scene, String page, Integer width, String envVersion) {
        try {
            return getWxCpService().getQrcodeService()
                    .createWxaCodeUnlimitBytes(
                            scene,           // scene
                            page,            // page
                            false,           // autoColor: 是否自动配色
                            envVersion,      // envVersion: "develop"|"trial"|"release"，生产传 null
                            width != null ? width : 430,  // width
                            true,            // isHyaline: 是否透明底色
                            null,            // lineColor: 自定义颜色，如 new WxMaCodeLineColor(0,0,0)，null 表示黑色
                            true             // throwError: true 表示出错抛异常
                    );
        } catch (Exception e) {
            log.error("生成小程序码失败。scene={}, page={}", scene, page, e);
            return null;
        }
    }

    /**
     * 小程序码生成并保存到本地
     */
    public static Boolean generateAndSaveQrCode(String scene, String page, Integer width, String envVersion,
                                                String pathTemp, String fileName) {
        // 判断文件是否已存在
        String filePath = pathTemp + fileName;
        Path existPath = Paths.get(filePath);
        if (Files.exists(existPath)) {
            log.info("小程序码已存在，跳过生成：{}", filePath);
            return true;
        }

        FileUploadUtil.getAbsoluteFile(pathTemp, fileName);

        byte[] imageBytes = createUnlimitedQrCode(scene, page, width, envVersion);
        if (imageBytes == null) {
            return false;
        }
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, imageBytes);
            log.info("小程序码已保存至: {}", filePath);

            FileUploadUtil.setPosixPermissions(path, "rw-r--r--");

            return true;
        } catch (Exception e) {
            log.error("小程序码保存失败", e);
            return false;
        }
    }

    /**
     * 获取用户访问小程序数据日趋势（总览）
     *
     * @param beginDate 开始日期，格式：yyyyMMdd
     * @param endDate   结束日期，格式：yyyyMMdd
     * @return 趋势数据
     */
    public static List<WxMaVisitTrend> getDailyVisitTrend(Date beginDate, Date endDate) {
        try {
            return getWxCpService().getAnalysisService().getDailyVisitTrend(beginDate, endDate);
        } catch (WxErrorException e) {
            log.error("调用微信数据分析接口失败", e);
            return null;
        }
    }

    /**
     * 发送订阅消息
     *
     * @param openId     用户openId
     * @param templateId 模板id
     * @param page       跳转页面
     * @param data       数据 只能是数据类型 不能是对象
     * @return
     */
    public static Boolean sendSubscribeMsg(String openId, String templateId, String page, Map<String, Object> data) {
        try {
            WxMaMsgService msgService = getWxCpService().getMsgService();

            List<WxMaSubscribeMessage.MsgData> msgData = new ArrayList<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                WxMaSubscribeMessage.MsgData msgDataItem = new WxMaSubscribeMessage.MsgData();
                msgDataItem.setName(entry.getKey());
                msgDataItem.setValue(entry.getValue().toString());
                msgData.add(msgDataItem);
            }

            WxMaSubscribeMessage message =
                    WxMaSubscribeMessage.builder().toUser(openId).templateId(templateId).data(msgData).page(page).build();

            msgService.sendSubscribeMsg(message);

            return true;
        } catch (WxErrorException e) {
            log.error("WechatUtil 发送订阅消息异常 ", e);
            return false;
        }
    }
}
