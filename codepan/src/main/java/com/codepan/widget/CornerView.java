package com.codepan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.codepan.R;

public class CornerView extends View {

	private Paint paint;
	private int color;
	private Path path;

	public CornerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.cornerView);
		color = ta.getColor(R.styleable.cornerView_fillColor, Color.WHITE);
		path = new Path();
		paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		ta.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w = getWidth();
		int h = getHeight();
		path.reset();
		path.moveTo(0, 0);
		path.lineTo(w, 0);
		path.lineTo(0, h);
		path.lineTo(0, 0);
		path.close();
		canvas.drawPath(path, paint);
	}

	public void setColor(int color) {
		this.color = color;
		paint.setColor(color);
		invalidate();
	}
}
