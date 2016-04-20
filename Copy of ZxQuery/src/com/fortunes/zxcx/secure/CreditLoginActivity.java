package com.fortunes.zxcx.secure;

import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.AppConfig;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.ui.CheckCreditReportActivity;
import com.fortunes.zxcx.ui.ModifyPwdActivity;
import com.fortunes.zxcx.ui.bean.CreditLoginBean;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 征信报告登录
 * 
 * @author wdd
 * 
 */
public class CreditLoginActivity extends BaseActivity implements OnClickListener{

	private TextView creditCardType;
	private EditText creditCardNo,creditLoginPwd, creditLoginCaptcha;
	private Button creditBtnLogin;
	private ImageView creditImageCaptcha;

	private String passwordMsg = "密码必须由8-16个字符组成";
	private String cardNo, cardType, pwd;
	private String mCaptcha, inCaptcha;// 验证码

	@Override
	protected void findViewById() {
		creditCardNo = (EditText) this.findViewById(R.id.creditCardNo);
		creditCardType = (TextView) this.findViewById(R.id.creditCardType);
		creditLoginPwd = (EditText) this.findViewById(R.id.creditLoginPwd);
		creditLoginCaptcha = (EditText) this.findViewById(R.id.creditLoginCaptcha);
		creditBtnLogin = (Button) this.findViewById(R.id.creditBtnLogin);
		creditImageCaptcha = (ImageView) this.findViewById(R.id.creditImageCaptcha);
	}

	@Override
	protected void setListener() {
		creditBtnLogin.setOnClickListener(this);
		creditImageCaptcha.setOnClickListener(this);
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				CreditLoginRequest();
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.creditBtnLogin:// 登录
			pwd = creditLoginPwd.getText().toString().trim();
			inCaptcha = creditLoginCaptcha.getText().toString().trim();
			boolean flag=validateItemAndDisplayMessage(creditCardNo.getText().toString(),inCaptcha, pwd);
			if (flag) {
				if (!KeyUtils.isKeyExchange2(this)) {// 如果没有AES密钥就重新交互一次
					initProgressDialog();
					keyUtils.RerequestKey(this, 1, loadingDialog, 0, "");// 重新请求密钥
					return;
				}
				CreditLoginRequest();
			}
			break;
		case R.id.creditImageCaptcha://验证码
			creditImageCaptcha.setImageBitmap(FDCaptcha.getInstance().createBitmap());
			CreditLoginActivity.this.mCaptcha = FDCaptcha.getInstance().getCode();
			break;
		}
	}
	

	public boolean validateItemAndDisplayMessage(String id,String inCaptcha, String pwd) {
		String msg = "";
		if (FDUtility.isEmpty(id)) {
			msg = "证件号为空";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}
		if (FDUtility.isEmpty(inCaptcha)) {
			msg = "验证码为空";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!inCaptcha.equals(this.mCaptcha)) {
			msg = "验证码不正确";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (FDUtility.isEmpty(pwd)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (pwd.length() < 6) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}
		if (pwd.length() > 16) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		return true;
	}

	@Override
	protected void init() {
		setTopTitle("查询征信报告登录");
		creditImageCaptcha.setImageBitmap(FDCaptcha.getInstance().createBitmap());
		mCaptcha = FDCaptcha.getInstance().getCode();
		creditCardNo.setText(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID));
		creditCardType.setText("身份证");

		// creditCardNo.setText(PreferencesUtils
		// .getString(this, AppConfig.USER_ID));
		//
		// String type=PreferencesUtils.getString(this,AppConfig.USER_ID_TYPE);
		// if("0".equals(type)){
		// creditCardType.setText("身份证");
		// }else if("1".equals(type)){
		// creditCardType.setText("户口簿");
		// }else if("2".equals(type)){
		// creditCardType.setText("护照");
		// }else if("3".equals(type)){
		// creditCardType.setText("军官证");
		// }else if("4".equals(type)){
		// creditCardType.setText("士兵证");
		// }else if("5".equals(type)){
		// creditCardType.setText("港澳居民来往内地通行证");
		// }else if("6".equals(type)){
		// creditCardType.setText("台湾同胞来往内地通行证");
		// }else if("7".equals(type)){
		// creditCardType.setText("临时身份证");
		// }else if("8".equals(type)){
		// creditCardType.setText("外国人居留证");
		// }else if("9".equals(type)){
		// creditCardType.setText("警官证");
		// }else if("A".equals(type)){
		// creditCardType.setText("香港身份证");
		// }else if("B".equals(type)){
		// creditCardType.setText("澳门身份证");
		// }else if("C".equals(type)){
		// creditCardType.setText("台湾身份证");
		// }else if("X".equals(type)){
		// creditCardType.setText("其他证件");
		// }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_credit_login);
		findViewById();
		init();
		setListener();
	}

	@SuppressWarnings("static-access")
	private void CreditLoginRequest() {// 征信报告登录请求
		RequestParams rp = new RequestParams();
		rp.put("certNum", keyUtils.RSAEncrypt(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		// PreferencesUtils.getString(this, AppConfig.USER_ID)
		rp.put("certType", keyUtils.RSAEncrypt("0", PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		// PreferencesUtils.getString(this, AppConfig.USER_ID_TYPE)
		rp.put("authorCode", keyUtils.RSAEncrypt(pwd, PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));

		initProgressDialog();
		https.get(CreditLoginActivity.this, loadingDialog, Urls.CREDITLOGIN_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);

					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);
					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(CreditLoginActivity.this, 1, loadingDialog, count, "登录异常");// 重新请求密钥
						return;
					}
					String aeskey = PreferencesUtils.getString(CreditLoginActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					CreditLoginBean bean = gson.fromJson(datajson, CreditLoginBean.class);
					Bundle bundle = new Bundle();
					if (bean.isSuccess()) {
						LogUtils.i(">>>>", bean.getState() + "是否存在报告=" + bean.getReportStatus());
						if ("0".equals(bean.getPwdChange())) {

							bundle.putString("ReportStatus", bean.getReportStatus());
							ActivityTools.skipActivity(CreditLoginActivity.this, ModifyPwdActivity.class, bundle);

							CreditLoginActivity.this.finish();
						} else {
							bundle.putString("ReportStatus", bean.getReportStatus());
							ActivityTools.skipActivity(CreditLoginActivity.this, CheckCreditReportActivity.class,
									bundle);
							CreditLoginActivity.this.finish();

						}
					} else {
						ToastUtils.showLongToast(CreditLoginActivity.this, bean.getMsg());
						LogUtils.i(">>>>", bean.getState());
					}

				}

				count = 0;
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(CreditLoginActivity.this,
							CreditLoginActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(CreditLoginActivity.this,
							CreditLoginActivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}

				count = 0;
			}
		});
	}

}
