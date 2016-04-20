package com.fortunes.zxcx.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.AppManager;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.secure.CreditLoginActivity;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.view.NotiDialog;

/**
 * 主界面
 * 
 * @author wdd
 * 
 */
public class MainActivity extends BaseActivity implements
		OnClickListener {

	private Button credit_authentication, credit_query;
	/** 顶部标题栏的左边按钮 */
	private Button mLeftIconBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_credit_report);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		credit_authentication = (Button) this
				.findViewById(R.id.credit_authentication);
		credit_query = (Button) this.findViewById(R.id.credit_query);
		mLeftIconBtn = (Button) this.findViewById(R.id.top_bar_left_btn);
	}

	@Override
	protected void setListener() {
		credit_authentication.setOnClickListener(this);
		credit_query.setOnClickListener(this);
	}

	@Override
	protected void init() {
		setTopTitle("信用查询");
		mLeftIconBtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.credit_authentication:
			ActivityTools.skipActivity(MainActivity.this,
					IdAuthActivity.class);
			break;
		case R.id.credit_query:
			ActivityTools.skipActivity(MainActivity.this,
					CreditLoginActivity.class);
			break;
		}
	}

	// 退出程序
	private void appExit() {
		final NotiDialog dialog = new NotiDialog(this, "是否退出应用程序");
		dialog.show();
		dialog.setTitleStr("温馨提示");
		dialog.setOkButtonText("确认");
		dialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				mhandler.sendEmptyMessage(0);
			}
		}).setNegativeListener(null);
	}

	Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 注销并清除状态
//			finish();
			AppManager.finishAllActivity();
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			appExit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
