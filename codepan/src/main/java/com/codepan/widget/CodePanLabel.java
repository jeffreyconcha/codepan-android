package com.codepan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepan.R;
import com.codepan.cache.TypefaceCache;
import com.codepan.constant.Reference;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;

import java.util.ArrayList;

public class CodePanLabel extends TextView {

	private Drawable backgroundPressed, backgroundEnabled, backgroundDisabled;
	private int textColorPressed, textColorEnabled, textColorDisabled;
	private boolean enableStatePressed, isSquare, isRequired;
	private int reference;

	public CodePanLabel(Context context) {
		super(context);
	}

	public CodePanLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			init(context, attrs);
		}
	}

	public void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.codePan);
		enableStatePressed = ta.getBoolean(R.styleable.codePan_enableStatePressed, false);
		textColorPressed = ta.getColor(R.styleable.codePan_textColorPressed, getCurrentTextColor());
		textColorEnabled = ta.getColor(R.styleable.codePan_textColorEnabled, getCurrentTextColor());
		textColorDisabled = ta.getColor(R.styleable.codePan_textColorDisabled, getCurrentTextColor());
		backgroundPressed = ta.getDrawable(R.styleable.codePan_backgroundPressed);
		backgroundEnabled = ta.getDrawable(R.styleable.codePan_backgroundEnabled);
		backgroundDisabled = ta.getDrawable(R.styleable.codePan_backgroundDisabled);
		isSquare = ta.getBoolean(R.styleable.codePan_setSquare, false);
		isRequired = ta.getBoolean(R.styleable.codePan_setRequired, false);
		reference = ta.getInt(R.styleable.codePan_reference, Reference.MAX);
		String font = ta.getString(R.styleable.codePan_typeface);
		if (font != null) {
			setFont(font);
		}
		setTextColor(isEnabled() ? textColorEnabled : textColorDisabled);
		setBackgroundState(backgroundEnabled);
		enableStatePressed(enableStatePressed);
		setRequired(isRequired);
		ta.recycle();
	}

	public void setBackgroundState(Drawable background) {
		if(background != null) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setBackground(background);
			}
			else {
				setBackgroundDrawable(background);
			}
		}
	}

	public void setRequired(boolean isRequired) {
		if(isRequired) {
			String text = this.getText().toString();
			int length = text.length();
			String name = text + "*";
			ArrayList<SpannableMap> list = new ArrayList<>();
			list.add(new SpannableMap(length, length + 1, Color.RED));
			SpannableStringBuilder ssb = CodePanUtils.customizeText(list, name);
			this.setText(ssb);
		}
		else {
			String text = this.getText().toString().replace("*", "");
			this.setText(text);
		}
	}

	public void setRequiredText(String text) {
		int length = text.length();
		String name = text + "*";
		ArrayList<SpannableMap> list = new ArrayList<>();
		list.add(new SpannableMap(length, length + 1, Color.RED));
		SpannableStringBuilder ssb = CodePanUtils.customizeText(list, name);
		this.setText(ssb);
	}

	public void setParentVisibility(int visibility) {
		ViewGroup parent = (ViewGroup) this.getParent();
		parent.setVisibility(visibility);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(enableStatePressed && isEnabled()) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					setTextColor(textColorPressed);
					setBackgroundState(backgroundPressed);
					break;
				case MotionEvent.ACTION_UP:
					setTextColor(textColorEnabled);
					setBackgroundState(backgroundEnabled);
					break;
			}
			return true;
		}
		else {
			return super.onTouchEvent(event);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled) {
			setTextColor(textColorEnabled);
			setBackgroundState(backgroundEnabled);
		}
		else {
			setTextColor(textColorDisabled);
			setBackgroundState(backgroundDisabled);
		}
	}

	public void enableStatePressed(boolean enableStatePressed) {
		this.enableStatePressed = enableStatePressed;
	}

	public void setTextColorPressed(int color) {
		this.textColorPressed = color;
	}

	public void setTextColorEnabled(int color) {
		this.textColorEnabled = color;
	}

	public void setTextColorDisabled(int color) {
		this.textColorDisabled = color;
	}

	public void setFont(String font) {
		setTypeface(TypefaceCache.get(getContext(), font));
	}

	@Override
	public void setTextSize(float size) {
		setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	public void setPadding(int padding) {
		setPadding(padding, padding, padding, padding);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (isSquare) {
			int width = getMeasuredWidth();
			int height = getMeasuredHeight();
			int dimension = 0;
			switch (reference) {
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
			setMeasuredDimension(dimension, dimension);
		}
	}
}
