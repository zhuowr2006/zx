package com.fortunes.zxcx.ui.bean;
/**
 * 登录
 */
public class UserBean extends MsgBean {
	private String name;
	private String credNum;
	private String certType;
	private String mobileNum;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCredNum() {
		return credNum;
	}

	public void setCredNum(String credNum) {
		this.credNum = credNum;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
}
