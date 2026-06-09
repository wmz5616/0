package com.zemcho.ddql.util;

/**
 * @author Ryan
 * @title: Constant
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/24 0024 14:45
 */
public class Constant {

    public static final String PRODUCT_NO_SEQ = "product:seq:";

    private Constant() {
    }

    /**
     * 基础配置缓存key前缀
     */
    public static final String BASIC_CONFIG_CACHE_PREFIX = "basic_config_type:";

    /**
     * redis-key-前缀-shiro:token:
     */
    public static final String PREFIX_SHIRO_TOKEN = "shiro:token:";

    /**
     * redis-key-前缀-shiro:mini:token:
     */
    public static final String PREFIX_MINI_SHIRO_TOKEN = "shiro:mini:token:";

    /**
     * JWT-mini-id:
     */
    public static final String MINI_USER_ID = "miniUserId";

    /**
     * redis-key-前缀-shiro:refresh_token:
     */
    public static final String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

    /**
     * JWT-authData:
     */
    public static final String JWT_AUTH_DATA = "authData";

    /**
     * JWT-currentTimeMillis:
     */
    public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";

    /**
     * 登录失败记录:
     */
    public static final String LOGIN_ERROR_PREFIX = "master:login:error:";

    /**
     * 授权
     */
    public static final String AUTH_CHECK = "auth_check";

    /**
     * 请求属性-authAttrData
     */
    public static final String REQUEST_ATTR_DATA = "authAttrData";

    /**
     * 管理员权限信息缓存前缀
     */
    public static final String ADMIN_PERMISSION_DATA_PREFIX = "admin:permission:";

    /**
     * 验证码code
     */
    public static final String CODE_PREFIX = "ddql:code:";

    /**
     * 绑定验证码code
     */
    public static final String BIND_PREFIX = "ddql:bind:";

    /**
     * app-JWT-authData:
     */
    public static final String APP_JWT_AUTH_DATA = "appAuthData";

    /**
     * app请求量限制key前缀
     */
    public static final String APP_REQUEST_NUM_LIMIT_CACHE_PREFIX = "app_request_num_limit:";

    /**
     * app上一次access_token缓存key前缀
     */
    public static final String APP_LAST_ACCESS_TOKEN_CACHE_PREFIX = "app_last_access_token:";

    /**
     * 用户操作锁前缀
     */
    public static final String USER_OPERATION_PREFIX = "ddql:user_operation_lock:";

    /**
     * 用户打卡币获取标志前缀
     */
    public static final String USER_CHECK_IN_COIN_PREFIX = "ddql:user_coin_obtain:";

    /**
     * 用户打卡排名前缀
     */
    public static final String USER_CHECK_IN_RANK_PREFIX = "ddql:user_check_in:rank:";

    /**
     * 设备扫码打卡结果队列前缀
     */
    public static final String EQUIPMENT_CHECK_IN_RESULT_QUEUE_PREFIX = "ddql:equipment_check_in_result:queue:";

    /**
     * 充值购币订单锁前缀
     */
    public static final String TEAM_RECHARGE_ORDER_PREFIX = "ddql:team_recharge_order_lock:";

    /**
     * 订单未支付超时自动取消监控key
     */
    public static final String ORDER_UNPAY_MONITOR = "ddql:order:unpay:monitor";

    /**
     * 商品兑换订单未支付超时自动取消监控key
     */
    public static final String EXCHANGE_ORDER_UNPAY_MONITOR = "ddql:exchange_order:unpay:monitor";

    /**
     * 订单支付回调锁前缀
     */
    public static final String ORDER_PAY_CALLBACK_LOCK_PREFIX = "ddql:order:pay_callback:";

    /**
     * 商品券码缓存key前缀
     */
    public static final String PRODUCT_TICKET_LIST = "ddql:product_ticket:";

    /**
     * 导入操作锁前缀
     */
    public static final String IMPORT_LOCK_PREFIX = "ddql:import:";

    /**
     * 微信支付订单key 用于判断是否已经创建对应的微信支付 加订单编号
     */
    public static final String WECHAT_ORDER_PREFIX = "ddql:wechat:order";
}
