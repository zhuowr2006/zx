package com.fortunes.zxcx.secure;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.ui.MainActivity;
import com.fortunes.zxcx.ui.bean.UserBean;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
/**
 * 登录界面
 * 
 * @author zwr
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private String mCaptcha;
	// private String usernameMsg = "用户名必须由6-16位字符组成，可包含字母和数字、'-'和'_'（首位必须为字母）";
	private String passwordMsg = "密码由8-16个字母数字加特殊符号(`~!@#$%^&*()_-+=:.)组成，区分大小写，不含空格";
	// "^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$"
	private Button mBtnLogin;
	private EditText mEditTextCardNo, mEditTextPassword, mEditTextCaptcha;
	private ImageView imageView;
	private TextView tv, forgetPwd;
	public String idCardMsg = "请输入18位身份证号";
	public List<FDSpinnerModel> lstCardType;
	private Spinner mSpinnerCardType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_login);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mBtnLogin = (Button) findViewById(R.id.btnLogin);
		mSpinnerCardType = (Spinner) findViewById(R.id.spinnerCardType);
		mEditTextCardNo = (EditText) findViewById(R.id.txtCardNo);
		mEditTextPassword = (EditText) findViewById(R.id.txtLoginPassword);
		mEditTextCaptcha = (EditText) findViewById(R.id.txtLoginCaptcha);
		imageView = (ImageView) findViewById(R.id.imageLoginCaptcha);
		forgetPwd = (TextView) findViewById(R.id.forgetPwd);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		mBtnLogin.setOnClickListener(this);
		imageView.setOnClickListener(this);
//		forgetPwd.setOnClickListener(this);
		mEditTextCardNo.setTransformationMethod(new InputLowerToUpper());
		mEditTextCardNo.addTextChangedListener(new FDUserNameTextLengthWatcher(20, "证件号码最多20位", getApplication()));
		mEditTextPassword.addTextChangedListener(new FDUserNameTextLengthWatcher(16, "密码最多16个字符", getApplication()));
		mEditTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (mEditTextPassword.getText().toString().length() < 8)
						ToastUtils.showShortToast(getApplicationContext(), LoginActivity.this.passwordMsg);
					if ((mEditTextPassword.getText().toString().length() >= 8)
							&& (mEditTextPassword.getText().toString().length() <= 16)) {
						boolean flag = FDUtility.stringMatch(
								"^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
								mEditTextPassword.getText().toString());
						if (!flag)
							ToastUtils.showShortToast(getApplicationContext(), LoginActivity.this.passwordMsg);
					}
				}
			}

		});
		mSpinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FDSpinnerModel model = (FDSpinnerModel) LoginActivity.this.lstCardType.get(position);
				if ((model != null) && (model.key.equals("1"))) {
					ToastUtils.showShortToast(getApplicationContext(), LoginActivity.this.idCardMsg);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				Map items = LoginActivity.this.getItemsValue();
				boolean flag = LoginActivity.this.validateItemAndDisplayMessage(items);
				if (!flag) {
					return;
				}
				LoginRequest();

			}
		});
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setTopTitle("登录");
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		String username = userInfo.getString("userName", "");
		imageView.setImageBitmap(FDCaptcha.getInstance().createBitmap());
		mCaptcha = FDCaptcha.getInstance().getCode();
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
		FDSpinnerModel model = (FDSpinnerModel) mSpinnerCardType.getSelectedItem();
		values.put("CardType", model.getKey());

		values.put("CardNo", mEditTextCardNo.getText().toString().toUpperCase(Locale.getDefault()));

		values.put("Password", mEditTextPassword.getText().toString());

		values.put("Captcha", mEditTextCaptcha.getText().toString());
		return values;
	}

	public boolean validateItemAndDisplayMessage(Map<String, String> items) {//检查条件
		String value = (String) items.get("Password");
		
		String msg = "";
		if (FDUtility.isEmpty(value)) {
			msg = this.passwordMsg;
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}

		if (value.length() < 8) {
			msg = this.passwordMsg;
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}
		if (value.length() > 16) {
			msg = this.passwordMsg;
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}

		if (!FDUtility.stringMatch("^(?=.*?[a-zA-Z])(?=.*?\\d)(?=.*?[`~!@#$%^&*()_\\-+=:.])(?!.*?\\s).{8,16}$",
				value)) {
			msg = this.passwordMsg;
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}
		value = (String) items.get("Captcha");
		if (FDUtility.isEmpty(value)) {
			msg = "验证码为空";
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}

		if (!value.equals(this.mCaptcha)) {
			msg = "验证码不正确";
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}

		value = (String) items.get("CardNo");
		if (value.length() != 18) {
			Toast.makeText(getApplication(), this.idCardMsg, 0).show();
			return false;
		}

		value = (String) items.get("CardNo");
		if (FDUtility.isEmpty(value)) {
			msg = "证件号码为空";
			Toast.makeText(getApplication(), msg, 0).show();
			return false;
		}
		
	
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		userInfo.edit().putString("userName", (String) items.get("UserName")).commit();

		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnLogin:// 登录
			// ActivityTools.skipActivity(LoginActivity.this,
			// CreditReportActivity.class);

			Map items = LoginActivity.this.getItemsValue();
			boolean flag = LoginActivity.this.validateItemAndDisplayMessage(items);
			if (flag) {
				if (!KeyUtils.isKeyExchange(this)) {// 如果没有AES密钥就重新交互一次
					initProgressDialog();
					keyUtils.RerequestKey(this, 0, loadingDialog, 0, "");// 重新请求密钥
					return;
				}
				LoginRequest();
			}
			break;
		case R.id.imageLoginCaptcha://验证码
			imageView.setImageBitmap(FDCaptcha.getInstance().createBitmap());
			LoginActivity.this.mCaptcha = FDCaptcha.getInstance().getCode();
			break;
		case R.id.forgetPwd://修改密码
			ActivityTools.skipActivity(LoginActivity.this, ForgetPwdActivity.class);
			break;
		}
	}

	private void LoginRequest() {// 登录请求
		Map items = LoginActivity.this.getItemsValue();
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
						count = keyUtils.RerequestKey(LoginActivity.this, 0, loadingDialog, count, "登录异常");// 重新请求密钥
						return;
					}
					String aeskey = PreferencesUtils.getString(LoginActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					// 解析 判断是不是数组是不是为空,空就显示添加界面
					UserBean bean = gson.fromJson(datajson, UserBean.class);
					if (bean.isSuccess()) {
						ToastUtils.showShortToast(LoginActivity.this, "登录成功");
						PreferencesUtils.putUserData(LoginActivity.this, bean);
						ActivityTools.skipActivity(LoginActivity.this, MainActivity.class);
					} else {
						ToastUtils.showShortToast(LoginActivity.this, bean.getMsg());
					}
				}
				count = 0;
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(LoginActivity.this,
							LoginActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(LoginActivity.this, LoginActivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}

				count = 0;
			}
		});
	}
}
