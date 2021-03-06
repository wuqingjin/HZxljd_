package com.ruoyi.common.constant;

/**
 * Shiro通用常量
 * 
 * @author ruoyi
 */
public interface ShiroConstants
{
    /**
     * 当前登录的用户
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * 用户名
     */
    public static final String CURRENT_USERNAME = "username";

    /**
     * 消息key
     */
    public static final String MESSAGE = "message";

    /**
     * 错误key
     */
    public static final String ERROR = "errorMsg";

    /**
     * 编码格式
     */
    public static final String ENCODING = "UTF-8";

    /**
     * 当前在线会话
     */
    public static final String ONLINE_SESSION = "online_session";

    /**
     * 验证码key
     */
    public static final String CURRENT_CAPTCHA = "captcha";

    /**
     * 验证码开关
     */
    public static final String CURRENT_ENABLED = "captchaEnabled";

    /**
     * 验证码类型
     */
    public static final String CURRENT_TYPE = "captchaType";

    /**
     * 验证码
     */
    public static final String CURRENT_VALIDATECODE = "validateCode";

    /**
     * 验证码错误
     */
    public static final String CAPTCHA_ERROR = "captchaError";

    /**
     * 登录记录缓存
     */
    public static final String LOGINRECORDCACHE = "loginRecordCache";

    /**
     * 系统活跃用户缓存
     */
    public static final String SYS_USERCACHE = "sys-userCache";
    
    /**
     * 账号密码登录
     */
    public static final String LOGIN_TYPE_USERNAME_PASSWORD = "password";
    
    /**
     * 鉴权秘钥登录
     */
    public static final String LOGIN_TYPE_USERNAME_SECRET_KEY ="secretKey";

    /**
     * 微信小程序自动登录
     */
    public static final String LOGIN_TYPE_WX_OPENID ="wx-openid";

    /**
     * 微信小程序账号密码登陆
     */
    public static final String LOGIN_TYPE_WX_USER_PSWD ="wx-login";

    /**
     * 微信小程序请求携带请求头
     */
    public static final String HEADER_TOKEN_NAME ="X-HKWS-Token";

    /**
     * 对外接口的请求头
     */
    public static final String HEADER_API_TOKEN_NAME ="X-HKAPI-Token";
}
