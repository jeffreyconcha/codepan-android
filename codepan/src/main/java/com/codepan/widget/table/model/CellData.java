package com.codepan.widget.table.model;


import com.codepan.widget.table.Callback.OnTableCellClearCallback;
import com.codepan.widget.table.Callback.OnUpdateCellDataCallback;

public class CellData {

	private OnTableCellClearCallback tableCellClearCallback;
	private OnUpdateCellDataCallback updateCellDataCallback;
	public String ID, rowID, name, value, webDetailID;
	public ColumnData column;

	private boolean withChanges;
	private Object tag, obj;

	public CellData() {
	}

	public CellData(CellData clone) {
		this.tag = clone.tag;
		this.name = clone.name;
		this.value = clone.value;
		this.rowID = clone.rowID;
		this.column = clone.column;
		this.withChanges = clone.withChanges;
		this.tableCellClearCallback = clone.tableCellClearCallback;
	}

	public CellData(String name) {
		this.name = name;
	}

	public CellData(String rowID, String name) {
		this.rowID = rowID;
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setNameValue(String input) {
		this.name = input;
		this.value = input;
	}

	public void setNameValue(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public void clearValue() {
		this.value = null;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return tag;
	}

	public void clear() {
		this.name = null;
		this.value = null;
		this.tag = null;
		if(tableCellClearCallback != null) {
			tableCellClearCallback.onTableCellClear();
		}
	}

	public void setOnTableCellClearCallback(OnTableCellClearCallback tableCellClearCallback) {
		this.tableCellClearCallback = tableCellClearCallback;
	}

	public void setOnUpdateCellDataCallback(OnUpdateCellDataCallback updateCellDataCallback) {
		this.updateCellDataCallback = updateCellDataCallback;
	}

	public boolean withChanges() {
		return withChanges;
	}

	public void setWithChanges(boolean withChanges) {
		this.withChanges = withChanges;
	}

	public void updateData(String value) {
		if(updateCellDataCallback != null) {
			updateCellDataCallback.onUpdateCellData(value);
		}
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
}
