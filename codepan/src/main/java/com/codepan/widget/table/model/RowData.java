package com.codepan.widget.table.model;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class RowData {
	public final ArrayList<CellData> cellList;
	public final String ID;

	public RowData(int rowID, @NonNull ArrayList<CellData> cellList) {
		this.ID = String.valueOf(rowID);
		this.cellList = cellList;
	}

	public RowData(@NonNull String rowID, @NonNull ArrayList<CellData> cellList) {
		this.ID = rowID;
		this.cellList = cellList;
	}

	public CellData get(int index) {
		return this.cellList.get(index);
	}

	public void set(int index, CellData data) {
		this.cellList.set(index, data);
	}

	public boolean isEmpty() {
		return cellList.isEmpty();
	}
}
