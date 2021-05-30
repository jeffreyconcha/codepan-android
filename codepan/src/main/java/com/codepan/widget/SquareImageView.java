package com.codepan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.codepan.R;
import com.codepan.constant.Reference;

public class SquareImageView extends ImageView {

	private int reference;

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.codePan);
		reference = ta.getInt(R.styleable.codePan_reference, Reference.MAX);
		ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		int dimension = 0;
		switch(reference) {
			case Reference.MAX:
				dimension = Math.max(width, height);
				break;
			case Reference.MIN:
				dimension = Math.min(width, height);
				break;
			case Reference.WIDTH:
				dimension = width;
				break;
			case Reference.HEIGHT:
				dimension = height;
				break;
		}
		int specification = MeasureSpec.makeMeasureSpec(dimension, MeasureSpec.EXACTLY);
		super.onMeasure(specification, specification);
	}
}
