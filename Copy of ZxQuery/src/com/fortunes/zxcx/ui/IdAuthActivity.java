package com.fortunes.zxcx.ui;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.AppConfig;
import com.fortunes.zxcx.config.Constant;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.keys.KeyUtils.RequestKeyListener;
import com.fortunes.zxcx.keys.bean.EncryptDataBean;
import com.fortunes.zxcx.ui.bean.AuthCodeBean;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.fortunes.zxcx.view.NotiDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 身份认证界面
 * 
 * @author wdd
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class IdAuthActivity extends BaseActivity {
	private WebView mWebView;
	private Handler mHandler = new Handler();
	ProgressBar bar;
	private Button mLeftIconBtn;
	/*** 网络请求失败或无数据 显示的布局 **/
	private RelativeLayout refresh;
	private LinearLayout layout_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_identity_auth);

		findViewById();
		init();
		setListener();
	}

	final class DemoJavaScriptInterface {

		DemoJavaScriptInterface() {
		}

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */
		public void clickOnAndroid() {
			mHandler.post(new Runnable() {
				public void run() {
					mWebView.loadUrl("javascript:wave()");
				}
			});

		}
	}

	private String createWebForm() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>").append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
		sb.append("<title>").append("表单测试").append("</title>");
		sb.append("</head><script language=\"javascript\">");
		sb.append("function checkform(){var username=document.loginForm.username.value;");
		sb.append("var password=document.loginForm.password.value;");
		sb.append("if(username==\"\"){alert(\"用户名不能为空！\"); return false;}");
		sb.append("if(password==\"\"){alert(\"密码不能为空！\"); return false;}");
		// sb.append("return username + \":\" + password;");
		sb.append("window.loginImpl.login(username, password)");
		sb.append("}");
		sb.append("</script>");
		sb.append("<body>");
		sb.append("<form method=\"post\" name=\"loginForm\">");
		sb.append("<table>");

		sb.append("<tr>");
		sb.append("<td align=\"right\">").append("用户名").append("</td>");
		sb.append("<td>").append("<input type=\"text\" name=\"username\" />").append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td align=\"right\">").append("密    码").append("</td>");
		sb.append("<td>").append("<input type=\"password\" name=\"password\" />").append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td align=\"center\" colspan=\"2\">");
		sb.append("<input type=\"submit\" value=\"登录\" onclick=\"checkform();\">");
		sb.append("&nbsp;&nbsp;<input type=\"reset\" value=\"重置\">").append("</td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</form>");
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	@Override
	protected void findViewById() {
		mWebView = (WebView) findViewById(R.id.identity_start);
		bar = (ProgressBar) findViewById(R.id.myProgressBar);
		mLeftIconBtn = (Button) findViewById(R.id.top_bar_left_btn);
		refresh = (RelativeLayout) findViewById(R.id.refreshdata);
		layout_data = (LinearLayout) findViewById(R.id.layout_data);
	}

	@Override
	protected void setListener() {
		mLeftIconBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appExit();
			}
		});
		keyUtils.setOnRequestKeyListener(new RequestKeyListener() {

			@Override
			public void callBack(String go) {
				// TODO Auto-generated method stub
				authorCodeRequest();
			}
		});
		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				authorCodeRequest();
			}
		});
	}

	// 退出提示
	private void appExit() {
		final NotiDialog dialog = new NotiDialog(this, "是否退出银联身份验证");
		dialog.show();
		dialog.setTitleStr("温馨提示");
		dialog.setOkButtonText("确认");
		dialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				IdAuthActivity.this.finish();
			}
		}).setNegativeListener(null);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("JavascriptInterface")
	@Override
	protected void init() {
		setTopTitle("申请身份认证");
		WebSettings webSettings = mWebView.getSettings();
		// 不保存密码
		webSettings.setSavePassword(false);
		// 不保存表单数据
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		// 不支持页面放大功能
		webSettings.setSupportZoom(false);
		// 设置Web视图
		mWebView.setWebViewClient(new MyWebViewClient());

		mWebView.addJavascriptInterface(new LoginJavaScriptImpl(), "loginImpl");
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					bar.setVisibility(View.INVISIBLE);
				} else {
					if (View.INVISIBLE == bar.getVisibility()) {
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}

		});
		authorCodeRequest();
	}

	// 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.contains("https://mcashier.95516.com/mobile/verify/callback.action")) {
				final NotiDialog dialog = new NotiDialog(IdAuthActivity.this,
						"请您谨记授权码，签写授权书时需要填写授权码！此码在您登录查询征信报告的时候也是您的初始密码");
				dialog.show();
				dialog.setTitleStr("温馨提示");
				dialog.setOkButtonText("确认");
				dialog.setPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent intent = new Intent(IdAuthActivity.this, ShouQuanAcitivity.class);
						IdAuthActivity.this.startActivity(intent);
						dialog.dismiss();
						IdAuthActivity.this.finish();

					}
				}).setNegativeListener(null);

				// finish();
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

	private String returnValue;

	protected final class LoginJavaScriptImpl {
		public void login(String username, String password) throws UnsupportedEncodingException {
			returnValue = username + ":" + password;

			mHandler.post(new Runnable() {
				public void run() {
					try {
						String value = new String(returnValue.getBytes(), "GB2312");
						Log.v("login", value);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@SuppressWarnings("static-access")
	private void authorCodeRequest() {// 获取授权码，启动银联控件
		RequestParams rp = new RequestParams();
		rp.put("certNum", keyUtils.AESEncrypt(PreferencesUtils.getString(this, AppConfig.USER_ID),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		rp.put("name", keyUtils.AESEncrypt(PreferencesUtils.getString(this, AppConfig.USER_NAME),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		refresh.setVisibility(View.GONE);
		initProgressDialog();
		https.get(IdAuthActivity.this, loadingDialog, Urls.IDAUTH_URL, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				dismissProgressDialog();
				if (!StringUtils.isEmpty(response)) {
					LogUtils.i(":response=", response);

					EncryptDataBean eBean = gson.fromJson(response, EncryptDataBean.class);
					if (StringUtils.isEquals(eBean.getState(), Constant.KEY_OVERDUE)) {
						count = keyUtils.RerequestKey(IdAuthActivity.this, 0, loadingDialog, count, "");// 重新请求密钥
						return;

					}
					String aeskey = PreferencesUtils.getString(IdAuthActivity.this, KeyUtils.SERVER_AES_KEY);
					String datajson = KeyUtils.AESdecrypt(eBean.getData(), aeskey);
					LogUtils.i(":AESdecryptResponse=", datajson);

					AuthCodeBean bean = gson.fromJson(datajson, AuthCodeBean.class);
					if (bean.getPostData() != null && !"".equals(bean.getPostData())) {
						layout_data.setVisibility(View.VISIBLE);
						bar.setVisibility(View.VISIBLE);
						LogUtils.i(">>>>", bean.getPostData());
						mWebView.loadDataWithBaseURL(null, bean.getPostData(), "text/html", "utf-8", null);
					} else {
						refresh.setVisibility(View.VISIBLE);
					}
					count = 0;
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				refresh.setVisibility(View.VISIBLE);
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(IdAuthActivity.this,
							IdAuthActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(IdAuthActivity.this,
							IdAuthActivity.this.getString(R.string.network_error));
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					dismissProgressDialog();
				}

				count = 0;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			appExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
