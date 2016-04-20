package com.fortunes.zxcx.application;

import android.app.Application;

import com.fortunes.zxcx.util.LogUtils;

public class BaseApplication extends Application {
	private String TAG = BaseApplication.class.getSimpleName();
	private static BaseApplication sInstance = null;
	// 本地app的RSA公钥和密钥
	public static String AppPublicKey;
	public static String AppPrivteKey;
	// 服务器上的AES密钥（已解密）和RSA密钥
	public static String PosAesKey;
	public static String PosRsaPubKey;
//	public static ImageLoader imageLoader = ImageLoader.getInstance();

	/**
	 * Global request queue for Volley
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;

	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		LogUtils.i(TAG, "BaseApplication  onError  onLowMemory");
		super.onLowMemory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		LogUtils.i(TAG, "BaseApplication  onError  onTerminate");
		super.onTerminate();
	}

	public static BaseApplication getInstance() {
		return sInstance;
	}
	/** 返回设备的版本号 默认为11 即3.0  **/
	public static int getSDKVersion(){
		try{
			return Integer.parseInt(android.os.Build.VERSION.SDK);
		}catch(Exception e){
			return 11;
		}
	}

}
