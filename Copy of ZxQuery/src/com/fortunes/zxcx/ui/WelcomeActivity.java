package com.fortunes.zxcx.ui;

import java.util.Map;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 欢迎界面
 * 
 * @author zwr
 * 
 */
public class WelcomeActivity extends BaseActivity {
	protected static final String TAG = WelcomeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
		startActivity();
	}

	private void startActivity() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this,
						StartActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		}, 2000);

	}

	@Override
	protected void findViewById() {

	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void init() {
	}

}
