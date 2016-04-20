package com.fortunes.zxcx.keys;

import java.util.Map;

import org.apache.http.Header;

import android.content.Context;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.bean.KeyBean;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.NetUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.fortunes.zxcx.view.LoadingDialog;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class KeyUtils {
	/**
	 * 公钥的key
	 */
	public final static String PUBLIC_KEY = "RSAPublicKey";
	/**
	 * 私钥的key
	 */
	public final static String PRIVATE_KEY = "RSAPrivateKey";
	
	/**
	 * 服务器AESkey
	 */
	public final static String SERVER_AES_KEY = "ServerAESKey";

	/**
	 * 服务器公钥
	 */
	public final static String SERVER_PUBLIC_KEY = "ServerPublicKey";
	
	/**
	 * 服务器AESkey2
	 */
	public final static String SERVER_AES_KEY_TWO = "ServerAESKey2";
	
	/**
	 * 服务器公钥2
	 */
	public final static String SERVER_PUBLIC_KEY_TWO = "ServerPublicKey2";

	/**
	 * 生成app公钥和密钥
	 * 
	 * @param context
	 * @return Map<String, String>
	 */
	public static Map<String, String> initKey(Context context) {// 产生公钥和密钥
		try {
			Map<String, String> map = new RSAUtils().genKeyPair();
			PreferencesUtils
					.putString(context, PUBLIC_KEY, map.get(PUBLIC_KEY));
			PreferencesUtils.putString(context, PRIVATE_KEY,
					map.get(PRIVATE_KEY));
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 密钥交互请求
	 * 
	 * @param type
	 *            接口地址类型 0：第一个登录 1：我第二个登录
	 * 
	 * @param publicKey
	 *            公钥
	 * @param privateKey
	 *            私钥
	 * @param name
	 *            发起重新请求秘钥的请求方法名字，没有的时候为"go"
	 * @param context
	 * @param loadingDialog
	 */
	public void RequestKey(final int type, String publicKey, final String privateKey,
			final String name, final Context context,
			final LoadingDialog loadingDialog) {// 密钥交互请求
		if (NetUtils.isNetConnected(context)) {
			RequestParams rp = new RequestParams();
			rp.put("appRsaPubKey", publicKey);
			String url="";
			if (1==type) {
				url=Urls.KEY_URL_TWO;
			}else {
				url=Urls.KEY_URL;
			}
			https.get(context, url, rp,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							if (loadingDialog != null
									&& loadingDialog.isShowing()) {
								loadingDialog.dismiss();
							}
							if (!StringUtils.isEmpty(response)) {
								LogUtils.i(":response=", response);
								Gson gson = new Gson();
								KeyBean b = gson.fromJson(response,
										KeyBean.class);
								if (!b.isSuccess()) {
									return;
								}
								String JMserverAESkey = b.getAesKey();// 服务器加密的asekey
								String serverPublickey = b.getRsaPubKey();// 服务器公钥
								if (type==0) {
									PreferencesUtils.putString(context,
											SERVER_PUBLIC_KEY, serverPublickey);
									String serverAESkey = JmAESkey(type,context,
											JMserverAESkey, privateKey);
								}else {
									PreferencesUtils.putString(context,
											SERVER_PUBLIC_KEY_TWO, serverPublickey);
									String serverAESkey = JmAESkey(type,context,
											JMserverAESkey, privateKey);
								}
								// System.out.println("解密成功");
								if (rKeyListener != null) {
									if (StringUtils.isEmpty(name)) {
										rKeyListener.callBack("go");
									} else {
										rKeyListener.callBack(name);
									}
								}
								// getyzm(context, privateKey);
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							super.onFailure(arg0, arg1, arg2, arg3);
							LogUtils.i(":response=", "请求失败");
							ToastUtils.showShortToast(context, "网络请求错误");
							if (loadingDialog != null
									&& loadingDialog.isShowing()) {
								loadingDialog.dismiss();
							}
						}
					});
		} else {
			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			ToastUtils.showShortToast(context,
					context.getString(R.string.net_not_connected));
		}
	}

	private RequestKeyListener rKeyListener = null;

	public void setOnRequestKeyListener(RequestKeyListener KeyListener) {
		this.rKeyListener = KeyListener;
	};

	public interface RequestKeyListener {
		void callBack(String go);
	};

	/**
	 * 清除所有密钥相关数据
	 * 
	 * @param context
	 */
	public static void cleardata(Context context) {// 清除密钥相关的数据
		PreferencesUtils.putString(context, PUBLIC_KEY, "");
		PreferencesUtils.putString(context, PRIVATE_KEY, "");
		PreferencesUtils.putString(context, SERVER_AES_KEY, "");
		PreferencesUtils.putString(context, SERVER_PUBLIC_KEY, "");
		PreferencesUtils.putString(context, SERVER_AES_KEY_TWO, "");
		PreferencesUtils.putString(context, SERVER_PUBLIC_KEY_TWO, "");
	}

	/**
	 * 清除AES密钥相关数据
	 * 
	 * @param context
	 */
	public static void cleardata2(Context context) {// 清除AES密钥相关数据
		PreferencesUtils.putString(context, SERVER_AES_KEY, "");
		PreferencesUtils.putString(context, SERVER_PUBLIC_KEY, "");
		PreferencesUtils.putString(context, SERVER_AES_KEY_TWO, "");
		PreferencesUtils.putString(context, SERVER_PUBLIC_KEY_TWO, "");
	}

	/**
	 * 判断有没服务器AES密钥
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isKeyExchange(Context context) {
		if (!StringUtils.isEmpty(PreferencesUtils.getString(context,
				SERVER_AES_KEY))) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断有没服务器AES密钥,第二个登录
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isKeyExchange2(Context context) {
		if (!StringUtils.isEmpty(PreferencesUtils.getString(context,
				SERVER_AES_KEY_TWO))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 解密 AES密文方法
	 * 
	 * @param context
	 * @param AESkey
	 *            AES密钥
	 * @param privatekey
	 *            本地私钥
	 * @return String AES密钥
	 */
	public static String JmAESkey(int type,Context context, String AESkey,
			String privatekey) {// 解密aes密文
		try {
			Base64Utils base64Utils = new Base64Utils();
			byte[] s = base64Utils.decode(AESkey);
			byte[] aes = new RSAUtils().decryptByPrivateKey(s, privatekey);
			String serverAESkey = new String(aes);
			LogUtils.i("aes密钥=", serverAESkey);
			if (type==0) {
				PreferencesUtils.putString(context, SERVER_AES_KEY, serverAESkey);
			}else {
				PreferencesUtils.putString(context, SERVER_AES_KEY_TWO, serverAESkey);
			}
			return serverAESkey;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtils.i("aes密钥=", "解密aes失败");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * RSA加密方法
	 * 
	 * @param context
	 * @param ServerPublicKey
	 *            服务器公钥
	 * @return String 加密内容
	 */
	public static String RSAEncrypt(String content, String ServerPublicKey) {// 解密aes密文
		try {
			Base64Utils base64Utils = new Base64Utils();
			byte[] aes = new RSAUtils().encryptByPublicKey(content.getBytes(),
					ServerPublicKey);
			String sd = base64Utils.encode(aes);
//			LogUtils.i("RSA加密内容=", sd);
			return sd;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtils.i("RSA加密=", "加密失败");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AES加密方法
	 * 
	 * @param String
	 *            内容
	 * @param AESkey
	 *            AES密钥
	 * @return String 加密内容
	 */
	public static String AESEncrypt(String string, String AESkey) {
		try {
			String sd = AESEND.encrypt(string, AESkey); // 数据aes加密
			return sd;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AES解密方法
	 * 
	 * @param string
	 *            内容
	 * @param AESkey
	 *            AES密钥
	 * @return String 解密内容
	 */
	public static String AESdecrypt(String string, String AESkey) {
		try {
			String sd = AESEND.decrypt(string, AESkey); // 数据aes解密
			return sd;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtils.i("AES解密：", "解密失败");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 重新请求密钥交互
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            接口地址类型 0：第一个登录 1：我第二个登录
	 * 
	 * @param loadingDialog
	 *            加载提示框
	 * @param count
	 *            记录的重复请求的次数
	 * @param toast
	 *            取消这次请求的提示语句，不写默认为“返回数据”；
	 * @return 已经请求的次数
	 */
	public int RerequestKey(Context context, int type,
			LoadingDialog loadingDialog, int count, String toast) {
		if (count == 2) {// 设置多次重复请求3次就放弃这一次请求；
			KeyUtils.cleardata2(context);
			count = 0;
			if (StringUtils.isEmpty(toast)) {
				ToastUtils.showShortToast(context, "数据异常");
			} else {
				ToastUtils.showShortToast(context, toast);
			}
			return count;
		}
		count++;
		if (StringUtils.isEmpty(PreferencesUtils.getString(context,
				KeyUtils.PUBLIC_KEY))
				|| StringUtils.isEmpty(PreferencesUtils.getString(context,
						KeyUtils.PRIVATE_KEY))) {
			initKey(context);
		}
		String publickey = PreferencesUtils.getString(context,
				KeyUtils.PUBLIC_KEY);
		String privatekey = PreferencesUtils.getString(context,
				KeyUtils.PRIVATE_KEY);
		RequestKey(type, publickey, privatekey, "", context, loadingDialog);
		return count;
	}

	/**
	 * 重新请求密钥交互
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            接口地址类型 0：第一个登录 1：我第二个登录
	 * 
	 * @param loadingDialog
	 *            加载提示框
	 * @param count
	 *            记录的重复请求的次数
	 * @param RerequestName
	 *            从哪个请求中重新去请求密钥
	 * @param toast
	 *            取消这次请求的提示语句，不写默认为“返回数据”；
	 * @return 已经请求的次数
	 */
	public int RerequestKey(Context context, int type,
			LoadingDialog loadingDialog, int count, String RerequestName,
			String toast) {
		if (count == 2) {// 设置多次重复请求3次就放弃这一次请求；
			KeyUtils.cleardata2(context);
			count = 0;
			if (StringUtils.isEmpty(toast)) {
				ToastUtils.showShortToast(context, "数据异常");
			} else {
				ToastUtils.showShortToast(context, toast);
			}
			return count;
		}
		count++;
		if (StringUtils.isEmpty(PreferencesUtils.getString(context,
				KeyUtils.PUBLIC_KEY))
				|| StringUtils.isEmpty(PreferencesUtils.getString(context,
						KeyUtils.PRIVATE_KEY))) {
			initKey(context);
		}
		String publickey = PreferencesUtils.getString(context,
				KeyUtils.PUBLIC_KEY);
		String privatekey = PreferencesUtils.getString(context,
				KeyUtils.PRIVATE_KEY);
		RequestKey(type, publickey, privatekey, RerequestName, context,
				loadingDialog);
		return count;
	}
}
