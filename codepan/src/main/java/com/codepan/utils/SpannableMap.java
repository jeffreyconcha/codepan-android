package com.codepan.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.widget.TextView;

import com.codepan.cache.TypefaceCache;

public class SpannableMap {

	public enum FontStyle {
		UNDERLINED,
		ITALIC,
	}

	public static final int COLOR = 1;
	public static final int FONT = 2;
	public static final int IMAGE = 3;
	public static final int UNDERLINED = 4;
	public static final int ITALIC = 5;

	public Bitmap bitmap;
	public Context context;
	public Typeface typeface;
	public int type = COLOR;
	public int color;
	public int start;
	public int end;

	public SpannableMap(int start, int end, int color) {
		this.color = color;
		this.start = start;
		this.end = end;
		this.type = COLOR;
	}

	public SpannableMap(int start, int end, Typeface typeface) {
		this.typeface = typeface;
		this.start = start;
		this.end = end;
		this.type = FONT;
	}

	public SpannableMap(TextView label, int start, int end, int resource) {
		this.context = label.getContext();
		Resources res = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, resource);
		float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
		float height = label.getTextSize();
		float width = height * ratio;
		this.bitmap = CodePanUtils.resizeBitmap(bitmap, (int) width, (int) height);
		this.start = start;
		this.end = end;
		this.type = IMAGE;
	}

	public SpannableMap(TextView label, int start, int end, Bitmap bitmap) {
		this.context = label.getContext();
		float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
		float height = label.getTextSize();
		float width = height * ratio;
		//this.bitmap = CodePanUtils.resizeBitmap(bitmap, (int) width, (int) height);
		this.bitmap = bitmap;
		this.start = start;
		this.end = end;
		this.type = IMAGE;
	}

	public SpannableMap(Context context, String typeface, int start, int end) {
		this.typeface = TypefaceCache.get(context.getAssets(), typeface);
		this.context = context;
		this.start = start;
		this.end = end;
		this.type = FONT;
	}

	public SpannableMap(Context context, FontStyle style, int start, int end) {
		this.context = context;
		this.start = start;
		this.end = end;
		switch(style) {
			case ITALIC:
				this.type = ITALIC;
				break;
			case UNDERLINED:
				this.type = UNDERLINED;
				break;
		}
	}
}
