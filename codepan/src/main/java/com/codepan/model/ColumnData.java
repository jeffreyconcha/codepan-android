package com.codepan.model;

import android.content.Context;
import android.content.res.Resources;

public class ColumnData {

	public int width, cellResId, headerResId;
	public boolean isFilterEnabled;
	public String ID, value;
	private Object tag;

	public ColumnData(String ID, String value, int width) {
		this.ID = ID;
		this.value = value;
		this.width = width;
	}

	public ColumnData(String value, int width) {
		this.value = value;
		this.width = width;
	}

	public ColumnData(Context context, String ID, String value, int width) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
	}

	public ColumnData(Context context, String value, int width) {
		Resources res = context.getResources();
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
	}

	public ColumnData(Context context, String value, int width, int headerResId, boolean isFilterEnabled) {
		Resources res = context.getResources();
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
		this.headerResId = headerResId;
		this.isFilterEnabled = isFilterEnabled;
	}

	public ColumnData(Context context, String ID, String value, int width, int cellResId) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.cellResId = cellResId;
		this.width = res.getDimensionPixelSize(width);
	}

	public ColumnData(Context context, String value, int width, int cellResId) {
		Resources res = context.getResources();
		this.value = value;
		this.cellResId = cellResId;
		this.width = res.getDimensionPixelSize(width);
	}

	public ColumnData(String ID, String value, Object tag, int width) {
		this.ID = ID;
		this.value = value;
		this.width = width;
		this.tag = tag;
	}

	public ColumnData(String value, Object tag, int width) {
		this.value = value;
		this.width = width;
		this.tag = tag;
	}

	public ColumnData(Context context, String ID, String value, Object tag, int width) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
	}

	public ColumnData(Context context, String ID, String value, Object tag, int width, boolean isFilterEnabled) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
		this.isFilterEnabled = isFilterEnabled;
	}

	public ColumnData(Context context, String value, Object tag, int width) {
		Resources res = context.getResources();
		this.value = value;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
	}

	public ColumnData(Context context, String ID, String value, Object tag, int width, int cellResId) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.cellResId = cellResId;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
	}

	public ColumnData(Context context, String ID, String value, Object tag,
			int width, int cellResId, boolean isFilterEnabled) {
		Resources res = context.getResources();
		this.ID = ID;
		this.value = value;
		this.cellResId = cellResId;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
		this.isFilterEnabled = isFilterEnabled;
	}

	public ColumnData(Context context, String value, Object tag, int width, int cellResId) {
		Resources res = context.getResources();
		this.value = value;
		this.cellResId = cellResId;
		this.width = res.getDimensionPixelSize(width);
		this.tag = tag;
	}

	public Object getTag() {
		return tag;
	}
}
