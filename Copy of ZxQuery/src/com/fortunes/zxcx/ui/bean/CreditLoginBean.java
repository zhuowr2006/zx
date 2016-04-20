package com.fortunes.zxcx.ui.bean;

import java.io.Serializable;

/**
 * 征信报告登录
 */
@SuppressWarnings("serial")
public class CreditLoginBean extends MsgBean implements Serializable {
	private String pwdChange;
	private String reportStatus;

	public String getPwdChange() {
		return pwdChange;
	}

	public void setPwdChange(String pwdChange) {
		this.pwdChange = pwdChange;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

}
