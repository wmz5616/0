package com.zemcho.guzhe.util;

/**
 * @author Ryan
 * @title: Constant
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/24 0024 14:45
 */
public class Constant {
    private Constant() {
    }

    /**
     * 基础配置缓存key前缀
     */
    public static final String BASIC_CONFIG_CACHE_PREFIX = "guzhe:basic_config_type:";

    /**
     * redis-key-前缀-shiro:token:
     */
    public static final String PREFIX_SHIRO_TOKEN = "guzhe:shiro:token:";

    /**
     * redis-key-前缀-shiro:mini:token:
     */
    public static final String PREFIX_MINI_SHIRO_TOKEN = "guzhe:shiro:mini:token:";

    /**
     * JWT-mini-id:
     */
    public static final String MINI_USER_ID = "miniUserId";

    /**
     * redis-key-前缀-shiro:refresh_token:
     */
    public static final String PREFIX_SHIRO_REFRESH_TOKEN = "guzhe:shiro:refresh_token:";

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
    public static final String LOGIN_ERROR_PREFIX = "guzhe:login:error:";

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
    public static final String ADMIN_PERMISSION_DATA_PREFIX = "guzhe:admin:permission:";

    /**
     * 验证码code
     */
    public static final String CODE_PREFIX = "guzhe:code:";

    /**
     * 绑定验证码code
     */
    public static final String BIND_PREFIX = "guzhe:bind:";

    /**
     * app-JWT-authData:
     */
    public static final String APP_JWT_AUTH_DATA = "appAuthData";

    /**
     * app请求量限制key前缀
     */
    public static final String APP_REQUEST_NUM_LIMIT_CACHE_PREFIX = "guzhe:app_request_num_limit:";

    /**
     * app上一次access_token缓存key前缀
     */
    public static final String APP_LAST_ACCESS_TOKEN_CACHE_PREFIX = "guzhe:app_last_access_token:";

    /**
     * 微信支付订单key 用于判断是否已经创建对应的微信支付 加订单编号
     */
    public static final String WECHAT_ORDER_PREFIX = "guzhe:wechat:order:";

    /**
     * 订单编号前缀
     */
    public static final String ORDER_NO_PREFIX = "guzhe:order:no:";

    /**
     * 券码列表
     */
    public static final String PRODUCT_TICKET_LIST = "guzhe:product:ticket:list:";

    /**
     * 导入操作锁前缀
     */
    public static final String IMPORT_LOCK_PREFIX = "guzhe:import:";

    /**
     * 用户操作锁前缀
     */
    public static final String OPERATION_LOCK_PREFIX = "guzhe:operation:";

    /**
     * 通莞支付失败回调写回的内容
     */
    public static final String TGY_PAY_CALLBACK_FAIL = "FAIL";

    /**
     * 通莞支付成功回调写回的内容
     */
    public static final String TGY_PAY_CALLBACK_SUCCESS = "SUCCESS";

    /**
     * 订单未支付超时自动取消监控key前缀
     */
    public static final String ORDER_UNPAY_MONITOR_PREFIX = "guzhe:order:unpay:monitor:";

    /**
     * 订单过期监控key前缀
     */
    public static final String ORDER_EXPIRED_MONITOR_PREFIX = "guzhe:order:expired:monitor:";

    /**
     * 设备商品扫码下单结果队列前缀
     */
    public static final String PRODUCT_ORDER_RESULT_QUEUE_PREFIX = "guzhe:product_order_result:queue:";
}