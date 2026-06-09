package com.zemcho.guzhe.service.wechat.subscribe;

import com.zemcho.guzhe.entity.audit.SettlementApplication;
import com.zemcho.guzhe.entity.cas.CasUser;
import com.zemcho.guzhe.entity.order.ProductOrder;
import com.zemcho.guzhe.entity.order.ProductOrderRefundApply;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.shop.Shop;
import com.zemcho.guzhe.entity.shop.ShopAudit;
import com.zemcho.guzhe.entity.shop.ShopAuditManager;
import com.zemcho.guzhe.entity.shop.ShopManager;
import com.zemcho.guzhe.mapper.cas.CasUserMapper;
import com.zemcho.guzhe.mapper.shop.ShopAuditManagerMapper;
import com.zemcho.guzhe.mapper.shop.ShopManagerMapper;
import com.zemcho.guzhe.mapper.shop.ShopMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信订阅消息业务封装
 */
@Slf4j
@Service
public class WechatSubscribeMessageService {
    private static final String TEMPLATE_REFUND_APPLY = "bSwdCAsvwIFyxTjFSIIFCFkHQHlKNK3ry_CbXzeFYBU";
    private static final String TEMPLATE_REFUND_RESULT = "Wc4hN5noMvfgnHwO9Qyr2RT_dIjCSxavpfqWUczOYEw";
    private static final String TEMPLATE_REFUND_DIRECT = "LoTfGAxJ88WFrSezbxgRfwanYXP2LR_IJ_lBTtXixEQ";
    private static final String TEMPLATE_SETTLEMENT_AUDIT = "_nzBwtw0UA6XeYn3nLLBoyOjD15eHy4IdbRj9DBi8SM";
    private static final String TEMPLATE_ORDER_PAID = "_aW1f4L5xmqkrN831qbPp1VlZI0uTPA-z737ERCeQWc";
    private static final String TEMPLATE_STOCK_WARNING = "9TsYaPEJc1lLa2o9F3WEn9n8f0pEPyLHRyGJtuNnnWc";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private WechatSubscribeAsyncSender asyncSender;

    @Autowired
    private ShopManagerMapper shopManagerMapper;

    @Autowired
    private ShopAuditManagerMapper shopAuditManagerMapper;

    @Autowired
    private CasUserMapper casUserMapper;

    @Autowired
    private ShopMapper shopMapper;

    public void notifyRefundApplyToShopManagers(ProductOrder orderInfo, ProductOrderRefundApply refundApply) {
        if (orderInfo == null || refundApply == null || orderInfo.getShopId() == null) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing6", limitThing(orderInfo.getShopName()));
        data.put("thing1", limitThing(buildOrderProductText(orderInfo.getProductName(), orderInfo.getNum())));
        data.put("thing10", limitThing(refundApply.getReason()));
        data.put("date5", formatTime(refundApply.getCreateTime()));
        data.put("character_string4", limitCharacter(orderInfo.getOrderNo()));

        asyncSender.sendMessagesAsync(
                getShopManagerOpenIds(orderInfo.getShopId()),
                TEMPLATE_REFUND_APPLY,
                buildRefundListPage(orderInfo.getShopId()),
                data
        );
    }

    public void notifyRefundResultToUser(ProductOrder orderInfo, ProductOrderRefundApply refundApply) {
        if (orderInfo == null || refundApply == null || orderInfo.getUserId() == null) {
            return;
        }

        String refundResult = refundApply.getStatus() != null && refundApply.getStatus() == 2 ? "成功" : "失败";
        Integer refundAmount = refundApply.getStatus() != null && refundApply.getStatus() == 2
                ? defaultAmount(refundApply.getRefundAmount()) : 0;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing6", limitThing(orderInfo.getProductName()));
        data.put("phrase4", refundResult);
        data.put("amount2", formatAmount(refundAmount));
        data.put("character_string1", limitCharacter(orderInfo.getOrderNo()));
        data.put("thing5", limitThing("如有问题请联系客服"));

        String openId = getUserOpenId(orderInfo.getUserId());
        if (openId == null) {
            return;
        }

        asyncSender.sendMessagesAsync(
                Collections.singletonList(openId),
                TEMPLATE_REFUND_RESULT,
                buildLotteryDetailPage(orderInfo.getId()),
                data
        );
    }

    public void notifyDirectRefundToUser(ProductOrder orderInfo, Integer refundAmount, String refundReason) {
        if (orderInfo == null || orderInfo.getUserId() == null) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing12", limitThing(orderInfo.getShopName()));
        data.put("thing2", limitThing(buildOrderProductText(orderInfo.getProductName(), orderInfo.getNum())));
        data.put("amount3", formatAmount(defaultAmount(refundAmount)));
        data.put("thing11", limitThing(refundReason));
        data.put("character_string7", limitCharacter(orderInfo.getOrderNo()));

        String openId = getUserOpenId(orderInfo.getUserId());
        if (openId == null) {
            return;
        }

        asyncSender.sendMessagesAsync(
                Collections.singletonList(openId),
                TEMPLATE_REFUND_DIRECT,
                buildLotteryDetailPage(orderInfo.getId()),
                data
        );
    }

    public void notifySettlementAuditToManagers(ShopAudit shopAudit, SettlementApplication application) {
        if (shopAudit == null || application == null || application.getId() == null) {
            return;
        }

        String auditResult = application.getApplyResult() != null && application.getApplyResult() == 1 ? "通过" : "不通过";
        String tip = "点击查看";
        if (application.getApplyResult() != null && application.getApplyResult() == 2 && application.getRejectReason() != null
                && !application.getRejectReason().trim().isEmpty()) {
            tip = "点击查看/" + application.getRejectReason().trim();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing3", limitThing(shopAudit.getName()));
        data.put("phrase2", auditResult);
        data.put("thing6", limitThing(tip));

        asyncSender.sendMessagesAsync(
                getShopAuditManagerOpenIds(shopAudit.getId()),
                TEMPLATE_SETTLEMENT_AUDIT,
                buildSettlementPage(application.getId()),
                data
        );
    }

    public void notifyOrderPaidToShopManagers(ProductOrder orderInfo) {
        if (orderInfo == null || orderInfo.getShopId() == null) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing6", limitThing(orderInfo.getShopName()));
        data.put("thing1", limitThing(orderInfo.getProductName()));
        data.put("number11", defaultNumber(orderInfo.getNum()));
        data.put("amount2", formatAmount(defaultAmount(orderInfo.getAmount())));
        data.put("character_string10", limitThing(orderInfo.getOrderNo()));

        asyncSender.sendMessagesAsync(
                getShopManagerOpenIds(orderInfo.getShopId()),
                TEMPLATE_ORDER_PAID,
                buildOrderManagementPage(orderInfo.getShopId(), orderInfo.getShopName()),
                data
        );
    }

    public void notifyLowStockToShopManagers(Product productInfo, Integer stock) {
        if (productInfo == null || productInfo.getShopId() == null || stock == null) {
            return;
        }
        if (stock != 2 && stock != 0) {
            return;
        }

        Shop shopInfo = shopMapper.selectById(productInfo.getShopId());
        if (shopInfo == null) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thing1", limitThing(shopInfo.getName()));
        data.put("thing6", limitThing(productInfo.getName()));
        data.put("number7", stock);
        data.put("thing5", limitThing("商品库存告警，请及时添加库存"));

        asyncSender.sendMessagesAsync(
                getShopManagerOpenIds(productInfo.getShopId()),
                TEMPLATE_STOCK_WARNING,
                buildProductManagePage(productInfo.getShopId(), shopInfo.getName()),
                data
        );
    }

    private List<String> getShopManagerOpenIds(Integer shopId) {
        List<ShopManager> managerList = shopManagerMapper.selectByShopId(shopId);
        List<String> openIds = new ArrayList<>();
        if (managerList == null || managerList.isEmpty()) {
            return openIds;
        }

        for (ShopManager manager : managerList) {
            if (manager.getPhone() == null || manager.getPhone().trim().isEmpty()) {
                continue;
            }
            CasUser user = casUserMapper.selectByPhone(manager.getPhone().trim());
            if (user != null && user.getOpenId() != null && !user.getOpenId().trim().isEmpty()) {
                openIds.add(user.getOpenId().trim());
            }
        }
        return openIds;
    }

    private List<String> getShopAuditManagerOpenIds(Integer shopAuditId) {
        List<ShopAuditManager> managerList = shopAuditManagerMapper.selectByShopAuditId(shopAuditId);
        List<String> openIds = new ArrayList<>();
        if (managerList == null || managerList.isEmpty()) {
            return openIds;
        }

        for (ShopAuditManager manager : managerList) {
            if (manager.getPhone() == null || manager.getPhone().trim().isEmpty()) {
                continue;
            }
            CasUser user = casUserMapper.selectByPhone(manager.getPhone().trim());
            if (user != null && user.getOpenId() != null && !user.getOpenId().trim().isEmpty()) {
                openIds.add(user.getOpenId().trim());
            }
        }
        return openIds;
    }

    private String getUserOpenId(Integer userId) {
        CasUser user = casUserMapper.selectById(userId);
        if (user == null || user.getOpenId() == null || user.getOpenId().trim().isEmpty()) {
            log.info("微信订阅消息未发送，用户openId为空，userId:{}", userId);
            return null;
        }
        return user.getOpenId().trim();
    }

    private String buildOrderManagementPage(Integer shopId, String shopName) {
        return "pages/orderManagement/orderManagement?shopId=" + shopId + "&name=" + safePageValue(shopName) + "&type=shoplist";
    }

    private String buildSettlementPage(Integer applicationId) {
        return "/pages/applySettlement/applySettlement?shopId=" + applicationId;
    }

    private String buildRefundListPage(Integer shopId) {
        return "/pages/refundList/refundList?shopId=" + shopId;
    }

    private String buildLotteryDetailPage(Integer orderId) {
        return "/pages/lotteryDetail/lotteryDetail?searchId=" + orderId;
    }

    private String buildProductManagePage(Integer shopId, String shopName) {
        return "/pages/merchantShjoppList/merchantShjoppList?shopId=" + shopId + "&name=" + safePageValue(shopName);
    }

    private String buildOrderProductText(String productName, Integer num) {
        return safeText(productName) + "*" + defaultNumber(num);
    }

    private Integer defaultAmount(Integer amount) {
        return amount == null ? 0 : amount;
    }

    private Integer defaultNumber(Integer num) {
        return num == null ? 0 : num;
    }

    private String formatAmount(Integer amount) {
        BigDecimal yuan = BigDecimal.valueOf(defaultAmount(amount)).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        return yuan.toPlainString() + "元";
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? "" : time.format(DATETIME_FORMATTER);
    }

    private String limitThing(String text) {
        return truncate(safeText(text), 20);
    }

    private String limitCharacter(String text) {
        return truncate(safeText(text), 32);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }

    private String safePageValue(String text) {
        return text == null ? "" : text.trim();
    }
}
