package com.codepan.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class VerticalImageSpan extends ImageSpan {

	private boolean isCloseable;
	private int start, end;
	private Rect rect;

	public VerticalImageSpan(Context context, Bitmap bitmap) {
		super(context, bitmap);
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			Paint.FontMetricsInt fmi) {
		Drawable drawable = getDrawable();
		Rect rect = drawable.getBounds();
		if(fmi != null) {
			Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
			int fontHeight = metrics.descent - metrics.ascent;
			int drHeight = rect.bottom - rect.top;
			int centerY = metrics.ascent + fontHeight / 2;
			fmi.ascent = centerY - drHeight / 2;
			fmi.top = fmi.ascent;
			fmi.bottom = centerY + drHeight / 2;
			fmi.descent = fmi.bottom;
		}
		return rect.right;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		this.start = start;
		this.end = end;
		Drawable drawable = getDrawable();
		canvas.save();
		Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
		int font = metrics.descent - metrics.ascent;
		int cy = y + metrics.descent - font / 2;
		int ty = cy - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
		canvas.translate(x, ty);
		drawable.draw(canvas);
		canvas.restore();
		if(isCloseable) {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			int right = (int) x + w;
			int left = right - h;
			rect = new Rect();
			rect.top = top;
			rect.bottom = bottom;
			rect.left = left;
			rect.right = right;
		}
	}

	public void setCloseable(boolean isCloseable) {
		this.isCloseable = isCloseable;
	}

	public boolean isCloseable() {
		return this.isCloseable;
	}

	public boolean isInsideCloseableArea(int x, int y) {
		if(rect != null) {
			return rect.contains(x, y);
		}
		return false;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}