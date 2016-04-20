package com.fortunes.zxcx.ui;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.config.AppConfig;
import com.fortunes.zxcx.http.Urls;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.util.FileUtil;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.PreferencesUtils;
import com.fortunes.zxcx.util.StringUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.fortunes.zxcx.util.ZipUtils;
import com.fortunes.zxcx.view.NotiDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 展示征信报告界面
 * 
 * @author wdd
 * 
 */
public class CheckCreditReportActivity extends BaseActivity implements OnClickListener {
	private WebView webView;

	private Button download, check;
	private LinearLayout check_layout;
	ProgressDialog progressDialog;

	/** 顶部标题栏的左边按钮 */
	private Button mLeftIconBtn;

	static final String mimeType = "text/html";
	static final String encoding = "utf-8";

	private String ReportStatus = "";
	private TextView tv_tips_reportStatus;// 提示语(有无征信报告可下载)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_check_credit_report);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		webView = (WebView) findViewById(R.id.credit_webView);
		// Button
		mLeftIconBtn = (Button) this.findViewById(R.id.top_bar_left_btn);
		download = (Button) findViewById(R.id.check_credit_download);
		check = (Button) findViewById(R.id.check_credit);
		// LinearLayout
		check_layout = (LinearLayout) findViewById(R.id.check_layout);
		// TextView
		tv_tips_reportStatus = (TextView) findViewById(R.id.tv_tips_reportStatus);

	}

	// Web视图
	private class webViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	protected void setListener() {
		mLeftIconBtn.setOnClickListener(this);
		download.setOnClickListener(this);
		check.setOnClickListener(this);
	}

	@Override
	protected void init() {
		setTopTitle("征信报告信息");
		checkReportStatus();
		checkReportInSdcard();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.check_credit_download:// 下载按钮
			downLoadRequest();
			break;

		case R.id.check_credit:// 查看本地报告 按钮

			String tempPath = Environment.getExternalStorageDirectory().getPath();
			try {
				FileUtil.deleteDir(
						tempPath + "/" + PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID)
								+ CheckCreditReportActivity.this.getString(R.string.report_path));
				String htmlurl = ZipUtils.unZipFileCN(tempPath + "/zxbg.zip",
						tempPath + "/" + PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID)
								+ CheckCreditReportActivity.this.getString(R.string.report_path));
				if (StringUtils.isEmpty(htmlurl)) {
//					System.out.println("本地没有征信报告");
					ToastUtils.showShortToast(getApplicationContext(), "本地没有征信报告,请下载后查看");

				} else {
					ToastUtils.showShortToast(getApplicationContext(), "正在为您打开本地保存的征信报告");
					ShowWebView(htmlurl);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.top_bar_left_btn:
			appExit();
			break;
		default:
			break;
		}
	}

	/**
	 * WebView设置
	 * 
	 * @param htmlurl
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public void ShowWebView(final String htmlurl) {
		initProgressDialog();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				// System.out.println(htmlurl);
				check_layout.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				WebSettings webSettings = webView.getSettings();
				// 加载需要显示的网页

				// 设置WebView属性，能够执行Javascript脚本
				webSettings.setJavaScriptEnabled(true);
				// // 设置支持缩放
				webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
				webSettings.setAppCacheEnabled(true);
				webSettings.setSaveFormData(true);
				webSettings.setBuiltInZoomControls(true);
				// webView.loadUrl("file://" + htmlurl);
//				System.out.println(FileUtil.ReadTxtFile(htmlurl));
				webView.loadDataWithBaseURL(null, FileUtil.ReadTxtFile(htmlurl), "text/html", "utf-8", null);
				// String string=FileUtil.ReadTxtFile(htmlurl);
				// webView.loadData(string,"text/html", "UTF-8");
				// 设置Web视图
				webView.setWebViewClient(new webViewClient());
			}
		}, 2000);

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.setProgress(msg.what);
			if (msg.what >= 100) {
				String tempPath = Environment.getExternalStorageDirectory().getPath();
				progressDialog.cancel();
				try {
					// 解压动作
					final String htmlurl = ZipUtils.unZipFileCN(tempPath + "/zxbg.zip",
							tempPath + "/" + PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID)
									+ CheckCreditReportActivity.this.getString(R.string.report_path));
					if (StringUtils.isEmpty(htmlurl)) {
//						System.out.println("本地没有征信报告");
					} else {
						ToastUtils.showShortToast(getApplicationContext(), "征信报告下载完毕，请稍等");
						ShowWebView(htmlurl);

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 下载进度框
	 * 
	 * @param title
	 * @param message
	 */
	public void progress(String title, String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(message);
		progressDialog.setTitle(title);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.show();
	}

	/**
	 * 报告下载状态(服务器有无报告) 根据状态设置下载按钮是否能点击，"0":无报告,"1":有报告
	 * 
	 */
	private void checkReportStatus() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			ReportStatus = bundle.getString("ReportStatus");
			if (StringUtils.isEquals("0", ReportStatus)) {
				download.setEnabled(false);
				tv_tips_reportStatus.setVisibility(View.VISIBLE);// 提示语(有无征信报告可下载)
			} else if (StringUtils.isEquals("1", ReportStatus)) {
				download.setEnabled(true);
				tv_tips_reportStatus.setVisibility(View.GONE);// 提示语(有无征信报告可下载)
			}
		}
	}

	/**
	 * 检测本地是否有征信报告
	 */
	private void checkReportInSdcard() {
		String tempPath = Environment.getExternalStorageDirectory().getPath();
		try {
			FileUtil.deleteDir(tempPath + "/" + PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID)
					+ CheckCreditReportActivity.this.getString(R.string.report_path));
			String htmlurl = ZipUtils.unZipFileCN(tempPath + "/zxbg.zip",
					tempPath + "/" + PreferencesUtils.getString(getApplicationContext(), AppConfig.USER_ID)
							+ CheckCreditReportActivity.this.getString(R.string.report_path));
			if (StringUtils.isEmpty(htmlurl)) {
//				System.out.println("本地没有征信报告");
				check.setVisibility(View.GONE);
			} else {
				download.setVisibility(View.GONE);
				check.setVisibility(View.VISIBLE);
				tv_tips_reportStatus.setVisibility(View.GONE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void downLoadRequest() { // 征信报告下载请求

		progress("报告下载中", "请稍等...");
		RequestParams rp = new RequestParams();
		rp.put("certNum", keyUtils.AESEncrypt(PreferencesUtils.getString(this, AppConfig.USER_ID),
				PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		rp.put("certType", keyUtils.AESEncrypt("0", PreferencesUtils.getString(this, KeyUtils.SERVER_AES_KEY)));
		https.get(this, Urls.DOWNLOAD_REPORT, rp, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// TODO Auto-generated method stub
				LogUtils.i(":response=", "请求成功");

				// TODO Auto-generated method stub
				// 下载成功后需要做的工作
				if (FileUtil.savefileInSdcard("", "zxbg.zip", responseBody)) {
//					System.out.println("下载完成");

				} else {
					ToastUtils.showShortToast(getApplicationContext(), "操作失败");
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				LogUtils.i(":response=", "请求失败");
				if (arg3 instanceof ConnectTimeoutException || arg3 instanceof SocketTimeoutException) {
					ToastUtils.showShortToast(getApplicationContext(),
							CheckCreditReportActivity.this.getString(R.string.network_timeout));
				} else {
					ToastUtils.showShortToast(getApplicationContext(),
							CheckCreditReportActivity.this.getString(R.string.network_error));
				}
				if (progressDialog != null && progressDialog.isShowing()) {

					progressDialog.cancel();
				}

			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				final int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);

//				System.out.println("进度---" + "   count=:" + count + "   bytesWritten=:" + bytesWritten
//						+ "     totalSize=:" + totalSize);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(count);
					}
				}, 1000);

			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {// 返回键操作
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			appExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出操作
	 */
	private void appExit() {

		NotiDialog dialog = new NotiDialog(this, "是否退出查询登录,再次查询需要重新登录");
		dialog.show();
		dialog.setTitleStr("温馨提示");
		dialog.setOkButtonText("确认");
		dialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
			}
		}).setNegativeListener(null);
	}
}
