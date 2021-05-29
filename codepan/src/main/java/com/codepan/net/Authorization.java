package com.codepan.net;

import android.util.Base64;
public class Authorization {

	public static final String BASIC_AUTH = "Basic Auth";
	public static final String BEARER = "Bearer";

	private String type;
	private String key;
	private String token;

	public String getAuthorization() {
		String authorization = null;
		String value;
		if(type != null) {
			if(type.equals(BASIC_AUTH)) {
				String basic = key + ":" + token;
				byte[] b = basic.getBytes();
				value = new String(Base64.encode(b, Base64.NO_WRAP));
			}
			else {
				value = token;
			}
			authorization = type + " " + value;
		}
		return authorization;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
