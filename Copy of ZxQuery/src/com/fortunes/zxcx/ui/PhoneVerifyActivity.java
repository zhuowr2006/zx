package com.fortunes.zxcx.ui;

import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.secure.RegisterActivity;
import com.fortunes.zxcx.ui.bean.MsgBean;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.NetUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.RegexUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 手机验证界面
 * 
 * @author zwr
 * 
 */
public class PhoneVerifyActivity extends BaseActivity implements OnClickListener {
	EditText phoneNumber_et, auth_code_et;
	Button get_verification_code_Btn, next_btn;
//	private boolean isnext = false;

	private final int maxLen = 60;
	private int recLen = maxLen;

	private final Handler delayedHandler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			recLen--;
			get_verification_code_Btn.setTextColor(getResources().getColor(R.color.bottom_text_color));
			get_verification_code_Btn.setEnabled(true);
			get_verification_code_Btn.setClickable(true);
			get_verification_code_Btn.setText(recLen + "s");
			delayedHandler.postDelayed(this, 1000);
			if (recLen <= 0) {
				recLen = maxLen;
				get_verification_code_Btn.setText(getResources().getText(R.string.get_auth_code));
				delayedHandler.removeCallbacks(runnable);
				get_verification_code_Btn.setEnabled(true);
				get_verification_code_Btn.setClickable(true);
				get_verification_code_Btn.setTextColor(getResources().getColor(R.color.white));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_phoneverify);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		phoneNumber_et = (EditText) findViewById(R.id.phoneNumber_et);
		auth_code_et = (EditText) findViewById(R.id.verification_code_et);
		get_verification_code_Btn = (Button) findViewById(R.id.get_verification_code_Btn);
		next_btn = (Button) findViewById(R.id.next_btn);
    }

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		get_verification_code_Btn.setOnClickListener(this);
		next_btn.setOnClickListener(this);
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				// if (isnext) {
				// if (StringUtils.isEmpty(get_verification_code_Btn.getText()
				// .toString())
				// || StringUtils.isEmpty(auth_code_et.getText()
				// .toString())) {
				// return;
				// }
				VerifyPhoneRequest();
				// } else {
				// if (StringUtils.isEmpty(get_verification_code_Btn.getText()
				// .toString())) {
				// return;
				// }
				// getCodeRequest();
				// }
			}
		});
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setTopTitle("注册");

	}

	/**
	 * @Description:[ 判断手机号是否为空且校验手机号 ]
	 * @param phoneNumber
	 *            手机号
	 * @return
	 */
	private boolean checkGetVerificationCodeParams(String phoneNumber) {
		if (StringUtils.isBlank(phoneNumber)) {
			ToastUtils.showOnceToast(this, "请输入手机号！");
			return false;
		}
		if (!RegexUtils.checkMobile(phoneNumber)) {
			ToastUtils.showOnceToast(this, "手机号码格式不正确！");
			return false;
		}
		return true;
	}

	private boolean checkRegisterParams(String phoneNumber, String checkCode) {
		LogUtils.i(TAG, "phoneNumber=" + phoneNumber);
		if (!checkGetVerificationCodeParams(phoneNumber)) {
			return false;
		}
		if (StringUtils.isBlank(checkCode)) {
			ToastUtils.showOnceToast(this, "请输入验证码！");
			return false;
		} else if (checkCode.length() < 6) {
			ToastUtils.showOnceToast(this, "验证码错误！");
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next_btn:// 下一步
			// isnext = true;
			if (!KeyUtils.isKeyExchange(this)) {// 如果没有AES密钥就重新交互一次
				initProgressDialog();
				keyUtils.RerequestKey(this, 0,loadingDialog, 0, "");// 重新请求密钥
				return;
			}
			if (checkRegisterParams(phoneNumber_et.getText().toString(), auth_code_et.getText().toString())) {
				VerifyPhoneRequest();
			}
			// Intent intent = new Intent(); // 新建Intent
			// intent.putExtra("phone", phoneNumber_et.getText().toString());//
			// 传入参数,注册接口需要传入电话号码
			// intent.setClass(PhoneVerifyActivity.this,
			// RegisterActivity.class);// 设置调用的Activity
			// startActivity(intent);// 显示界面
			break;
		case R.id.get_verification_code_Btn:// 获取验证码
			// isnext = false;
			if (!KeyUtils.isKeyExchange(this)) {// 如果没有AES密钥就重新交互一次
				initProgressDialog();
				keyUtils.RerequestKey(this, 0,loadingDialog, 0, "");// 重新请求密钥
				return;
			}
			if (checkGetVerificationCodeParams(phoneNumber_et.getText().toString())) {
				getCodeRequest();
			}
			break;
		}
	}

	@SuppressWarnings("static-access")
	private void getCodeRequest() {// 获取验证码
		// initProgressDialog();
		if (!NetUtils.isNetConnected(this)) {
			ToastUtils.showShortToast(this, this.getString(R.string.net_not_connected));
			return;
		}
		RequestParams rp = new RequestParams();
		rp.put("mobileNum", keyUtils.AESEncrypt(phoneNumber_et.getText().toString(),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		https.get(this, loadingDialog, Urls.VERIFYCODE_URL, rp, new AsyncHttpResponseHandler());
		// https.get(this, loadingDialog, Urls.VERIFYCODE_URL, rp,
		// new AsyncHttpResponseHandler() {
		// @Override
		// public void onSuccess(String response) {
		// if (!StringUtils.isEmpty(response)) {
		// dismissProgressDialog();
		// isnext = false;
		// LogUtils.i(":response=", response);
		// MsgBean bean = gson.fromJson(response,
		// MsgBean.class);
		// if (StringUtils.isEquals(bean.getState(),
		// Constant.KEY_OVERDUE)) {
		// if (count == 2) {// 设置多次重复请求3次就放弃这一次请求；
		// KeyUtils.cleardata2(PhoneVerifyActivity.this);
		// count = 0;
		// ToastUtils
		// .showShortToast(
		// PhoneVerifyActivity.this,
		// "获取验证码异常");
		// return;
		// }
		// count++;
		// keyUtils.RerequestKey(PhoneVerifyActivity.this,
		// loadingDialog);// 重新请求密钥
		// return;
		// }
		// // if (StringUtils.isEquals("0011",
		// // bean.getState())) {
		// // ToastUtils.showShortToast(
		// // PhoneVerifyActivity.this, "验证码重复获取");
		// // }
		// if (bean != null && bean.isSuccess()) {
		// delayedHandler.postDelayed(runnable, 200);
		// } else {
		// ToastUtils.showShortToast(
		// PhoneVerifyActivity.this, bean.getMsg());
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		// Throwable arg3) {
		// LogUtils.i(":response=", "请求失败");
		// isnext = false;
		// ToastUtils.showShortToast(PhoneVerifyActivity.this,
		// "网络请求错误");
		// dismissProgressDialog();
		// }
		// });
		delayedHandler.postDelayed(runnable, 200);
	}

	@SuppressWarnings("static-access")
	private void VerifyPhoneRequest() {// 验证手机号码
		initProgressDialog();
		RequestParams rp = new RequestParams();
		rp.put("mobileNum", keyUtils.AESEncrypt(phoneNumber_et.getText().toString(),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		rp.put("msgCode", keyUtils.AESEncrypt(auth_code_et.getText().toString(),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		https.get(this, loadingDialog, Urls.VERIFYPHONE_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				if (!StringUtils.isEmpty(response)) {
					dismissProgressDialog();
					LogUtils.i(":response=", response);
					EncryptDataBean bean = gson.fromJson(response, EncryptDataBean.class);

					if (StringUtils.isEquals(bean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(getApplicationContext(), 0, loadingDialog, count, "注册异常");// 重新请求密钥
						return;
					}
					String aeskey = PreferencesUtils.getString(PhoneVerifyActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(bean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					// 解析 判断是不是数组是不是为空,空就显示添加界面
					MsgBean mbean = gson.fromJson(datajson, MsgBean.class);
					if (mbean != null && mbean.isSuccess()) {
						/* 调用方法如下 */
						Intent intent = new Intent(); // 新建Intent
						intent.putExtra("phone", phoneNumber_et.getText().toString());// 传入参数,注册接口需要传入电话号码
						intent.setClass(PhoneVerifyActivity.this, RegisterActivity.class);// 设置调用的Activity
						startActivity(intent);// 显示界面
						// isnext = false;
					} else {
						if (mbean.getMsg() != null) {
							ToastUtils.showShortToast(PhoneVerifyActivity.this, mbean.getMsg());
						} else {
							ToastUtils.showShortToast(PhoneVerifyActivity.this, "验证失败");
						}
						// isnext = false;
					}
					count = 0;
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {

					ToastUtils.showShortToast(getApplicationContext(),
							PhoneVerifyActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(PhoneVerifyActivity.this,
							PhoneVerifyActivity.this.getString(R.string.network_error));
				}
				// isnext = false;
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}
				count = 0;
			}
		});
	}
}
