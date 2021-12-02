package com.codepan.callback;

import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;

public class Interface {

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnCameraChangeCallback {
		void onCameraChange(boolean isOpen);
	}

	public interface OnSingleTapCallback {
		void onSingleTap();
	}

	public interface OnDoubleTapCallback {
		void onDoubleTap();
	}

	public interface OnItemClickCallback {
		void onItemClick(int position, View view, ViewGroup parent);
	}

	public interface OnItemLongClickCallback {
		void onItemLongClick(int position, View view, ViewGroup parent);
	}

	public interface OnKeyboardDismissCallback {
		void onKeyboardDismiss();
	}

	public interface OnPhotoZoomCallback {
		void onPhotoZoom(boolean isZoom);
	}

	public interface OnHideKeyboardCallback {
		void onHideKeyboard();
	}

	public interface OnRefreshCallback {
		void onRefresh();
	}

	public interface OnCancelCallback {
		void onCancel();
	}

	public interface OnBackPressedCallback {
		void onBackPressed();
	}

	public interface OnErrorCallback {
		void onError(String error, String url, String params,
				String response, boolean showError);
	}

	public interface OnSetIDCallback {
		void onSetID(String id);
	}

	public interface OnSetUpdateCallback {
		void onSetUpdate(boolean isUpdate);
	}

	public interface OnTextChangedCallback {
		void onTextChanged(View view, String text);
	}

	public interface OnTouchCallback {
		void onActionDown();
		void onActionUp();
	}

	public interface OnProgressCallback {
		void onProgress(int progress, int max);
	}

	public interface OnCompletionCallback {
		void onCompletion();
	}

	public interface OnResultCallback {
		void onResult(boolean result, String error);
	}

	public interface OnScrollChangeCallback {
		void onScrollChanged(int l, int t, int ol, int ot);
	}
}
