package com.codepan.net;

import android.annotation.SuppressLint;

import com.codepan.utils.Console;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
@SuppressLint("CustomX509TrustManager")
class TrustManager implements X509TrustManager {
	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) {
		Console.log(authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) {
		Console.log(authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
}
