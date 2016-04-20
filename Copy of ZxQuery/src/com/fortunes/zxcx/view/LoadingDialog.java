/** 
 *LoadingDialog.java 
 * classes : com.dld.view.LoadingDialog 
 * @author cdj
 * V 1.0.0 * Create at 2014-8-14 下午4:10:48 
 */
package com.fortunes.zxcx.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.util.StringUtils;

/**
 * 加载框类
 * 
 * @author zwr
 * 
 */
public class LoadingDialog extends Dialog {
	private TextView tip_Tv;
	private ImageView loding_Iv;
	private Context ctx;

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, int style) {
		super(context, style);
		this.ctx = context;
		setContentView(R.layout.dialog_loading);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		loding_Iv = (ImageView) findViewById(R.id.loding_Iv);
		tip_Tv = (TextView) findViewById(R.id.tip_Tv);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		hyperspaceJumpAnimation.setInterpolator(new LinearInterpolator());
		loding_Iv.startAnimation(hyperspaceJumpAnimation);
		setCancelable(false);//
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/**
	 * @Description:[设置加载信息]
	 * @param content
	 */
	public void setText(String content) {
		if (!StringUtils.isBlank(content)) {
			tip_Tv.setText(content);
			tip_Tv.setVisibility(View.VISIBLE);
		}
	}
}
