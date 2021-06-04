package com.codepan.net;

public class Callback {
	public interface OnDownloadFileCallback {
		void onProgress(int progress, int max);

		void onError(String error);

		void onComplete();
	}
}
