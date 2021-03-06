package com.codepan.widget.signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SignatureView extends SurfaceView implements OnTouchListener, Callback {

	private final List<List<Dot>> dots = new ArrayList<>();
	public Paint paint;

	public SignatureView(Context context) {
		super(context);
		if(!isInEditMode()) {
			init();
		}
	}

	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) {
			init();
		}
	}

	private void init() {
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(3);
		this.setOnTouchListener(this);
		this.getHolder().addCallback(this);
	}

	public void setStrokeWidth(float width) {
		paint.setStrokeWidth(width);
		this.invalidate();
	}

	public void setColor(int color) {
		paint.setColor(color);
		this.invalidate();
	}

	public void clear() {
		dots.clear();
		this.invalidate();
	}

	private static class Dot {
		public float X = 0;
		public float Y = 0;

		public Dot(float x, float y) {
			X = x;
			Y = y;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dots.add(new ArrayList<>());
				dots.get(dots.size() - 1).add(new Dot(event.getX(), event.getY()));
				this.invalidate();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_MOVE:
				dots.get(dots.size() - 1).add(new Dot(event.getX(), event.getY()));
				this.invalidate();
				break;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for(List<Dot> dots : this.dots) {
			for(int i = 0; i < dots.size(); i++) {
				if(i - 1 == -1)
					continue;
				canvas.drawLine(dots.get(i - 1).X, dots.get(i - 1).Y, dots.get(i).X, dots.get(i).Y, paint);
			}
		}
	}

	public Bitmap getBitmap(int width, int height) {
		Bitmap src = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(src);
		this.draw(canvas);
		return Bitmap.createScaledBitmap(src, width, height, true);
	}

	public boolean exportFile(String folder, String fileName, int width, int height) {
		invalidate();
		boolean result = false;
		String path = getContext().getDir(folder, Context.MODE_PRIVATE).getPath();
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		try {
			File file = new File(dir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			setBackgroundColor(Color.WHITE);
			Bitmap bitmap = getBitmap(width, height);
			result = bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			if(result) {
				File signature = new File(dir, fileName);
				result = signature.exists() && signature.length() > 0;
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isEmpty() {
		return dots.isEmpty();
	}

	public void setBitmap(Bitmap bitmap) {
		Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);
		this.draw(canvas);
	}
}