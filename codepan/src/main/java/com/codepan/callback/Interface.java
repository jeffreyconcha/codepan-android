package com.codepan.callback;

import android.location.Location;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;

import java.util.ArrayList;

public class Interface {

	public interface OnCreateDatabaseCallback {
		void onCreateDatabase(SQLiteAdapter db);
	}

	public interface OnUpgradeDatabaseCallback {
		void onUpgradeDatabase(SQLiteAdapter db,
				int oldVersion, int newVersion);
	}

	public interface OnInitializeCallback {
		void onInitialize(SQLiteAdapter db);
	}

	public interface OnCaptureCallback {
		void onCapture(String fileName);
	}

	public interface OnCameraErrorCallback {
		void onCameraError();
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

	public interface OnFragmentCallback {
		void onFragment(boolean isActive, boolean hasBackPressed);
	}

	public interface OnHideKeyboardCallback {
		void onHideKeyboard();
	}

	public interface OnPermissionGrantedCallback {
		void onPermissionGranted(boolean isPermissionGranted);
	}

	public interface OnWheelStopCallback {
		void onWheelStop(float degree);
	}

	public interface OnWheelSpinningCallback {
		void onWheelSpinning(float degree);
	}

	public interface OnRefreshCallback {
		void onRefresh();
	}

	public interface OnBackPressedCallback {
		void onBackPressed();
	}

	public interface OnErrorCallback {
		void onError(String error, String url, String params,
				String response, boolean showError);
	}

	public interface OnSignCallback {
		void onSign(String fileName);
	}

	public interface OnSetIDCallback {
		void onSetID(String id);
	}

	public interface OnSetUpdateCallback {
		void onSetUpdate(boolean isUpdate);
	}

	public interface OnSwapElementCallback {
		void onSwapElement();
	}

	public interface OnTextChangedCallback {
		void onTextChanged(View view, String text);
	}

	public interface OnLocationAverageChangedCallback {
		void onLocationAverageChanged(Location average);
	}

	public interface OnDownloadFileCallback {
		void onProgress(int progress, int max);

		void onError(String error);

		void onComplete();
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

	public interface OnTableCellClickCallback {
		void onTableCellClick(View view, int ri, int mri, int mci);
	}

	public interface OnTableRowClickCallback {
		void onTableCellClick(View view, int ri, int mri);
	}

	public interface OnTableCellCreatedCallback {
		/**
		 * @param view The parent view of the cell
		 * @param ri   The row index of the current page.
		 * @param mri  The map row index or x.
		 * @param mci  The map column index or y.
		 */
		void onTableCellCreated(View view, int ri, int mri, int mci);
	}

	public interface OnTableCellClearCallback {
		void onTableCellClear();
	}

	public interface OnTableColumnClickCallback {
		void onTableColumnClick(int mci, ArrayList<String> distinctList);
	}

	public interface OnTableAddRowCallback {
		void onTableAddRow();
	}

	public interface OnUpdateCellDataCallback {
		void onUpdateCellData(String value);
	}
}
