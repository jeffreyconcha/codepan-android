package com.codepan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class BlurBuilder {

	private static final float BITMAP_SCALE = 0.2f;
	private static final float BLUR_RADIUS = 7.5f;

	public static Bitmap blur(View v) {
		try {
			return blur(v.getContext(), CodePanUtils.getScreenshot(v));
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap blur(View v, int width, int height) {
		try {
			return blur(v.getContext(), CodePanUtils.getScreenshot(v, width, height));
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap blur(Context context, Bitmap image) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			int width = Math.round(image.getWidth() * BITMAP_SCALE);
			int height = Math.round(image.getHeight() * BITMAP_SCALE);
			Bitmap in = Bitmap.createScaledBitmap(image, width, height, false);
			Bitmap out = Bitmap.createBitmap(in);
			RenderScript rs = RenderScript.create(context);
			ScriptIntrinsicBlur sib = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			Allocation tmpIn = Allocation.createFromBitmap(rs, in);
			Allocation tmpOut = Allocation.createFromBitmap(rs, out);
			sib.setRadius(BLUR_RADIUS);
			sib.setInput(tmpIn);
			sib.forEach(tmpOut);
			tmpOut.copyTo(out);
			return out;
		}
		return image;
	}
}