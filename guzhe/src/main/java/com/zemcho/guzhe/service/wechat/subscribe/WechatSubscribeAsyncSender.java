package com.zemcho.guzhe.service.wechat.subscribe;

import com.zemcho.guzhe.util.wechat.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 微信订阅消息异步发送器
 */
@Slf4j
@Service
public class WechatSubscribeAsyncSender {

    @Async("customAsyncThreadPool")
    public void sendMessagesAsync(List<String> openIds, String templateId, String page, Map<String, Object> data) {
        if (openIds == null || openIds.isEmpty() || templateId == null || templateId.isEmpty()) {
            return;
        }

        Set<String> distinctOpenIds = new LinkedHashSet<>();
        for (String openId : openIds) {
            if (openId != null && !openId.trim().isEmpty()) {
                distinctOpenIds.add(openId.trim());
            }
        }
        if (distinctOpenIds.isEmpty()) {
            return;
        }

        for (String openId : distinctOpenIds) {
            try {
                Boolean result = WechatUtil.sendSubscribeMsg(openId, templateId, page, data);
                log.info("微信订阅消息发送结果 templateId:{} openId:{} result:{}", templateId, openId, result);
            } catch (Exception e) {
                log.error("微信订阅消息发送异常 templateId:{} openId:{}", templateId, openId, e);
            }
        }
    }
}
