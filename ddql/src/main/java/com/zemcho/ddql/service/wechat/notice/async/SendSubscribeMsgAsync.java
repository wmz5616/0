package com.zemcho.ddql.service.wechat.notice.async;

import com.zemcho.ddql.util.wechat.WechatUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @title: SendSubscribeMsgAsync
 * @Description: 微信订阅消息通知异步处理
 * @Date: 2025/11/10 9:48
 */
@Component
public class SendSubscribeMsgAsync {
    /**
     * 异步发送微信订阅消息
     *
     * @param openId
     * @param templateId
     * @param page
     * @param data
     */
    @Async("customAsyncThreadPool")
    public void asyncSendSubscribeMsg(String openId, String templateId, String page, Map<String, Object> data) {
        WechatUtil.sendSubscribeMsg(openId, templateId, page, data);
    }
}
