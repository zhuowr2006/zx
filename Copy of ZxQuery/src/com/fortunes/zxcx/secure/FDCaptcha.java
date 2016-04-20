package com.fortunes.zxcx.secure;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FDCaptcha {
	private static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };

	private static FDCaptcha bmpCode;

	public static FDCaptcha getInstance() {
		if (bmpCode == null) {
			bmpCode = new FDCaptcha();
		}
		return bmpCode;
	}

	private static final int DEFAULT_CODE_LENGTH = 6;
	private static final int DEFAULT_FONT_SIZE = 50;
	private static final int DEFAULT_LINE_NUMBER = 0;
	private static final int BASE_PADDING_LEFT = 5;
	private static final int RANGE_PADDING_LEFT = 20;
	private static final int BASE_PADDING_TOP = 65;
	private static final int RANGE_PADDING_TOP = 10;
	private static final int DEFAULT_WIDTH = 220;
	private static final int DEFAULT_HEIGHT = 110;
	private int width = 220;
	private int height = 110;

	private int base_padding_left = 5;
	private int range_padding_left = 20;
	private int base_padding_top = 65;
	private int range_padding_top = 10;

	private int codeLength = 6;
	private int line_number = 0;
	private int font_size = 50;

	// variables
	private String code;
	private int padding_left, padding_top;
	private Random random = new Random();

	// 验证码图片
	public Bitmap createBitmap() {
		padding_left = 0;

		Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bp);

		code = createCode();

		c.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setTextSize(font_size);

		for (int i = 0; i < code.length(); i++) {
			randomTextStyle(paint);
			randomPadding();
			c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
		}

		for (int i = 0; i < line_number; i++) {
			drawLine(c, paint);
		}

		c.save(Canvas.ALL_SAVE_FLAG);// 保存
		c.restore();//
		return bp;
	}

	public String getCode() {
		return code;
	}

	// 验证码
	private String createCode() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			buffer.append(CHARS[random.nextInt(CHARS.length)]);
		}
		return buffer.toString();
	}

	private void drawLine(Canvas canvas, Paint paint) {
		int color = randomColor();
		int startX = random.nextInt(width);
		int startY = random.nextInt(height);
		int stopX = random.nextInt(width);
		int stopY = random.nextInt(height);
		paint.setStrokeWidth(1);
		paint.setColor(color);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	private int randomColor() {
		return randomColor(1);
	}

	private int randomColor(int rate) {
		int red = random.nextInt(256) / rate;
		int green = random.nextInt(256) / rate;
		int blue = random.nextInt(256) / rate;
		return Color.rgb(red, green, blue);
	}

	private void randomTextStyle(Paint paint) {
		int color = randomColor();
		paint.setColor(color);
//		paint.setFakeBoldText(random.nextBoolean()); // true为粗体，false为非粗体
//		float skewX = random.nextInt(11) / 10;
//		skewX = random.nextBoolean() ? skewX : -skewX;
//		paint.setTextSkewX(true); // float类型参数，负数表示右斜，整数左斜
		paint.setFakeBoldText(true);
		// paint.setUnderlineText(true); //true为下划线，false为非下划线
		// paint.setStrikeThruText(true); //true为删除线，false为非删除线
	}

	private void randomPadding() {
		padding_left += base_padding_left + range_padding_left;
		padding_top = base_padding_top + range_padding_top;
	}
}
