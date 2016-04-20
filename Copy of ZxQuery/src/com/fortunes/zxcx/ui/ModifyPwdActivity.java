package com.fortunes.zxcx.ui;

import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.fortunes.zxcx.secure.FDUserNameTextLengthWatcher;
import com.fortunes.zxcx.secure.FDUtility;
import com.fortunes.zxcx.ui.bean.MsgBean;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 修改密码界面
 * 
 * @author wdd
 * 
 */
public class ModifyPwdActivity extends BaseActivity {

	private String passwordMsg = "密码必须由8-16个字母数字加特殊符号(`~!@#$%^&*()_-+=:.)组成，区分大小写，不含空格";
	private String pwd, rePwd;

	private TextView modify_id_type, modify_id_number;
	private EditText modify_pwd, modify_repwd;
	private Button modify_next;
	private String ReportStatus = "";

	@Override
	protected void findViewById() {
		modify_id_type = (TextView) this.findViewById(R.id.modify_id_type);
		modify_id_number = (TextView) this.findViewById(R.id.modify_id_number);
		modify_pwd = (EditText) this.findViewById(R.id.modify_pwd);
		modify_repwd = (EditText) this.findViewById(R.id.modify_repwd);
		modify_next = (Button) this.findViewById(R.id.modify_next);
	}

	@Override
	protected void setListener() {
		modify_pwd.addTextChangedListener(new FDUserNameTextLengthWatcher(16, "密码最多16个字符", getApplication()));
		modify_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (modify_pwd.getText().toString().length() < 8)
						ToastUtils.showShortToast(getApplicationContext(), ModifyPwdActivity.this.passwordMsg);
					if ((modify_pwd.getText().toString().length() >= 8)
							&& (modify_pwd.getText().toString().length() <= 16)) {
						boolean flag = FDUtility.stringMatch(
								"^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
								modify_pwd.getText().toString());
						if (!flag)
							ToastUtils.showShortToast(getApplicationContext(), ModifyPwdActivity.this.passwordMsg);
					}
				}
			}

		});

		modify_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pwd = modify_pwd.getText().toString().trim();
				rePwd = modify_repwd.getText().toString().trim();
				boolean flag=validateItemAndDisplayMessage(pwd, rePwd);
				if (flag) {
					if (!KeyUtils.isKeyExchange2(ModifyPwdActivity.this)) {// 如果没有AES密钥就重新交互一次
						initProgressDialog();
						keyUtils.RerequestKey(ModifyPwdActivity.this, 1, loadingDialog, 0, "");// 重新请求密钥
						return;
					}
					ModifyRequest();
				}
			}
		});
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				ModifyRequest();

			}
		});
	}

	public boolean validateItemAndDisplayMessage(String pwd, String rePwd) {
		String msg = "";

		if (FDUtility.isEmpty(pwd)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (pwd.length() < 8) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}
		if (pwd.length() > 16) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!FDUtility.stringMatch("^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$", pwd)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!rePwd.equals(pwd)) {
			msg = "两次输入密码不一致，请重新输入！";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		return true;
	}

	@Override
	protected void init() {
		setTopTitle("修改密码");
		modify_id_number.setText(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID));
		modify_id_type.setText("身份证");
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			ReportStatus = bundle.getString("ReportStatus");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_modify_pwd);
		findViewById();
		init();
		setListener();
	}

	@SuppressWarnings("static-access")
	private void ModifyRequest() {// 强制修改密码请求
		RequestParams rp = new RequestParams();
		rp.put("certNum", keyUtils.RSAEncrypt(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		rp.put("certType", keyUtils.RSAEncrypt("0", PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));

		rp.put("pwd", keyUtils.RSAEncrypt(pwd, PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));

		rp.put("pwdComfirm", keyUtils.RSAEncrypt(rePwd, PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		initProgressDialog();
		https.get(ModifyPwdActivity.this, loadingDialog, Urls.MODIFYPWD_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);

					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);
					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(ModifyPwdActivity.this, 1, loadingDialog, count, "");// 重新请求密钥
						return;
					}
					if (StringUtils.isEquals(eBean.getState(), Constant.HTTP_SUCCESS)) {
						String aeskey = PreferencesUtils.getString(ModifyPwdActivity.this, KeyUtils.SERVER_AES_KEY);
						String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
						LogUtils.i(":AESdecryptResponse=", datajson);

						MsgBean bean = gson.fromJson(datajson, MsgBean.class);
						Bundle bundle = new Bundle();
						if (bean.isSuccess()) {
							bundle.putString("ReportStatus", ReportStatus);
							ActivityTools.skipActivity(ModifyPwdActivity.this, CheckCreditReportActivity.class, bundle);
							ModifyPwdActivity.this.finish();
							LogUtils.i(">>>>", bean.getMsg());
						} else {
							ToastUtils.showLongToast(ModifyPwdActivity.this, bean.getMsg());
						}
						count = 0;
					} else {
						ToastUtils.showShortToast(getApplicationContext(), "请求异常，请重新请求！");
					}

				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {

					ToastUtils.showShortToast(getApplicationContext(),
							ModifyPwdActivity.this.getString(R.string.network_timeout));
				}
				ToastUtils.showShortToast(getApplicationContext(),
						ModifyPwdActivity.this.getString(R.string.network_error));
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}
				count = 0;
			}
		});
	}

}
