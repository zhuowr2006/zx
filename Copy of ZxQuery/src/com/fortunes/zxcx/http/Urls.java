package com.fortunes.zxcx.http;

/**
 * @ClassName: Urls
 * @Description: 请求数据的URL
 * @author 
 * @date 2014-3-19 下午2:12:45
 */
public class Urls {
//	public static final String BASE_URL = "http://10.9.4.58:8888/jzxdc/admin/";//内网服务器地址1
	public static final String BASE_URL = "http://1018381579-qq.6655.la:21242/jzxdc/";//外网服务器地址1(花生壳)
//	public static final String BASE_URL = "http://359183.cicp.net/jzxdc/";//外网服务器地址1(花生棒)
//	public static final String BASE_TWO_URL = "http://10.9.4.58:8080/wzxdc/admin/";//内网服务器地址1
	public static final String BASE_TWO_URL = "http://1018381579-qq.6655.la:21242/wzxdc/admin/";//外网服务器地址1(花生壳)
//	public static final String BASE_TWO_URL = "http://359183.cicp.net/wzxdc/admin/";//外网服务器地址1(花生棒)
	
	

	public static final String KEY_URL = BASE_URL+"AppI/keyChange";// 密钥交互
	public static final String KEY_URL_TWO = BASE_TWO_URL+"AppI/keyChange";// 密钥交互2
	
	public static final String REGISTRATION_URL = BASE_URL+ "AppI/registration";// 注册
	
	public static final String LOGIN_URL = BASE_URL+"AppI/login";// 登录
	
//	public static final String LOGOUT_URL = BASE_URL+ "usersAppI/loginOut";// 注销
	public static final String VERIFYPHONE_URL = BASE_URL+ "AppI/verifyPhone";// 验证手机号码
	public static final String VERIFYCODE_URL = BASE_URL+ "AppI/verifyEmail";// 获取验证码
	
	public static final String FORGETPWD_URL = BASE_URL+ "AppI/findPwd";// 忘记密码
	public static final String IDAUTH_URL = BASE_URL+ "AppI/getAuthCode";// 获取身份认证授权码
	/**
	 *  提交验证与授权
	 */
	public static final String AUTHORIZATION_URL = BASE_URL + "AppI/certApprove";
	
	/**
	 * 征信报告登录
	 */
	public static final String CREDITLOGIN_URL = BASE_TWO_URL + "AppI/login";
	/**
	 * 修改密码
	 */
	public static final String MODIFYPWD_URL = BASE_TWO_URL + "AppI/changePwd";
	public static final String DOWNLOAD_REPORT = BASE_TWO_URL + "AppI/downLoadReport";
//	public static final String DOWNLOAD_REPORT = "http://10.9.3.11:8080/wzxdc/appl/applyReport";
	

}
