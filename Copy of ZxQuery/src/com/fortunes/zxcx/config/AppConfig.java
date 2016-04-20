package com.fortunes.zxcx.config;

import java.io.File;

import android.os.Environment;

/**
 * @ClassName: AppConfig
 * @Description: 应用程序的根配置，比如版本，目录配置等等。
 * @author liuyongzheng
 * @date 2014-3-18 下午2:18:59
 */
public class AppConfig {
	private final static String APP_CONFIG = "config";

	// APP内部版本号
	public final static int APP_VERSION_CODE = 5;// 这个要重新写
	// APP版本名称
	public final static String APP_VERSION_NAME = "2.2";
	// APP名称
	public static final String APP_NAME_DESC = "征信查询";
	// APP名称
	public static final String APP_NAME = "fortunes-zxcx-client";

	public final static String DB_NAME = "fortuneszxcx.db";// 数据库名字
	public final static int DB_VERSION = 1;// 数据库版本号

	// 是否打印Log信息 true打印 false不打印
	public final static boolean isDebug = true;
	/**
	 * PreferencesUtils用到的
	 */
	// PreferencesUtils
	public final static String APP_PREFERENCE_NAME = "fortunes-zxcx-client";
	// 用户名
	public final static String USER_NAME = "username";
	// 用户身份证号
	public final static String USER_ID = "userid";
	// 用户证件类型
	public final static String USER_ID_TYPE = "user_id_type";
	// 用户手机号码
	public final static String USER_PHONE = "user_phone";

	// 完成支付页面标记
	public static String PAY_WHERE = "where";
	

	/***
	 * SDCard路径
	 */
	public static final String SDCARD_Path = Environment.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * 报告zip保存路径
	 */
	public static final String ZIP_PATH = SDCARD_Path + File.separator + "zxbg.zip";
}
