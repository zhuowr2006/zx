package com.fortunes.zxcx.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.http.https;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.view.LoadingDialog;
import com.google.gson.Gson;

/**
 * @ClassName: BaseActivity
 * @Description: Activity基类
 * @author zwr
 */
public abstract class BaseActivity extends FragmentActivity {
	public static final String TAG = BaseActivity.class.getSimpleName();
	/**
	 * 屏幕的宽度和高度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected LoadingDialog loadingDialog;
	/** 顶部标题栏的左边按钮 */
	private Button mLeftIconBtn;
	/** 显示标题 */
	private TextView toptext;
	private LinearLayout layout;
	protected LinearLayout add_top;// 头部布局
	protected Gson gson;
	protected KeyUtils keyUtils;
	protected int count = 0;// 重复请求密钥次数

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/**
		 * 获取屏幕宽度和高度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		add_top = (LinearLayout) findViewById(R.id.add_top);
		gson = new Gson();
		keyUtils = new KeyUtils();
	}

	// 子类使用这个加载布局文件，可继承一个标题栏
	protected void setContentViewWithTop(int layoutResID) {
		initView();
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(layoutResID, null);
		view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		layout.addView(view);
	}

	/**
	 * 初始化按钮
	 * 
	 * @param id
	 *            按钮的资源ID
	 * @param l
	 *            按钮点击事件的监听器
	 * @return
	 */
	protected Button initButton(int id, View.OnClickListener l) {
		View v = findViewById(id);
		if (v != null) {
			v.setOnClickListener(l);
			return (Button) v;
		}

		return null;
	}

	/**
	 * 初始化标题栏的View
	 */
	private void initView() {
		layout = (LinearLayout) findViewById(R.id.mainlayout);
		mLeftIconBtn = initButton(R.id.top_bar_left_btn, mClickListener);
		toptext = (TextView) findViewById(R.id.top_bar_titleTv);
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			if (tag instanceof Runnable) {
				runOnUiThread((Runnable) tag);
				return;
			}

			// so many ids and no annotation, for which?
			switch (v.getId()) {
			case R.id.top_bar_left_btn:
				onLeftIconButtonClick(mLeftIconBtn);
				break;
			}
		}
	};

	/**
	 * 顶部标题栏左边按钮点击执行
	 * 
	 * @param left
	 */
	protected void onLeftIconButtonClick(Button left) {
		/** 隐藏软键盘 **/
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		finish();
	}
	/**
	 * 隐藏标题栏左边的按钮
	 */
	public void hideLeftIconButton() {
		mLeftIconBtn.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示标题栏左边的按钮
	 */
	public void showLeftIconButton() {
		mLeftIconBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置顶部标题栏显示的标题内容
	 * 
	 * @param s
	 */
	public void setTopTitle(CharSequence s) {
		if (toptext != null) {
			toptext.setText(s);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		AppManager.getAppManager().addActivity(this);
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// 结束Activity&从堆栈中移除
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
		https.cancelRequests(this, true);
	}

	/** 绑定界面UI **/
	protected abstract void findViewById();

	/** 界面UI事件监听 **/
	protected abstract void setListener();

	/** 界面数据初始化 **/
	protected abstract void init();

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener, String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message, int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener, String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).show();
		return alertDialog;
	}

	public void initProgressDialog() {
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
			loadingDialog.setText("加载中...");
		}
		if (!isFinishing() && !loadingDialog.isShowing()) {
			loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
			loadingDialog.setText("加载中...");
			loadingDialog.show();
		}
		loadingDialog.setCanceledOnTouchOutside(true);
	}

	public void dismissProgressDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
}
