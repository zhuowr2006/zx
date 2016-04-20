package com.fortunes.zxcx.ui;

import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.BaseActivity;
import com.fortunes.zxcx.keys.KeyUtils;
import com.fortunes.zxcx.secure.LoginActivity;
import com.fortunes.zxcx.util.ActivityTools;
import com.fortunes.zxcx.util.PreferencesUtils;

/**
 * 开始界面
 * 
 * @author zwr
 * 
 */
public class StartActivity extends BaseActivity implements OnClickListener {
	private Button loginBtn, registerBtn;
	private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_start);
		findViewById();
		init();
		setListener();
		System.out.println("kxxxx");
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		loginBtn = (Button) findViewById(R.id.login_btn);
		registerBtn = (Button) findViewById(R.id.register_btn);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		loginBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		PreferencesUtils.clearUserData(getApplicationContext());// 每次进入都清除数据
		KeyUtils.cleardata(this);// 清除缓存的key数据
		map = KeyUtils.initKey(this);// 创建公私钥
		String publickey = map.get(KeyUtils.PUBLIC_KEY);
		String privatekey = map.get(KeyUtils.PRIVATE_KEY);
		keyUtils.RequestKey(0,publickey, privatekey, "", this, loadingDialog);// 请求aes密钥
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_btn:
			ActivityTools.skipActivity(this, LoginActivity.class);
			break;
		case R.id.register_btn:
			ActivityTools.skipActivity(this, PhoneVerifyActivity.class);
			break;
		}
	}

}
