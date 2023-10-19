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

public class InverseCircleView extends View {

	private int color, strokeColor, strokeThickness, edgeThickness;
	private Paint paint, stroke;
	private Path path;

	public InverseCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.inverseCircle);
		strokeThickness = ta.getInteger(R.styleable.inverseCircle_strokeThickness, 0);
		strokeColor = ta.getColor(R.styleable.inverseCircle_strokeColor, Color.TRANSPARENT);
		edgeThickness = ta.getInteger(R.styleable.inverseCircle_edgeThickness, 0);
		color = ta.getColor(R.styleable.inverseCircle_color, Color.WHITE);
		path = new Path();
		paint = new Paint();
		paint.setColor(Color.TRANSPARENT);
		paint.setAntiAlias(true);
		stroke = new Paint();
		stroke.setColor(strokeColor);
		stroke.setAntiAlias(true);
		stroke.setStrokeWidth(strokeThickness);
		stroke.setStyle(Paint.Style.STROKE);
		ta.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
		int r = w / 2;
		path.reset();
		path.addCircle(cx, cy, r - edgeThickness, Path.Direction.CW);
		path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
		canvas.drawCircle(cx, cy, r - edgeThickness, stroke);
		canvas.drawPath(path, paint);
		canvas.clipPath(path);
		canvas.drawColor(color);
	}

	public void setColor(int color) {
		this.color = color;
		invalidate();
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
		stroke.setColor(strokeColor);
		invalidate();
	}
}
