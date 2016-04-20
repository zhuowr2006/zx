package com.fortunes.zxcx.ui;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.AppConfig;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.secure.FDSpinnerModel;
import com.fortunes.zxcx.ui.bean.MsgBean;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.fortunes.zxcx.view.NotiDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * 授权书界面
 * 
 * @author wgs
 *
 */
public class ShouQuanAcitivity extends BaseActivity implements OnClickListener {
	private Button mLeftIconBtn;
	private Button submit;// 提交&取消
	private EditText et_shouquanma;
	private CheckBox cb_shouquanma, cb_ifSendToOrg;

	private WebView mWebView;// 授权书

	private LinearLayout layout_shouquanma, layout_ifSendToOrg, layout_isread;

	private String shouquanma = "";
	private String ifSendToOrg = "0";
	private String querySendType, queryReason = "0";
	private Spinner mSpinnerQueryType, mSpinnerQueryReason;
	public List<FDSpinnerModel> lstQueryType, lstQueryReason;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_shouquanshu);
		findViewById();
		init();
		setListener();

	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		// button
		submit = (Button) findViewById(R.id.btn_submit);
		mLeftIconBtn = (Button) findViewById(R.id.top_bar_left_btn);
		// EditText
		et_shouquanma = (EditText) findViewById(R.id.et_shouquanma);
		// CheckBox
		cb_shouquanma = (CheckBox) findViewById(R.id.cb_shouquanma);
		cb_ifSendToOrg = (CheckBox) findViewById(R.id.cb_ifSendToOrg);

		// WebView 授权书
		mWebView = (WebView) findViewById(R.id.shouquanshu_webview);
		// LinearLayout
		layout_shouquanma = (LinearLayout) findViewById(R.id.layout_shouquanma);
		layout_ifSendToOrg = (LinearLayout) findViewById(R.id.layout_ifSendToOrg);
		layout_isread = (LinearLayout) findViewById(R.id.layout_isread);
		// Spinner
		mSpinnerQueryReason = (Spinner) findViewById(R.id.spinnerQueryReason);
		mSpinnerQueryType = (Spinner) findViewById(R.id.spinnerQueryType);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		submit.setOnClickListener(this);
		layout_isread.setOnClickListener(this);
		layout_ifSendToOrg.setOnClickListener(this);
		mLeftIconBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appExit();
			}
		});
		cb_shouquanma.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					submit.setEnabled(true);
				} else {
					submit.setEnabled(false);
				}
			}
		});
		cb_ifSendToOrg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					ifSendToOrg = "1";
				} else {
					ifSendToOrg = "0";
				}
			}
		});

		mSpinnerQueryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FDSpinnerModel model = (FDSpinnerModel) ShouQuanAcitivity.this.lstQueryType.get(position);
				if (model != null) {
					querySendType = model.key.toString();
				}

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mSpinnerQueryReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				FDSpinnerModel model = (FDSpinnerModel) ShouQuanAcitivity.this.lstQueryReason.get(position);
				if (model != null) {
					queryReason = model.key.toString();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				SubmitRequest(shouquanma);
			}
		});
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setTopTitle("授权书");
		// setWebView();
		// mWebView.loadUrl("http://baidu.com");
		setQueryType();
		setQueryReason();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit:
			shouquanma = et_shouquanma.getText().toString().trim();
			if (shouquanma.length() == 6) {
				SubmitRequest(shouquanma);
			} else {
				ToastUtils.showShortToast(getApplicationContext(), "授权码长度必须为6");
			}

			break;
		case R.id.layout_ifSendToOrg:
			if (cb_ifSendToOrg.isChecked()) {
				cb_ifSendToOrg.setChecked(false);
			} else {
				cb_ifSendToOrg.setChecked(true);
			}
			LogUtils.i(">>>>", "ifSendToOrg=="+ifSendToOrg);
			break;
		case R.id.layout_isread:
			if (cb_shouquanma.isChecked()) {
				cb_shouquanma.setChecked(false);
			} else {
				cb_shouquanma.setChecked(true);
			}
			break;
		default:
			break;
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void setWebView() {

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				initProgressDialog();
				layout_shouquanma.setVisibility(View.GONE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				dismissProgressDialog();
				layout_shouquanma.setVisibility(View.VISIBLE);
			}

		});

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webSettings.setJavaScriptEnabled(true); // 支持js
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(true);// 支持缩放
		webSettings.setLoadsImagesAutomatically(true); // 支持自动加载图片
	}

	// 退出提示
	private void appExit() {
		final NotiDialog dialog = new NotiDialog(this, "是否退出授权书页面");
		dialog.show();
		dialog.setTitleStr("温馨提示");
		dialog.setOkButtonText("确认");
		dialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				ShouQuanAcitivity.this.finish();
			}
		}).setNegativeListener(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			appExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
	// mWebView.goBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@SuppressWarnings("static-access")
	private void SubmitRequest(final String authorCode) {// 提交授权码
		RequestParams rp = new RequestParams();
		rp.put("certNum", keyUtils.AESEncrypt(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID),
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));// 身份证
		rp.put("name", keyUtils.AESEncrypt(PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_NAME),
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));
		rp.put("authorCode", keyUtils.AESEncrypt(authorCode,
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));
		rp.put("ifSendToOrg", keyUtils.AESEncrypt(ifSendToOrg,
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));
		rp.put("querySendType", keyUtils.AESEncrypt(querySendType,
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));
		rp.put("queryReason", keyUtils.AESEncrypt(queryReason,
				PreferencesUtils.getString(getApplicationContext(), KeyUtils.SERVER_AES_KEY)));
		initProgressDialog();
		https.get(this, loadingDialog, Urls.AUTHORIZATION_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);

					LogUtils.i(">>>>", "authorCode= " + authorCode + "\n" + "ifSendToOrg= " + ifSendToOrg + "\n"
							+ "querySendType= " + querySendType + "\n" + "queryReason= " + queryReason + "\n");
					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);

					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(ShouQuanAcitivity.this, 0,loadingDialog, count, "");// 重新请求密钥
						return;
					}
					if (!StringUtils.isEmpty(eBean.getState())
							&& !StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)
							&& !StringUtils.isEquals(eBean.getState(), Constant.HTTP_SUCCESS) && eBean == null) {
						ToastUtils.showShortToast(getApplicationContext(),
								ShouQuanAcitivity.this.getString(R.string.erro));
						return;
					}

					String aeskey = PreferencesUtils.getString(ShouQuanAcitivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);
					MsgBean mBean = gson.fromJson(datajson, MsgBean.class);
					if (mBean.isSuccess()) {
						ActivityTools.skipActivity(ShouQuanAcitivity.this, ResultAcitivity.class);
						ShouQuanAcitivity.this.finish();
						ToastUtils.showShortToast(getApplicationContext(), mBean.getMsg());
					} else {
						ToastUtils.showShortToast(getApplicationContext(), mBean.getMsg());
					}
					count = 0;
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {

					ToastUtils.showShortToast(getApplicationContext(),
							ShouQuanAcitivity.this.getString(R.string.network_timeout));
				} else {

					ToastUtils.showShortToast(ShouQuanAcitivity.this,
							ShouQuanAcitivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {

					dismissProgressDialog();
				}
				count = 0;
			}
		});
	}

	public void setQueryType() {
		lstQueryType = new ArrayList();
		lstQueryType.add(new FDSpinnerModel("1", "贷前查询"));
		lstQueryType.add(new FDSpinnerModel("2", "贷后查询"));
		lstQueryType.add(new FDSpinnerModel("3", "页面提交的查询"));

		ArrayAdapter adapterCardType = new ArrayAdapter(getApplication(), R.drawable.fd_spinner, (List) lstQueryType);

		mSpinnerQueryType.setAdapter(adapterCardType);
	}

	public void setQueryReason() {
		lstQueryReason = new ArrayList();
		lstQueryReason.add(new FDSpinnerModel("19", "特约商户实名审查"));
		lstQueryReason.add(new FDSpinnerModel("01", "贷后管理"));
		lstQueryReason.add(new FDSpinnerModel("02", "贷前审批"));
		lstQueryReason.add(new FDSpinnerModel("03", "信用卡审批"));
		lstQueryReason.add(new FDSpinnerModel("05", "异议核查"));
		lstQueryReason.add(new FDSpinnerModel("08", "担保资格审查"));
		lstQueryReason.add(new FDSpinnerModel("16", "公积金提取复核"));

		ArrayAdapter adapterCardType = new ArrayAdapter(getApplication(), R.drawable.fd_spinner, (List) lstQueryReason);
		mSpinnerQueryReason.setAdapter(adapterCardType);
	}

}
