package com.codepan.widget.table;

import android.view.View;

import java.util.ArrayList;

public class Callback {

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
