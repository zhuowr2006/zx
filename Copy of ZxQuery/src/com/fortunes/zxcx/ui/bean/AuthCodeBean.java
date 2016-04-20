package com.fortunes.zxcx.ui.bean;

import java.io.Serializable;

/**
 * 授权码
 */
public class AuthCodeBean implements Serializable {
	private String state;
	private String postData;
	private String msg;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
