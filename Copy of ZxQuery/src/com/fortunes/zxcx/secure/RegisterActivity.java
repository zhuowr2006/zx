package com.fortunes.zxcx.secure;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.ui.MainActivity;
import com.fortunes.zxcx.ui.bean.MsgBean;
import com.fortunes.zxcx.ui.bean.UserBean;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName: RegisterActivity
 * @Description:帐号注册
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	public String mCaptcha;
	public String mPhone;
	public String usernameMsg = "用户名必须由6-16位字符组成，可包含字母和数字、'-'和'_'（首位必须为字母）";
	public String passwordMsg = "密码由8-16个字母数字加特殊符号(`~!@#$%^&*()_-+=:.)组成，区分大小写，不含空格";
	public String idCardMsg = "请输入18位身份证号";
	public List<FDSpinnerModel> lstCardType;
	private Button mButtonDone;
	private EditText mEditTextName, mEditTextCardNo, mEditTextUserName, mEditTextPassword, mEditTextPasswordAgain,
			mEditTextCaptcha;
	private Spinner mSpinnerCardType;
	private ImageView imageView;

	private CheckBox ck;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_register);
		findViewById();
		init();
		setListener();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnDone:
			if (!KeyUtils.isKeyExchange(this)) {// 如果没有AES密钥就重新交互一次
				initProgressDialog();
				keyUtils.RerequestKey(this, 0, loadingDialog, 0, "");// 重新请求密钥
				return;
			}
			Map items = RegisterActivity.this.getItemsValue();
			boolean flag = RegisterActivity.this.validateItemAndDisplayMessage(items);
			if (flag) {
				RegisterRequest();
			}
			break;

		case R.id.imageCaptcha:// 验证码
			imageView.setImageBitmap(FDCaptcha.getInstance().createBitmap());
			mCaptcha = FDCaptcha.getInstance().getCode();
			break;
		case R.id.text:
			Uri uri = Uri.parse("https://ipcrs.pbccrc.org.cn/html/servearticle.html");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
			break;
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mButtonDone = (Button) findViewById(R.id.btnDone);
		mEditTextName = (EditText) findViewById(R.id.txtName);
		mSpinnerCardType = (Spinner) findViewById(R.id.spinnerCardType);
		mEditTextCardNo = (EditText) findViewById(R.id.txtCardNo);
		mEditTextUserName = (EditText) findViewById(R.id.txtUserName);
		mEditTextPassword = (EditText) findViewById(R.id.txtPassword);
		mEditTextPasswordAgain = (EditText) findViewById(R.id.txtPasswordAgain);
		mEditTextCaptcha = (EditText) findViewById(R.id.txtCaptcha);
		imageView = (ImageView) findViewById(R.id.imageCaptcha);
		imageView.setImageBitmap(FDCaptcha.getInstance().createBitmap());
		mCaptcha = FDCaptcha.getInstance().getCode();

		ck = (CheckBox) findViewById(R.id.checkbox);
		text = (TextView) findViewById(R.id.text);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mButtonDone.setOnClickListener(this);
		imageView.setOnClickListener(this);
		text.setOnClickListener(this);

		mEditTextCardNo.setTransformationMethod(new InputLowerToUpper());
		mEditTextCardNo.addTextChangedListener(new FDUserNameTextLengthWatcher(20, "证件号码最多20位", getApplication()));
		mEditTextUserName.addTextChangedListener(new FDUserNameTextLengthWatcher(16, "用户名最多20个字符", getApplication()));

		mEditTextPassword.addTextChangedListener(new FDUserNameTextLengthWatcher(16, "密码最多16个字符", getApplication()));

		mEditTextPasswordAgain
				.addTextChangedListener(new FDUserNameTextLengthWatcher(16, "密码最多16个字符", getApplication()));
		mSpinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FDSpinnerModel model = (FDSpinnerModel) RegisterActivity.this.lstCardType.get(position);
				if ((model != null) && (model.key.equals("1"))) {
					ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.idCardMsg);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mEditTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (mEditTextUserName.getText().toString().length() < 6)
						ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.usernameMsg);
					if (mEditTextUserName.getText().toString().length() >= 6) {
						if (mEditTextUserName.getText().toString().length() <= 16) {
							boolean flag = FDUtility.stringMatch("^[a-zA-Z]{1}[a-zA-Z0-9_\\-]*$",
									mEditTextUserName.getText().toString());
							if (!flag)
								ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.usernameMsg);
						}
					}
				}
			}
		});
		mEditTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (mEditTextPassword.getText().toString().length() < 8)
						ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.passwordMsg);
					if (mEditTextPassword.getText().toString().length() >= 8) {
						if (mEditTextPassword.getText().toString().length() <= 16) {
							boolean flag = FDUtility.stringMatch(
									"^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
									mEditTextPassword.getText().toString());
							if (!flag)
								ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.passwordMsg);
						}
					}
				}
			}
		});
		mEditTextPasswordAgain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (mEditTextPasswordAgain.getText().toString().length() < 8) {
						ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.passwordMsg);
					}
					if (mEditTextPasswordAgain.getText().toString().length() >= 8) {
						if (mEditTextPasswordAgain.getText().toString().length() <= 16) {
							boolean flag = FDUtility.stringMatch(
									"^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
									mEditTextPasswordAgain.getText().toString());
							if (!flag)
								ToastUtils.showShortToast(getApplicationContext(), RegisterActivity.this.passwordMsg);
						}
					}
				}
			}
		});
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				Map items = RegisterActivity.this.getItemsValue();
				boolean flag = RegisterActivity.this.validateItemAndDisplayMessage(items);
				if (!flag) {
					return;
				}
				if (StringUtils.isEquals(go, "register")) {
					RegisterRequest();
				} else {
					LoginRequest();
				}
			}
		});
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setTopTitle("注册");
		Intent from = getIntent();
		mPhone = from.getStringExtra("phone");
		lstCardType = new ArrayList();
		lstCardType.add(new FDSpinnerModel("0", "身份证"));
		lstCardType.add(new FDSpinnerModel("1", "户口簿"));
		lstCardType.add(new FDSpinnerModel("2", "护照"));
		lstCardType.add(new FDSpinnerModel("3", "军官证"));
		lstCardType.add(new FDSpinnerModel("4", "士兵证"));
		lstCardType.add(new FDSpinnerModel("5", "港澳居民来往内地通行证"));
		lstCardType.add(new FDSpinnerModel("6", "台湾同胞来往内地通行证"));
		lstCardType.add(new FDSpinnerModel("7", "临时身份证"));
		lstCardType.add(new FDSpinnerModel("8", "外国人居留证"));
		lstCardType.add(new FDSpinnerModel("9", "警官证"));
		lstCardType.add(new FDSpinnerModel("A", "香港身份证"));
		lstCardType.add(new FDSpinnerModel("B", "澳门身份证"));
		lstCardType.add(new FDSpinnerModel("C", "台湾身份证"));
		lstCardType.add(new FDSpinnerModel("X", "其他证件"));

		ArrayAdapter adapterCardType = new ArrayAdapter(getApplication(), R.drawable.fd_spinner, (List) lstCardType);

		mSpinnerCardType.setAdapter(adapterCardType);
	}

	public Map<String, String> getItemsValue() {
		Map values = new LinkedHashMap();

		// EditText mEditTextUserName = (EditText)
		// findViewById(R.id.txtUserName);
		// values.put("UserName", mEditTextUserName.getText().toString());

		values.put("Password", mEditTextPassword.getText().toString());

		values.put("PasswordAgain", mEditTextPasswordAgain.getText().toString());

		values.put("Name", mEditTextName.getText().toString());

		FDSpinnerModel model = (FDSpinnerModel) mSpinnerCardType.getSelectedItem();
		values.put("CardType", model.getKey());

		values.put("CardNo", mEditTextCardNo.getText().toString().toUpperCase(Locale.getDefault()));

		values.put("Captcha", mEditTextCaptcha.getText().toString());

		return values;
	}

	public boolean validateItemAndDisplayMessage(Map<String, String> items) {// 检查条件
		String value = (String) items.get("Captcha");
		String msg = "";
		if (FDUtility.isEmpty(value)) {
			msg = "验证码为空";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!value.equals(this.mCaptcha)) {
			msg = "验证码不正确";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		// value = (String) items.get("UserName");
		// if (FDUtility.isEmpty(value)) {
		// msg = this.usernameMsg;
		// Toast.makeText(getApplication(), msg, 0).show();
		// return false;
		// }
		//
		// if (value.length() < 6) {
		// msg = this.usernameMsg;
		// Toast.makeText(getApplication(), msg, 0).show();
		// return false;
		// }
		//
		// if (value.length() > 20) {
		// msg = this.usernameMsg;
		// Toast.makeText(getApplication(), msg, 0).show();
		// return false;
		// }
		//
		// if (!FDUtility.stringMatch("^[a-zA-Z]{1}[a-zA-Z0-9_\\-]*$", value)) {
		// msg = this.usernameMsg;
		// Toast.makeText(getApplication(), msg, 0).show();
		// return false;
		// }

		value = (String) items.get("Password");
		if (FDUtility.isEmpty(value)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (value.length() < 8) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (value.length() > 16) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!FDUtility.stringMatch("^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
				value)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		value = (String) items.get("PasswordAgain");
		if (FDUtility.isEmpty(value)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!FDUtility.stringMatch("^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
				value)) {
			msg = this.passwordMsg;
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		if (!((String) items.get("Password")).equals(items.get("PasswordAgain"))) {
			msg = "两次密码不相同";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		value = (String) items.get("Name");
		if (FDUtility.isEmpty(value)) {
			msg = "姓名为空";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}

		value = (String) items.get("CardNo");
		if (value.length() != 18) {
			ToastUtils.showShortToast(getApplicationContext(), this.idCardMsg);
			return false;
		}

		value = (String) items.get("CardNo");
		if (FDUtility.isEmpty(value)) {
			msg = "证件号码为空";
			ToastUtils.showShortToast(getApplicationContext(), msg);
			return false;
		}
		if (ck.isChecked() == false) {
			showShortToast("请阅读须知");
			return false;
		}

		return true;
	}

	@SuppressWarnings("static-access")
	private void RegisterRequest() {// 注册请求
		Map items = RegisterActivity.this.getItemsValue();
		RequestParams rp = new RequestParams();
		rp.put("name", keyUtils.AESEncrypt((String) items.get("Name"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		rp.put("mobileNum", keyUtils.AESEncrypt(mPhone, PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		//
		rp.put("certType", keyUtils.RSAEncrypt((String) items.get("CardType"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		rp.put("credNum", keyUtils.RSAEncrypt((String) items.get("CardNo"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		rp.put("userPassWord", keyUtils.RSAEncrypt((String) items.get("Password"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		initProgressDialog();
		https.get(this, loadingDialog, Urls.REGISTRATION_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);
					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);
					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(RegisterActivity.this, 0, loadingDialog, count, "register",
								"注册异常");// 重新请求密钥
						return;
					}
					String aeskey = PreferencesUtils.getString(RegisterActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					// 解析 判断是不是数组是不是为空,空就显示添加界面
					MsgBean bean = gson.fromJson(datajson, MsgBean.class);
					count = 0;
					if (bean.isSuccess()) {
						ToastUtils.showShortToast(RegisterActivity.this, "注册成功，准备登录");
						LoginRequest();
					} else {
						ToastUtils.showShortToast(RegisterActivity.this, bean.getMsg());
					}
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(getApplicationContext(),
							RegisterActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(getApplicationContext(),
							RegisterActivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}
				count = 0;
			}
		});
	}

	@SuppressWarnings("static-access")
	private void LoginRequest() {// 登录请求
		Map items = RegisterActivity.this.getItemsValue();
		RequestParams rp = new RequestParams();
		rp.put("credNum", keyUtils.RSAEncrypt((String) items.get("CardNo"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		rp.put("certType", keyUtils.RSAEncrypt((String) items.get("CardType"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		rp.put("userPassWord", keyUtils.RSAEncrypt((String) items.get("Password"),
				PreferencesUtils.getString(this, KeyUtils.SERVER_PUBLIC_KEY)));
		initProgressDialog();
		https.get(this, loadingDialog, Urls.LOGIN_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);
					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);
					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(RegisterActivity.this, 0, loadingDialog, count, "login", "登录异常");// 重新请求密钥
						return;
					}
					String aeskey = PreferencesUtils.getString(RegisterActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					// 解析 判断是不是数组是不是为空,空就显示添加界面
					UserBean bean = gson.fromJson(datajson, UserBean.class);
					count = 0;
					if (bean.isSuccess()) {
						ToastUtils.showShortToast(RegisterActivity.this, "登录成功");
						PreferencesUtils.putUserData(RegisterActivity.this, bean);
						ActivityTools.skipActivityFinish(RegisterActivity.this, MainActivity.class);
					} else {
						ToastUtils.showShortToast(RegisterActivity.this, bean.getMsg());
					}
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(getApplicationContext(),
							RegisterActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(getApplicationContext(),
							RegisterActivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}
				count = 0;
			}
		});
	}
}