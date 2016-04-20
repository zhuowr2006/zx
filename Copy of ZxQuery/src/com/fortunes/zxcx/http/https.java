package com.fortunes.zxcx.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.content.Context;
import android.os.Environment;

import com.fortunes.zxcx.R;
import com.fortunes.zxcx.util.FileUtil;
import com.fortunes.zxcx.util.LogUtils;
import com.fortunes.zxcx.util.NetUtils;
import com.fortunes.zxcx.util.ToastUtils;
import com.fortunes.zxcx.view.LoadingDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class https {
	private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

	static {
		client.setTimeout(10000); // 设置链接超时，如果不设置，默认为10s
	}

	// 用一个完整url获取一个string对象
	public static void get(Context context, String urlString,
			AsyncHttpResponseHandler res) {
		LogUtils.i("Http","URL="+urlString);
		client.get(context, urlString, res);
	}

	/**
	 * @param 带检查网络的功能
	 * @param loadingDialog
	 * @param urlString
	 * @param params
	 * @param res
	 */
	public static void get(Context context, LoadingDialog loadingDialog,
			String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		try {
			LogUtils.i("Http","URL="
							+ java.net.URLDecoder.decode(client
									.getUrlWithQueryString(true, urlString,
											params), "UTF-8"));
//			System.out.println("URL="
//					+ java.net.URLDecoder.decode(client.getUrlWithQueryString(
//							true, urlString, params), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (NetUtils.isNetConnected(context)) {
			client.get(context, urlString, params, res);
		} else {
			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			ToastUtils.showShortToast(context,
					context.getString(R.string.net_not_connected));
		}

	}

	/**
	 * @param 没有检查网络的功能
	 * @param loadingDialog
	 * @param urlString
	 * @param params
	 * @param res
	 */
	public static void get(Context context, String urlString,
			RequestParams params, AsyncHttpResponseHandler res) {
		try {
			LogUtils.i(
					"Http",
					"URL="
							+ java.net.URLDecoder.decode(client
									.getUrlWithQueryString(true, urlString,
											params), "UTF-8"));
//			System.out.println("URL="
//					+ java.net.URLDecoder.decode(client.getUrlWithQueryString(
//							true, urlString, params), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.get(context, urlString, params, res);

	}

	// 不带参数，获取json对象或者数组
	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);

	}

	// 带参数，获取json对象或者数组
	public static void get(String urlString, RequestParams params,
			JsonHttpResponseHandler res) {

		client.get(urlString, params, res);

	}

	// 下载图片使用，会返回byte数据
	public static void get(String uString, BinaryHttpResponseHandler bHandler) {
		client.get(uString, bHandler);
	}
	// 下载文件使用，会返回byte数据
	public static void get(String uString, AsyncHttpResponseHandler res) {
		client.get(uString, res);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}

	/** * 取消网络请求。 * @param context * @param mayInterruptIfRunning */
	public static void cancelRequests(Context context, boolean mayInterruptIfRunning) {
		if (client != null) {
			client.cancelRequests(context, mayInterruptIfRunning);
		}
	}
//	https.get(urld, new AsyncHttpResponseHandler() {
//
//		
//		@Override
//		public void onSuccess(int statusCode, Header[] headers,
//				byte[] responseBody) {
//			// TODO Auto-generated method stub
//			LogUtils.i(":response=", "请求成功");
//			String tempPath = Environment.getExternalStorageDirectory()
//					.getPath();
//			// TODO Auto-generated method stub
//			// 下载成功后需要做的工作
//			 if (FileUtil.savefileInSdcard("",urld, responseBody)) {
//				 //解压动作
//			}
//		}
//
//		@Override
//		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//				Throwable arg3) {
//			LogUtils.i(":response=", "请求失败");
//		}
//
//		@Override
//		public void onProgress(int bytesWritten, int totalSize) {
//			// TODO Auto-generated method stub
//			super.onProgress(bytesWritten, totalSize);
//			int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
//			// 下载进度显示
//			System.out.println("进度---" + count);
//		}
//
//	});
}
