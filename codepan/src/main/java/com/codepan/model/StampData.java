package com.codepan.model;

import android.graphics.Paint;

public class StampData {

	public String data;
	public Paint.Align alignment;

	public StampData() {
	}

	public StampData(String data, Paint.Align alignment) {
		this.data = data;
		this.alignment = alignment;
	}
}
