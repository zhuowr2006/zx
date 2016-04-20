package com.fortunes.zxcx.secure;

import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 忘记密码
 * 
 * @author wdd
 * 
 */
public class ForgetPwdActivity extends BaseActivity implements OnClickListener {
	private Button forget_next;
	private EditText identity_name, identity_number, identity_phone;
	private String name, certNum, mobileNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_forget_pwd);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		identity_name = (EditText) this.findViewById(R.id.identity_name);
		identity_number = (EditText) this.findViewById(R.id.identity_name);
		identity_phone = (EditText) this.findViewById(R.id.identity_name);
		forget_next = (Button) this.findViewById(R.id.forget_next);
	}

	@Override
	protected void setListener() {
		forget_next.setOnClickListener(new MyForgetListener());
	}

	class MyForgetListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			name = identity_name.getText().toString().trim();
			certNum = identity_number.getText().toString().trim();
			mobileNum = identity_phone.getText().toString().trim();
			getPwdRequest();
		}
	}

	@Override
	protected void init() {
		setTopTitle("忘记密码");
	}

	private void getPwdRequest() {// 获取授权码，启动银联控件

		RequestParams rp = new RequestParams();
		rp.put("certNum", certNum);
		rp.put("mobileNum", mobileNum);
		initProgressDialog();
		https.get(ForgetPwdActivity.this, loadingDialog, Urls.FORGETPWD_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);
					// JobGradeBean bean = gson.fromJson(response,
					// JobGradeBean.class);
					/*
					 * if (bean.getData() != null && bean.getData().size() > 0)
					 * {
					 * 
					 * }
					 */
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(getApplicationContext(),
							ForgetPwdActivity.this.getString(R.string.network_timeout));

				} else {
					ToastUtils.showShortToast(getApplicationContext(),
							ForgetPwdActivity.this.getString(R.string.network_error));

				}
				dismissProgressDialog();
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

}
