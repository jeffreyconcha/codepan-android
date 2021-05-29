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
	private Paint paint, stroke, edge;
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
		int width = strokeThickness + edgeThickness;
		stroke.setStrokeWidth(width);
		stroke.setStyle(Paint.Style.STROKE);
		edge = new Paint();
		edge.setColor(color);
		edge.setAntiAlias(true);
		edge.setStrokeWidth(edgeThickness);
		edge.setStyle(Paint.Style.STROKE);
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
		path.addCircle(cx, cy, r, Path.Direction.CW);
		path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
		canvas.drawCircle(cx, cy, r - edgeThickness, stroke);
		canvas.drawCircle(cx, cy, r, edge);
		canvas.drawPath(path, paint);
		canvas.clipPath(path);
		canvas.drawColor(color);
	}
}
