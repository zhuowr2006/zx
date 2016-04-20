package com.fortunes.zxcx.ui;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.base.AppManager;
import com.fortunes.zxcx.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 申请结果界面
 * 
 * @author wgs
 *
 */
public class ResultAcitivity extends BaseActivity implements OnClickListener {

	private Button know, mLeftIconBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentViewWithTop(R.layout.activity_result);
		findViewById();
		init();
		setListener();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		know = (Button) findViewById(R.id.btn_know);
		mLeftIconBtn = (Button) findViewById(R.id.top_bar_left_btn);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		know.setOnClickListener(this);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setTopTitle("提交成功");

		mLeftIconBtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_know:
			AppManager.finishToActivity(getApplicationContext(), MainActivity.class);// 跳转至主界面
			break;

		default:
			break;
		}
	}

}
